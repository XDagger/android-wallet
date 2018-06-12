/* pool and miner logic o_O, T13.744-T13.836 $DVS:time$ */

#ifndef XDAG_POOL_H
#define XDAG_POOL_H

#include <stdio.h>
#include <pthread.h>
#include "block.h"
#include "hash.h"

#ifdef __cplusplus
extern "C" {
#endif

#define XDAG_POOL_N_CONFIRMATIONS  16

struct xdag_pool_task {
	struct xdag_field task[2], lastfield, minhash, nonce;
	xdag_time_t main_time;
	void *ctx0, *ctx;
};

/*connecting the miner to pool,and transfer blocks*/
extern int xdag_start_wallet_mainthread(const char *pool_arg);

/* send block to network via pool */
extern int xdag_send_block_via_pool(struct xdag_block *b);

extern struct xdag_pool_task g_xdag_pool_task[2];
extern uint64_t g_xdag_pool_ntask;
extern xdag_hash_t g_xdag_mined_hashes[XDAG_POOL_N_CONFIRMATIONS],
						g_xdag_mined_nonce[XDAG_POOL_N_CONFIRMATIONS];
/* a number of mining threads */
extern int g_xdag_mining_threads;
/* poiter to mutex for optimal share */
extern void *g_ptr_share_mutex;

extern void xdag_pool_uninit();


#ifdef __cplusplus
}
#endif

#endif
