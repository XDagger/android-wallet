/* cheatcoin main, T13.654-T13.895 $DVS:time$ */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <pthread.h>
#include <signal.h>
#include <math.h>
#include <ctype.h>
#include <sys/stat.h>
#if !defined(_WIN32) && !defined(_WIN64)
#include <poll.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <errno.h>
#elif defined(_WIN64)
#define poll WSAPoll
#else
#define poll(a, b, c) ((a)->revents = (a)->events, (b))
#endif
#include "system.h"
#include "address.h"
#include "block.h"
#include "crypt.h"
#include "log.h"
#include "transport.h"
#include "version.h"
#include "wallet.h"
#include "netdb.h"
#include "xdagmain.h"
#include "sync.h"
#include "pool.h"
#include "memory.h"
#include "../wrapper/xdagwrapper.h"

//macro defines
#define coinname                "xdag"
#define XDAG_COMMAND_MAX		0x1000
#define UNIX_SOCK               "unix_sock.dat"
#define XFER_MAX_IN             11
#define Nfields(d)				(2 + d->nfields + 3 * d->nkeys + 2 * d->outsig)

//inline structs
struct account_callback_data {
	FILE *  out;
	int count;
};

struct out_balances_data {
    struct xdag_field *blocks;
    unsigned nblocks, maxnblocks;
};

struct xfer_callback_data {
	struct xdag_field fields[XFER_MAX_IN + 1];
	int keys[XFER_MAX_IN + 1];
	xdag_amount_t todo, done, remains;
	int nfields, nkeys, outsig;
};

//global variables
const char *g_coinname = "xdag";
const char *g_progname = "xdagwallet";

//global statistical variable
int g_xdag_run = 0;
int g_xdag_state = XDAG_STATE_NINT;
int g_xdag_testnet = 0;
int g_is_miner = 0;
static int g_is_pool = 0;
time_t g_xdag_xfer_last = 0;
pthread_mutex_t g_update_ui_mutext =  PTHREAD_MUTEX_INITIALIZER;

struct xdag_stats g_xdag_stats;
struct xdag_ext_stats g_xdag_extstats;

//callbacks
int (*g_xdag_show_state)(const char *state, const char *balance, const char *address) = 0;

static long double amount2cheatcoins(xdag_amount_t amount)
{
	return xdag_amount2xdag(amount) + (long double)xdag_amount2cheato(amount) / 1000000000;
}

static xdag_amount_t cheatcoins2amount(const char *str)
{
    long double sum, flr;
    xdag_amount_t res;

    if (sscanf(str, "%Lf", &sum) != 1 || sum <= 0)
        return 0;

    flr = floorl(sum);
    res = (xdag_amount_t)flr << 32;
    sum -= flr;
    sum = ldexpl(sum, 32);
    flr = ceill(sum);

    return res + (xdag_amount_t)flr;
}

static long double diff2log(xdag_diff_t diff)
{
	long double res = (long double)xdag_diff_to64(diff);

	xdag_diff_shr32(&diff);
	xdag_diff_shr32(&diff);
	if (xdag_diff_to64(diff))
		res += ldexpl((long double)xdag_diff_to64(diff), 64);
	
	return res > 0 ? logl(res) : 0;
}

static long double hashrate(xdag_diff_t *diff)
{
	long double sum = 0;
	int i;

	for (i = 0; i < HASHRATE_LAST_MAX_TIME; ++i) {
		sum += diff2log(diff[i]);
	}
	
	sum /= HASHRATE_LAST_MAX_TIME;
	
	return ldexpl(expl(sum), -58);
}

static int make_block(struct xfer_callback_data *d)
{
    int res;

    if (d->nfields != XFER_MAX_IN)
        memcpy(d->fields + d->nfields, d->fields + XFER_MAX_IN, sizeof(xdag_hashlow_t));

    d->fields[d->nfields].amount = d->todo;
    res = xdag_create_block(d->fields, d->nfields, 1, 0, 0);
    if (res) {
        xdag_err("FAILED: to %s xfer %.9Lf %s, error %d",
                      xdag_hash2address(d->fields[d->nfields].hash), amount2cheatcoins(d->todo), coinname, res);
        return -1;
    }

    d->done += d->todo;
    d->todo = 0;
    d->nfields = 0;
    d->nkeys = 0;
    d->outsig = 1;

    return 0;
}

int xdag_set_password_callback(int (*callback)(const char *prompt, char *buf, unsigned size))
{
    return xdag_user_crypt_action((uint32_t*)(void*)callback, 0, 0, 6);
}

static int account_callback(void *data, xdag_hash_t hash, xdag_amount_t amount, xdag_time_t time, int n_our_key)
{
	struct account_callback_data *d = (struct account_callback_data *)data;

	if (!d->count--) return -1;
	
	if (g_xdag_state < XDAG_STATE_XFER)
		fprintf(d->out, "%s  key %d\n", xdag_hash2address(hash), n_our_key);
	else
		fprintf(d->out, "%s %20.9Lf  key %d\n", xdag_hash2address(hash), amount2cheatcoins(amount), n_our_key);

    return 0;;
}

static int xfer_coin_callback(void *data, xdag_hash_t hash, xdag_amount_t amount, xdag_time_t time, int n_our_key)
{
    struct xfer_callback_data *d = (struct xfer_callback_data *)data;
    xdag_amount_t todo = d->remains;
    int i;

    if (!amount)
        return -1;
    if (g_is_pool && xdag_main_time() < (time >> 16) + 2 * XDAG_POOL_N_CONFIRMATIONS)
        return 0;

    for (i = 0; i < d->nkeys; ++i) {
        if (n_our_key == d->keys[i])
            break;
    }

    if (i == d->nkeys) d->keys[d->nkeys++] = n_our_key;
    if (d->keys[XFER_MAX_IN] == n_our_key) d->outsig = 0;

    if (Nfields(d) > XDAG_BLOCK_FIELDS) {
        if (make_block(d)) return -1;
        d->keys[d->nkeys++] = n_our_key;
        if (d->keys[XFER_MAX_IN] == n_our_key)
            d->outsig = 0;
    }

    if (amount < todo)
        todo = amount;

    memcpy(d->fields + d->nfields, hash, sizeof(xdag_hashlow_t));
    d->fields[d->nfields++].amount = todo;
    d->todo += todo, d->remains -= todo;
    xdag_log_xfer(hash, d->fields[XFER_MAX_IN].hash, todo);

    if (!d->remains || Nfields(d) == XDAG_BLOCK_FIELDS) {
        if (make_block(d))
            return -1;
        if (!d->remains)
            return 1;
    }

    return 0;
}

static int out_balances_callback(void *data, xdag_hash_t hash, xdag_amount_t amount, xdag_time_t time)
{
    struct out_balances_data *d = (struct out_balances_data *)data;
    struct xdag_field f;

    memcpy(f.hash, hash, sizeof(xdag_hashlow_t));

    f.amount = amount;
    if (!f.amount)
        return 0;
    if (d->nblocks == d->maxnblocks) {
        d->maxnblocks = (d->maxnblocks ? d->maxnblocks * 2 : 0x100000);
        d->blocks = realloc(d->blocks, d->maxnblocks * sizeof(struct xdag_field));
    }

    memcpy(d->blocks + d->nblocks, &f, sizeof(struct xdag_field));
    d->nblocks++;

    return 0;
}

static int out_sort_callback(const void *l, const void *r)
{
    return strcmp(xdag_hash2address(((struct xdag_field *)l)->data),
                  xdag_hash2address(((struct xdag_field *)r)->data));
}

static void *add_block_callback(void *block, void *data)
{
    unsigned *i = (unsigned*)data;

    xdag_add_block((struct xdag_block *)block);

    if (!(++*i % 10000)) printf("blocks: %u\n", *i);

    return 0;
}

void xdag_log_xfer(xdag_hash_t from, xdag_hash_t to, xdag_amount_t amount)
{
	xdag_mess("Xfer  : from %s to %s xfer %.9Lf %s",
				   xdag_hash2address(from), xdag_hash2address(to), amount2cheatcoins(amount), coinname);
}

static const char *get_state(void)
{
	static const char *states[] = {
#define xdag_state(n, s) s,
#include "state.h"
#undef xdag_state
	};

	return states[g_xdag_state];
}

int xdag_xfer_coin(const char* amount,const char* address){

    uint32_t pwd[4];
    struct xfer_callback_data xfer;

    memset(&xfer, 0, sizeof(xfer));
    xfer.remains = cheatcoins2amount(amount);

    if (!xfer.remains) {
        report_ui_xfer_event(en_event_nothing_transfer,"nothing to transfer");
        return 1;
    }

    if (xfer.remains > xdag_get_balance(0)) {
        report_ui_xfer_event(en_event_balance_too_small,"balance too small");
        return 1;
    }

    if (xdag_address2hash(address, xfer.fields[XFER_MAX_IN].hash)) {
        report_ui_xfer_event(en_event_invalid_recv_address,"incorrect address");
        return 1;
    }

    /* ask user type in password */
    if (xdag_user_crypt_action(0, 0, 0, 3)) {
        //sleep(3);
        report_ui_xfer_event(en_event_pwd_error,"password error");
        return 1;
    }

    xdag_wallet_default_key(&xfer.keys[XFER_MAX_IN]);
    xfer.outsig = 1;
    g_xdag_state = XDAG_STATE_XFER;
    g_xdag_xfer_last = time(0);
    xdag_traverse_our_blocks(&xfer, &xfer_coin_callback);

    char err_msg[MAX_XDAG_ERR_MSG_LEN] = {0};
    sprintf(err_msg, "transferred : %.9Lf %s to the address: %s",
        amount2cheatcoins(xfer.done), coinname, xdag_hash2address(xfer.fields[XFER_MAX_IN].hash));

    report_ui_xfer_event(en_event_xdag_transfered,err_msg);

    return 0;
}
#if 1
static int xdag_command(char *cmd, FILE *out)
{
	uint32_t pwd[4];
	char *lasts;
	int ispwd = 0;

	cmd = strtok_r(cmd, " \t\r\n", &lasts);
	
	if (!cmd) return 0;
	
	if (sscanf(cmd, "pwd=%8x%8x%8x%8x", pwd, pwd + 1, pwd + 2, pwd + 3) == 4) {
		ispwd = 1;
		cmd = strtok_r(0, " \t\r\n", &lasts);
	}

	if (!strcmp(cmd, "account")) {
		struct account_callback_data d;

		d.out = out;
		d.count = (g_is_miner ? 1 : 20);

		cmd = strtok_r(0, " \t\r\n", &lasts);
		if (cmd) {
			sscanf(cmd, "%d", &d.count);
		}
		
		if (g_xdag_state < XDAG_STATE_XFER) {
			fprintf(out, "Not ready to show balances. Type 'state' command to see the reason.\n");
		}

		xdag_traverse_our_blocks(&d, &account_callback);
	} else if (!strcmp(cmd, "balance")) {
		if (g_xdag_state < XDAG_STATE_XFER) {
			fprintf(out, "Not ready to show a balance. Type 'state' command to see the reason.\n");
		} else {
			xdag_hash_t hash;
			xdag_amount_t balance;

			cmd = strtok_r(0, " \t\r\n", &lasts);
			if (cmd) {
				xdag_address2hash(cmd, hash);
				balance = xdag_get_balance(hash);
			} else {
				balance = xdag_get_balance(0);
			}

			fprintf(out, "Balance: %.9Lf %s\n", amount2cheatcoins(balance), coinname);
		}
    }else {
		fprintf(out, "Illegal command.\n");
	}
	return 0;
}
#endif

static int out_balances(void)
{
	struct out_balances_data d;
	unsigned i = 0;

	xdag_set_log_level(0);
	
	xdag_mem_init((xdag_main_time() - xdag_start_main_time()) << 17);
	xdag_crypt_init(0);
	memset(&d, 0, sizeof(struct out_balances_data));
    xdag_app_debug("Out Balances Loading blocks from local storage...");
	xdag_load_blocks(xdag_start_main_time() << 16, xdag_main_time() << 16, &i, add_block_callback);
	xdag_traverse_all_blocks(&d, out_balances_callback);
	qsort(d.blocks, d.nblocks, sizeof(struct xdag_field), out_sort_callback);

	for (i = 0; i < d.nblocks; ++i) {
		printf("%s  %20.9Lf\n", xdag_hash2address(d.blocks[i].data), amount2cheatcoins(d.blocks[i].amount));
	}
	
	return 0;
}
/* init global variable and callback */
void xdag_global_init(){
    g_xdag_run = 1;
    g_is_miner = 1;
    g_is_pool = 0;
    g_xdag_state = XDAG_STATE_INIT;
    g_xdag_testnet = 0;
}

int xdag_main(const char *pool_arg)
{
    const char *addrports[256], *bindto = 0, *pubaddr = 0, *miner_address = 0;
    char *ptr;
    int transport_flags = 0, n_addrports = 0, n_mining_threads = 0, is_pool = 0, is_miner = 0, i, level;
    pthread_t th;

//ignore some linux signals to avoid terminate
#if !defined(_WIN32) && !defined(_WIN64)
	signal(SIGHUP, SIG_IGN);
	signal(SIGPIPE, SIG_IGN);
	signal(SIGWINCH, SIG_IGN);
	signal(SIGINT, SIG_IGN);
	signal(SIGTERM, SIG_IGN);
#endif

    xdag_show_state(0);

    if (pubaddr && !bindto) {
        char str[64], *p = strchr(pubaddr, ':');
        if (p) {
            sprintf(str, "0.0.0.0%s", p); bindto = strdup(str);
        }
    }

    xdag_app_debug("xdag initialize g_xdag_last_received is %x ",g_xdag_last_received);

    memset(&g_xdag_stats, 0, sizeof(g_xdag_stats));
    memset(&g_xdag_extstats, 0, sizeof(g_xdag_extstats));

    xdag_app_debug("Starting %s, version %s", g_progname, XDAG_VERSION);

    xdag_app_debug("Starting synchonization engine...");
    if (xdag_sync_init()){
        xdag_app_err(" xdag sync init error ");
        return -1;
    }

    xdag_app_debug("Starting dnet transport...");
    xdag_app_debug("Transport module: ");
    if (xdag_transport_start(transport_flags, bindto, n_addrports, addrports)){
        xdag_app_err(" xdag transport start error ");
        return -1;
    }

    xdag_app_debug("Initializing log system...");
//    if (xdag_signal_init()){
//        xdag_app_err(" xdag signal init error ");
//        return -1;
//    }

    xdag_app_debug("Initializing cryptography...");
    if (xdag_crypt_init(1)){
        xdag_app_err(" xdag signal init error ");
        return -1;
    }

    xdag_app_debug("Reading wallet...");
    if (xdag_wallet_init()){
        xdag_app_err(" xdag wallet init error ");
        return -1;
    }

    xdag_app_debug("Initializing addresses...");
    if (xdag_address_init()){
        xdag_app_err(" xdag address init error ");
        return -1;
    }

    xdag_app_debug("Starting blocks engine...");
    if (xdag_blocks_start(0, -1)){
        xdag_app_err(" xdag start regular block thread error ");
        return -1;
    }

    xdag_app_debug("Starting pool engine...");
    if (xdag_start_wallet_mainthread(pool_arg)){
        xdag_app_err(" xdag start wallet main thread error ");
        return -1;
    }

    return 0;
}

void xdag_show_state(xdag_hash_t hash)
{
    char balance[64] , address[64], state[256];
    en_balance_load_state balance_state = en_balance_ready;
    en_address_load_state address_state = en_address_ready;

    memset(balance,0,64);
    memset(address,0,64);
    memset(state,0,256);

    if (!g_app_callback_func)
        return;

    if (g_xdag_state < XDAG_STATE_XFER){
        balance_state = en_balance_not_ready;
        strcpy(balance, "Not ready");
    }else{
        sprintf(balance, "%.9Lf", amount2cheatcoins(xdag_get_balance(0)));
    }

    if (g_xdag_state < XDAG_STATE_NINT || !hash){
        address_state = en_address_not_ready;
        strcpy(address, "Not ready");
    }else{
        strcpy(address, xdag_hash2address(hash));
    }

    strcpy(state, get_state());

    st_xdag_event event;
    memset(&event,0,sizeof(st_xdag_event));
    event.procedure_type = en_procedure_init_wallet;
    event.event_type = en_event_update_state;
    event.xdag_program_state = g_xdag_state;
    event.xdag_balance_state = balance_state;
    event.xdag_address_state = address_state;

    strcpy(event.address,address);
    strcpy(event.balance,balance);
    strcpy(event.state,state);

    //xdag_app_debug(" update ui address %s balance %s state %s ",event.address,event.balance,event.state);

    g_app_callback_func(g_callback_object,&event);
}

void xdag_uninit(){
    xdag_block_uninit();
    xdag_pool_uninit();
    g_xdag_state = XDAG_STATE_NINT;
    xdag_wallet_uninit();
    xdag_netdb_uninit();
    xdag_storage_uninit();
    xdag_mem_uninit();
    xdag_transport_stop();

    //reinit ui if needed
    xdag_show_state(0);
}
