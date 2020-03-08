/* basic variables, T13.714-T13.895 $DVS:time$ */

#ifndef XDAG_MAIN_H
#define XDAG_MAIN_H

#include <time.h>
#include "block.h"

#ifdef __cplusplus
extern "C" {
#endif

enum xdag_states {
#define xdag_state(n, s) XDAG_STATE_ ## n,
#include "state.h"
#undef xdag_state
};

/* the maximum period of time for which blocks are requested, not their amounts */
#define REQUEST_BLOCKS_MAX_TIME (1 << 20)

extern struct xdag_stats {
	xdag_diff_t difficulty, max_difficulty;
	uint64_t nblocks, total_nblocks;
	uint64_t nmain, total_nmain;
	uint32_t nhosts, total_nhosts, reserved1, reserved2;
} g_xdag_stats;

#define HASHRATE_LAST_MAX_TIME  (64 * 4)

extern struct xdag_ext_stats {
	xdag_diff_t hashrate_total[HASHRATE_LAST_MAX_TIME];
	xdag_diff_t hashrate_ours[HASHRATE_LAST_MAX_TIME];
	xdag_time_t hashrate_last_time;
	uint64_t nnoref;
	uint64_t nhashes;
	double hashrate_s;
	uint32_t nwaitsync;
} g_xdag_extstats;

extern void xdag_log_xfer(xdag_hash_t from, xdag_hash_t to, xdag_amount_t amount);

/* the program state */
extern int g_xdag_state;

/* is there command 'run' */
extern int g_xdag_run;

/* if set to '1', the program works in a test network */
extern int g_xdag_testnet;

/* coin token and program name */
extern const char *g_coinname, *g_progname;

/* time of last transfer */
extern xdag_time_t g_xdag_xfer_last;

extern void xdag_global_init();

extern int xdag_main(const char *pool_arg);

extern int xdag_set_password_callback(int (*callback)(const char *prompt, char *buf, unsigned size));

extern void xdag_show_state(xdag_hash_t hash);

extern int (*g_xdag_show_state)(const char *state, const char *balance, const char *address);

extern int xdag_xfer_coin(const char* amount,const char* address,const char* remark);

extern void xdag_uninit();

#define xdag_amount2xdag(amount) ((unsigned)((amount) >> 32))
#define xdag_amount2cheato(amount) ((unsigned)(((uint64_t)(unsigned)(amount) * 1000000000) >> 32))

#ifdef __cplusplus
}
#endif

#endif
