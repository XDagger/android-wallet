/* пул и майнер, T13.744-T13.895 $DVS:time$ */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <fcntl.h>
#include <errno.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#if defined(_WIN32) || defined(_WIN64)
#if defined(_WIN64)
#define poll WSAPoll
#else
#define poll(a, b, c) ((a)->revents = (a)->events, (b))
#endif
#else
#include <poll.h>
#endif
#include "system.h"
#include "../dus/dfslib_crypt.h"
#include "../dus/dfslib_string.h"
#include "../dus/crc.h"
#include "address.h"
#include "block.h"
#include "xdagmain.h"
#include "pool.h"
#include "storage.h"
#include "sync.h"
#include "transport.h"
#include "wallet.h"
#include "log.h"

#define N_MINERS        4096
#define START_N_MINERS  256
#define START_N_MINERS_IP 8
#define N_CONFIRMATIONS XDAG_POOL_N_CONFIRMATIONS
#define MINERS_PWD      "minersgonnamine"
#define SECTOR0_BASE    0x1947f3acu
#define SECTOR0_OFFSET  0x82e9d1b5u
#define HEADER_WORD     0x3fca9e2bu
#define DATA_SIZE       (sizeof(struct xdag_field) / sizeof(uint32_t))
#define SEND_PERIOD     10                                  /* период в секундах, с которым майнер посылает пулу результаты */
#define FUND_ADDRESS    "FQglVQtb60vQv2DOWEUL7yh3smtj7g1s"  /* адрес фонда сообщества */

enum miner_state {
	MINER_BLOCK     = 1,
	MINER_ARCHIVE   = 2,
	MINER_FREE      = 4,
	MINER_BALANCE   = 8,
	MINER_ADDRESS   = 0x10,
};

struct miner {
	double maxdiff[N_CONFIRMATIONS];
	struct xdag_field id;
	uint32_t data[DATA_SIZE];
	double prev_diff;
	xdag_time_t main_time;
	uint64_t nfield_in;
	uint64_t nfield_out;
	uint64_t ntask;
	struct xdag_block *block;
	uint32_t ip;
	uint32_t prev_diff_count;
	uint16_t port;
	uint16_t state;
	uint8_t data_size;
	uint8_t block_size;
};

struct xdag_pool_task g_xdag_pool_task[2];
uint64_t g_xdag_pool_ntask = 0;
/* a number of mining threads */
int g_xdag_mining_threads = 0;
xdag_hash_t g_xdag_mined_hashes[N_CONFIRMATIONS], g_xdag_mined_nonce[N_CONFIRMATIONS];

/* 1 - program works as a pool */
static int g_xdag_pool = 0;

static int g_max_nminers = START_N_MINERS, g_max_nminers_ip = START_N_MINERS_IP, g_nminers = 0, g_socket = -1,
	g_stop_mining = 1, g_stop_general_mining = 1;
static struct miner *g_miners, g_local_miner, g_fund_miner;
static struct pollfd *g_fds;
static struct dfslib_crypt *g_crypt;
static struct xdag_block *g_firstb = 0, *g_lastb = 0;
static pthread_mutex_t g_pool_mutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_mutex_t g_share_mutex = PTHREAD_MUTEX_INITIALIZER;
static const char *g_miner_address;
/* poiter to mutex for optimal share  */
void *g_ptr_share_mutex = &g_share_mutex;
/* for pool thread safe quit */
static int g_is_pool_thread_run = 0;
static pthread_cond_t g_pool_cancel_cond = PTHREAD_COND_INITIALIZER;
static pthread_mutex_t g_pool_cancel_mutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_t g_pool_thread_t;

#define diff2pay(d, n) ((n) ? exp((d) / (n) - 20) * (n) : 0)

static void report_ui_pool_event(en_xdag_event_type event_type,const char* err_msg){

    st_xdag_event event;
    memset(&event,0,sizeof(st_xdag_event));
    event.event_type = event_type;
    event.procedure_type = en_procedure_pool_thread;

    if(err_msg)
        strncpy(event.error_msg,err_msg,strlen(err_msg));

    g_app_callback_func(g_callback_object,&event);
}

static int send_to_pool(struct xdag_field *fld, int nfld)
{
	struct xdag_field f[XDAG_BLOCK_FIELDS];
	xdag_hash_t h;
	struct miner *m = &g_local_miner;
	int i, res, todo = nfld * sizeof(struct xdag_field), done = 0;

	if (g_socket < 0) {
		pthread_mutex_unlock(&g_pool_mutex);
		return -1;
	}

	memcpy(f, fld, todo);

	if (nfld == XDAG_BLOCK_FIELDS) {
		f[0].transport_header = 0;
		
		xdag_hash(f, sizeof(struct xdag_block), h);
		
		f[0].transport_header = HEADER_WORD;
		
		uint32_t crc = crc_of_array((uint8_t*)f, sizeof(struct xdag_block));
		
		f[0].transport_header |= (uint64_t)crc << 32;
	}

	for (i = 0; i < nfld; ++i) {
		dfslib_encrypt_array(g_crypt, (uint32_t*)(f + i), DATA_SIZE, m->nfield_out++);
	}

	while (todo) {
		struct pollfd p;
		
		p.fd = g_socket;
		p.events = POLLOUT;
		
		if (!poll(&p, 1, 1000)) continue;
		
		if (p.revents & (POLLHUP | POLLERR)) {
			pthread_mutex_unlock(&g_pool_mutex);
			return -1;
		}

		if (!(p.revents & POLLOUT)) continue;
		
		res = write(g_socket, (uint8_t*)f + done, todo);
		if (res <= 0) {
			pthread_mutex_unlock(&g_pool_mutex);
            return -1;
		}
        done += res, todo -= res;
	}
    xdag_app_debug("send block to the pool alread send  %d  fields",done);
	pthread_mutex_unlock(&g_pool_mutex);
	
	if (nfld == XDAG_BLOCK_FIELDS) {
                xdag_app_debug("Sent  : %016llx%016llx%016llx%016llx t=%llx res=%d",
					   h[3], h[2], h[1], h[0], fld[0].time, 0);
	}

	return 0;
}

/* send block to network via pool */
int xdag_send_block_via_pool(struct xdag_block *b)
{
	if (g_socket < 0) return -1;
	
	pthread_mutex_lock(&g_pool_mutex);
	
	return send_to_pool(b->field, XDAG_BLOCK_FIELDS);
}

static void miner_net_thread_cleanup(void*arg){

    xdag_debug("call miner net thread clean up");
    g_is_pool_thread_run = 0;

    pthread_mutex_unlock(&g_pool_mutex);
    pthread_mutex_unlock(&g_share_mutex);

    pthread_mutex_lock(&g_pool_cancel_mutex);

    //global virables init
    g_miner_address = NULL;
    g_firstb = NULL;
    g_lastb = NULL;
    g_crypt = NULL;
    g_max_nminers = START_N_MINERS;
    g_max_nminers_ip = START_N_MINERS_IP;
    g_nminers = 0;
    close(g_socket);
    g_socket = -1;
    g_stop_mining = 1;
    g_stop_general_mining = 1;
    g_xdag_pool = 0;
    g_xdag_mining_threads = 0;
    g_xdag_pool_ntask = 0;

    //free some resource
    if(g_xdag_pool_task[0].ctx){
        free(g_xdag_pool_task[0].ctx);
        g_xdag_pool_task[0].ctx = NULL;
    }
    if(g_xdag_pool_task[1].ctx){
        free(g_xdag_pool_task[1].ctx);
        g_xdag_pool_task[1].ctx = NULL;
    }

    pthread_cond_signal(&g_pool_cancel_cond);

    xdag_debug(" pool state cancel signaled");

    pthread_mutex_unlock(&g_pool_cancel_mutex);

    xdag_debug("call xdag clean up finished");
}

static void *miner_net_thread(void *arg)
{
    int oldcancelstate;
    int oldcanceltype;
    struct xdag_block b;
    struct xdag_field data[2];
    xdag_hash_t hash;
    const char *str = (const char*)arg;
    char buf[0x100];
    const char *mess, *mess1 = "";
    struct sockaddr_in peeraddr;
    char *lasts;
    int res = 0, reuseaddr = 1;
    struct linger linger_opt = { 1, 0 }; // Linger active, timeout 0
    xdag_time_t t;
    struct miner *m = &g_local_miner;
    time_t t00, t0, tt;
    int ndata, maxndata;

    pthread_setcancelstate(PTHREAD_CANCEL_ENABLE,&oldcancelstate);
    pthread_setcanceltype(PTHREAD_CANCEL_DEFERRED,&oldcanceltype);
    pthread_cleanup_push(miner_net_thread_cleanup,(void*)mess);

    g_is_pool_thread_run = 1;
    while (!g_xdag_sync_on) {
        pthread_iscancel(pthread_self());
        sleep(1);
    }

    pthread_iscancel(pthread_self());

    ndata = 0;
    maxndata = sizeof(struct xdag_field);
    t0 = t00 = 0;
    m->nfield_in = m->nfield_out = 0;

    if (xdag_get_our_block(hash)) {
        mess = "can't create a block";
        report_ui_pool_event(en_event_cannot_create_block,mess);
        pthread_exit((void*)NULL);
    }

    int64_t pos = xdag_get_block_pos(hash, &t);

    if (pos < 0) {
        mess = "can't find the block";
        report_ui_pool_event(en_event_cannot_find_block,mess);
        pthread_exit((void*)NULL);
    }

    struct xdag_block *blk = xdag_storage_load(hash, t, pos, &b);
    if (!blk) {
        mess = "can't load the block";
        report_ui_pool_event(en_event_cannot_load_block,mess);
        pthread_exit((void*)NULL);
    }
    if (blk != &b) memcpy(&b, blk, sizeof(struct xdag_block));

    pthread_mutex_lock(&g_pool_mutex);
    // Create a socket
    g_socket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (g_socket == INVALID_SOCKET) {
        pthread_mutex_unlock(&g_pool_mutex);
        mess = "cannot create a socket";
        report_ui_pool_event(en_event_cannot_create_socket,mess);
        pthread_exit((void*)NULL);
    }
    if (fcntl(g_socket, F_SETFD, FD_CLOEXEC) == -1) {
        xdag_app_err("pool  : can't set FD_CLOEXEC flag on socket %d, %s\n", g_socket, strerror(errno));
    }

    // Fill in the address of server
    memset(&peeraddr, 0, sizeof(peeraddr));
    peeraddr.sin_family = AF_INET;

    // Resolve the server address (convert from symbolic name to IP number)
    strcpy(buf, str);
    const char *s = strtok_r(buf, " \t\r\n:", &lasts);
    if (!s) {
        pthread_mutex_unlock(&g_pool_mutex);
        mess = "host is not given";
        report_ui_pool_event(en_event_host_is_not_given,mess);
        pthread_exit((void*)NULL);
    }
    if (!strcmp(s, "any")) {
        peeraddr.sin_addr.s_addr = htonl(INADDR_ANY);
    } else if (!inet_aton(s, &peeraddr.sin_addr)) {
        struct hostent *host = gethostbyname(s);
        if (!host || !host->h_addr_list[0]) {
            pthread_mutex_unlock(&g_pool_mutex);
            mess = "cannot resolve host ", mess1 = s;
            report_ui_pool_event(en_event_cannot_reslove_host,mess);
            res = h_errno;
            pthread_exit((void*)NULL);
        }
        // Write resolved IP address of a server to the address structure
        memmove(&peeraddr.sin_addr.s_addr, host->h_addr_list[0], 4);
    }

    // Resolve port
    s = strtok_r(0, " \t\r\n:", &lasts);
    if (!s) {
        pthread_mutex_unlock(&g_pool_mutex);
        mess = "port is not given";
        report_ui_pool_event(en_event_port_is_not_given,mess);
        pthread_exit((void*)NULL);
    }
    peeraddr.sin_port = htons(atoi(s));

    // Set the "LINGER" timeout to zero, to close the listen socket
    // immediately at program termination.
    setsockopt(g_socket, SOL_SOCKET, SO_LINGER, (char*)&linger_opt, sizeof(linger_opt));
    setsockopt(g_socket, SOL_SOCKET, SO_REUSEADDR, (char*)&reuseaddr, sizeof(int));

    // Now, connect to a pool
    xdag_app_debug(" trying connected to the pool %s",str);
    res = connect(g_socket, (struct sockaddr*)&peeraddr, sizeof(peeraddr));
    if (res) {
        pthread_mutex_unlock(&g_pool_mutex);
        mess = "cannot connect to the pool";
        report_ui_pool_event(en_event_cannot_connect_to_pool,mess);
        pthread_exit((void*)NULL);
    }
    xdag_app_debug("already connected to the pool %s",str);

    if (send_to_pool(b.field, XDAG_BLOCK_FIELDS) < 0) {
        mess = "socket is closed";
        report_ui_pool_event(en_event_socket_isclosed,mess);
        pthread_exit((void*)NULL);
    }

    for (;;) {
        struct pollfd p;

        pthread_iscancel(g_pool_thread_t);
        pthread_mutex_lock(&g_pool_mutex);

        if (g_socket < 0) {
            pthread_mutex_unlock(&g_pool_mutex);
            mess = "socket is closed";
            report_ui_pool_event(en_event_socket_isclosed,mess);
            pthread_exit((void*)NULL);
        }

        p.fd = g_socket;
        tt = time(0);
        p.events = POLLIN | (tt - t0 >= SEND_PERIOD && tt - t00 <= 64 ? POLLOUT : 0);

        if (!poll(&p, 1, 0)) {
            pthread_mutex_unlock(&g_pool_mutex);
            sleep(1);
            continue;
        }

        if (p.revents & POLLHUP) {
            pthread_mutex_unlock(&g_pool_mutex);
            mess = "socket hangup";
            report_ui_pool_event(en_event_socket_hangup,mess);
            pthread_exit((void*)NULL);
        }

        if (p.revents & POLLERR) {
            pthread_mutex_unlock(&g_pool_mutex);
            mess = "socket error";
            report_ui_pool_event(en_event_socket_error,mess);
            pthread_exit((void*)NULL);
        }

        if (p.revents & POLLIN) {
            res = read(g_socket, (uint8_t*)data + ndata, maxndata - ndata);
            if (res < 0) {
                pthread_mutex_unlock(&g_pool_mutex);
                mess = "read error on socket";
                report_ui_pool_event(en_event_read_socket_error,mess);
                pthread_exit((void*)NULL);
            }
            ndata += res;
            xdag_app_debug("receive block from pool ndata %d",ndata);
            if (ndata == maxndata) {
                struct xdag_field *last = data + (ndata / sizeof(struct xdag_field) - 1);

                dfslib_uncrypt_array(g_crypt, (uint32_t*)last->data, DATA_SIZE, m->nfield_in++);

                if (!memcmp(last->data, hash, sizeof(xdag_hashlow_t))) {
                    xdag_set_balance(hash, last->amount);

                    xdag_app_debug("xdag last received block time %d",tt);
                    g_xdag_last_received = tt;
                    xdag_app_debug("xdag set g_xdag_last_received to %d",g_xdag_last_received);

                    ndata = 0;

                    maxndata = sizeof(struct xdag_field);
                } else if (maxndata == 2 * sizeof(struct xdag_field)) {
                    uint64_t ntask = g_xdag_pool_ntask + 1;
                    struct xdag_pool_task *task = &g_xdag_pool_task[ntask & 1];

                    task->main_time = xdag_main_time();
                    xdag_hash_set_state(task->ctx, data[0].data,
                                                                     sizeof(struct xdag_block) - 2 * sizeof(struct xdag_field));
                    xdag_hash_update(task->ctx, data[1].data, sizeof(struct xdag_field));
                    xdag_hash_update(task->ctx, hash, sizeof(xdag_hashlow_t));

                    xdag_generate_random_array(task->nonce.data, sizeof(xdag_hash_t));

                    memcpy(task->nonce.data, hash, sizeof(xdag_hashlow_t));
                    memcpy(task->lastfield.data, task->nonce.data, sizeof(xdag_hash_t));

                    xdag_hash_final(task->ctx, &task->nonce.amount, sizeof(uint64_t), task->minhash.data);

                    g_xdag_pool_ntask = ntask;
                    t00 = time(0);

                    xdag_app_debug("Task  : t=%llx N=%llu", task->main_time << 16 | 0xffff, ntask);

                    ndata = 0;
                    maxndata = sizeof(struct xdag_field);
                } else {
                    maxndata = 2 * sizeof(struct xdag_field);
                }
            }
        }

        if (p.revents & POLLOUT) {
            uint64_t ntask = g_xdag_pool_ntask;
            struct xdag_pool_task *task = &g_xdag_pool_task[ntask & 1];
            uint64_t *h = task->minhash.data;

            t0 = time(0);
            res = send_to_pool(&task->lastfield, 1);

            xdag_app_debug("Share : %016llx%016llx%016llx%016llx t=%llx res=%d",
                                       h[3], h[2], h[1], h[0], task->main_time << 16 | 0xffff, res);

            if (res) {
                mess = "write error on socket";
                report_ui_pool_event(en_event_write_socket_error,mess);
                pthread_exit((void*)NULL);
            }
        } else {
            pthread_mutex_unlock(&g_pool_mutex);
        }

        pthread_iscancel(pthread_self());
    }

    pthread_cleanup_pop(0);

    return 0;
}

static int crypt_start(void)
{
	struct dfslib_string str;
	uint32_t sector0[128];
	int i;

	g_crypt = malloc(sizeof(struct dfslib_crypt));
	if (!g_crypt) return -1;
	dfslib_crypt_set_password(g_crypt, dfslib_utf8_string(&str, MINERS_PWD, strlen(MINERS_PWD)));

	for (i = 0; i < 128; ++i) {
		sector0[i] = SECTOR0_BASE + i * SECTOR0_OFFSET;
	}

	for (i = 0; i < 128; ++i) {
		dfslib_crypt_set_sector0(g_crypt, sector0);
		dfslib_encrypt_sector(g_crypt, sector0, SECTOR0_BASE + i * SECTOR0_OFFSET);
	}

	return 0;
}

/*
	connecting the miner to pool,and transfer blocks
*/
int xdag_start_wallet_mainthread(const char *pool_arg)
{
    //pthread_t th;
	int i, res;

	g_xdag_pool = 0;
	g_miner_address = 0;

	for (i = 0; i < 2; ++i) {
		g_xdag_pool_task[i].ctx0 = malloc(xdag_hash_ctx_size());
		g_xdag_pool_task[i].ctx = malloc(xdag_hash_ctx_size());
		
		if (!g_xdag_pool_task[i].ctx0 || !g_xdag_pool_task[i].ctx) return -1;
	}

	if (crypt_start()) return -1;

	memset(&g_local_miner, 0, sizeof(struct miner));
	memset(&g_fund_miner, 0, sizeof(struct miner));

    res = pthread_create(&g_pool_thread_t, 0, miner_net_thread, (void*)pool_arg);
	if (res) {
		xdag_app_err(" miner_net_thread create failed \n");
		return -1;
	}

    pthread_detach(g_pool_thread_t);
		
	return 0;
}

static int print_miner(FILE *out, int n, struct miner *m)
{
	double sum = m->prev_diff;
	int count = m->prev_diff_count;
	char buf[32], buf2[64];
	uint32_t i = m->ip;
	int j;

	for (j = 0; j < N_CONFIRMATIONS; ++j) {
		if (m->maxdiff[j] > 0) {
			sum += m->maxdiff[j]; count++;
		}
	}
	
	sprintf(buf, "%u.%u.%u.%u:%u", i & 0xff, i >> 8 & 0xff, i >> 16 & 0xff, i >> 24 & 0xff, ntohs(m->port));
	sprintf(buf2, "%llu/%llu", (unsigned long long)m->nfield_in * sizeof(struct xdag_field),
			(unsigned long long)m->nfield_out * sizeof(struct xdag_field));
	fprintf(out, "%3d. %s  %s  %-21s  %-16s  %lf\n", n, xdag_hash2address(m->id.data),
			(m->state & MINER_FREE ? "free   " : (m->state & MINER_ARCHIVE ? "archive" :
												  (m->state & MINER_ADDRESS ? "active " : "badaddr"))), buf, buf2, diff2pay(sum, count));
	
	return m->state & (MINER_FREE | MINER_ARCHIVE) ? 0 : 1;
}

/* output to the file a list of miners */
int xdag_print_miners(FILE *out)
{
    int i, res;

    fprintf(out, "List of miners:\n"
            " NN  Address for payment to            Status   IP and port            in/out bytes      nopaid shares\n"
            "------------------------------------------------------------------------------------------------------\n");
    res = print_miner(out, -1, &g_local_miner);

    for (i = 0; i < g_nminers; ++i) {
        res += print_miner(out, i, g_miners + i);
    }

    fprintf(out,
            "------------------------------------------------------------------------------------------------------\n"
            "Total %d active miners.\n", res);

    return res;
}
void xdag_pool_uninit(){
    //if pool thread is run wait the thread quit and
    //release resource in miner_net_thread_cleanup
    if(g_is_pool_thread_run){
        g_is_pool_thread_run = 0;
        pthread_mutex_lock(&g_pool_cancel_mutex);
        pthread_cond_init(&g_pool_cancel_cond,NULL);
        pthread_cancel(g_pool_thread_t);
        pthread_cond_wait(&g_pool_cancel_cond,&g_pool_cancel_mutex);
        pthread_mutex_unlock(&g_pool_cancel_mutex);
    }
}
