/* dnet: main file; T11.231-T13.789; $DVS:time$ */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#if !defined(_WIN32) && !defined(_WIN64)
#include <signal.h>
#include <unistd.h>
#include <sys/wait.h>
#include <xdaglib/client/log.h>

#endif
#include "dnet_crypt.h"
#include "dnet_database.h"
#include "dnet_history.h"
#include "dnet_connection.h"
#include "dnet_threads.h"
#include "dnet_log.h"
#include "dnet_command.h"
#include "dnet_main.h"

extern int getdtablesize(void);

#ifdef __LDuS__
#include <ldus/system/kernel.h>
static void catcher(int signum) {
	ldus_block_signal(0, signum);	/* заблокировать его, чтобы самому повторно не получить */
	ldus_kill_task(0, signum);	/* передать сигнал дальше */
}
#endif

int dnet_init() {
    struct dnet_thread *thread_watchdog,*thread_collector;
    int i = 0, err = 0, res;
    const char *mess = 0;

    xdag_app_debug("dnet init start");
    if (system_init() || dnet_threads_init() || dnet_hosts_init()) {
        xdag_app_err("dnet init error \n");
        return err;
    }

    xdag_app_debug("dnet crypt init start");
    if ((err = dnet_crypt_init(DNET_VERSION))) {
        xdag_app_err("dnet crypto init error\n");
        return err;
    }

    return err;
}

void dnet_uninit(){
    //TODO: kill some thread release some resource
    dnet_crypt_uninit();
}
