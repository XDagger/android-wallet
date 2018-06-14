/* logging, T13.670-T13.895 $DVS:time$ */

#include <stdio.h>
#include <stdint.h>
#include <stdarg.h>
#include <pthread.h>
#include <sys/time.h>
#include <string.h>
#include "system.h"
#include "log.h"
#include "xdagmain.h"

#define XDAG_LOG_FILE "%s.log"
#define XDAG_APP_LOG_BUF_SIZE 4096
static pthread_mutex_t log_mutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_mutex_t app_log_mutex = PTHREAD_MUTEX_INITIALIZER;
static int log_level = XDAG_INFO;
static en_xdag_app_log_level app_log_level = en_xdag_info;

#if !defined(_WIN32) && !defined(_WIN64)
#define filename(x) strrchr(x,'/')?strrchr(x,'/')+1:x
#endif
#define filename(x) strrchr(x,'\\')?strrchr(x,'\\')+1:x

int xdag_log(int level, const char *format, ...)
{
	static const char lvl[] = "NONEFATACRITINTEERROWARNMESSINFODBUGTRAC";
	char tbuf[64], buf[64];
	struct tm tm;
	va_list arg;
	struct timeval tv;
	FILE *f;
	int done;
	time_t t;

	if (level < 0 || level > XDAG_TRACE) {
		level = XDAG_INTERNAL;
	}

	if (level > log_level) {
		return 0;
	}

	gettimeofday(&tv, 0);
	t = tv.tv_sec;
	localtime_r(&t, &tm);
	strftime(tbuf, 64, "%Y-%m-%d %H:%M:%S", &tm);
	pthread_mutex_lock(&log_mutex);
	sprintf(buf, XDAG_LOG_FILE, g_progname);
	
	f = fopen(buf, "a");
	if (!f) {
		done = -1; goto end;
	}
	
	fprintf(f, "%s.%03d [%012llx:%.4s]  ", tbuf, (int)(tv.tv_usec / 1000), (long long)pthread_self_ptr(), lvl + 4 * level);

	va_start(arg, format);
	done = vfprintf(f, format, arg);
	va_end(arg);

	fprintf(f, "\n");
	fclose(f);

 end:
	pthread_mutex_unlock(&log_mutex);

	return done;
}

extern char *xdag_log_array(const void *arr, unsigned size)
{
	static int k = 0;
	static char buf[4][0x1000];
	char *res = &buf[k++ & 3][0];
	unsigned i;

	for (i = 0; i < size; ++i) {
		sprintf(res + 3 * i - !!i, "%s%02x", (i ? ":" : ""), ((uint8_t*)arr)[i]);
	}

	return res;
}

/* sets the maximum error level for output to the log, returns the previous level (0 - do not log anything, 9 - all) */
extern int xdag_set_log_level(int level)
{
	int level0 = log_level;

	if (level >= 0 && level <= XDAG_TRACE) {
		log_level = level;
	}

	return level0;
}

char* app_level_str(en_xdag_app_log_level level){
    switch (level) {
        case en_xdag_no_error:
            return "noerror";
        case en_xdag_fatal:
            return "fatal";
        case en_xdag_critical:
            return "critical";
        case en_xdag_internal:
            return "internal";
        case en_xdag_error:
            return "error";
        case en_xdag_warning:
            return "warning";
        case en_xdag_message:
            return "message";
        case en_xdag_info:
            return "info";
        case en_xdag_debug:
            return "debug";
        case en_xdag_trace:
            return "trace";
        default:
            return "info";
    }

    return "info";
}

void xdag_app_log(en_xdag_app_log_level level,const char* file,int line,const char* format,...){

    int pos = 0;
    int done;
    char tbuf[64];
    struct tm tm;
    va_list arg;
    struct timeval tv;
    time_t t;

    if(app_log_level > level)
        return;

    //get time in tbuf time and log level is unnecessary for android
	//since android's log utils already printed them
#if !defined(ANDROID) || !defined(__ANDROID__)
    gettimeofday(&tv, 0);
    t = tv.tv_sec;
    localtime_r(&t, &tm);
    strftime(tbuf, 64, "%Y-%m-%d %H:%M:%S", &tm);
#endif
    //printf level, file, line ,time,threadid
    pthread_mutex_lock(&app_log_mutex);

    st_xdag_event event;
	memset(&event,0,sizeof(st_xdag_event));
    event.event_type = en_event_xdag_log_print;
    event.log_level = level;


    //debug warning error critical fatal will print time,file,line,thread id
    if(level == en_xdag_debug ||
        level == en_xdag_warning ||
        level == en_xdag_error||
        level == en_xdag_critical ||
        level == en_xdag_fatal){

#if !defined(ANDROID) || !defined(__ANDROID__)
        pos += sprintf(event.app_log_msg,"[%s][%s][%s][%d][%lu]",
                       app_level_str(level),tbuf,filename(file),line,pthread_self());
#else
		pos += sprintf(event.app_log_msg,"[%s][%d][%lu]",filename(file),line,pthread_self());
#endif
    }

    //printf log msg
    va_start(arg, format);
    done = vsprintf(event.app_log_msg + pos, format, arg);
    va_end(arg);

    //invoke app log function
    g_app_callback_func(g_callback_object,&event);

    pthread_mutex_unlock(&app_log_mutex);

    return;
}

int xdag_set_app_log_level(en_xdag_app_log_level level)
{
    int level0 = app_log_level;

    if (level >= 0 && level <= en_xdag_trace) {
        app_log_level = level;
    }

    return level0;
}

/* log utils for app end */
#if !defined(_WIN32) && !defined(_WIN64)
#define __USE_GNU
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>
//#include <execinfo.h>
#include <ucontext.h>

#define REG_(name) sprintf(buf + strlen(buf), #name "=%llx, ", (unsigned long long)uc->uc_mcontext.gregs[REG_ ## name])

static void sigCatch(int signum, siginfo_t *info, void *context)
{
	static void *callstack[100];
	int frames, i;
	char **strs;

	xdag_app_fatal("Signal %d delivered", signum);
#ifdef __x86_64__
	{
		static char buf[0x100]; *buf = 0;
		ucontext_t *uc = (ucontext_t*)context;
		REG_(RIP); REG_(EFL); REG_(ERR); REG_(CR2);
		xdag_fatal("%s", buf); *buf = 0;
		REG_(RAX); REG_(RBX); REG_(RCX); REG_(RDX); REG_(RSI); REG_(RDI); REG_(RBP); REG_(RSP);
		xdag_fatal("%s", buf); *buf = 0;
		REG_(R8); REG_(R9); REG_(R10); REG_(R11); REG_(R12); REG_(R13); REG_(R14); REG_(R15);
		xdag_fatal("%s", buf);
	}
#endif
	//frames = backtrace(callstack, 100);
	//strs = backtrace_symbols(callstack, frames);

	for (i = 0; i < frames; ++i) {
        xdag_app_fatal("%s", strs[i]);
	}
	signal(signum, SIG_DFL);
	kill(getpid(), signum);
	exit(-1);
}

int xdag_signal_init(void)
{
	int i;
	struct sigaction sa;

	sa.sa_sigaction = sigCatch;
	sigemptyset(&sa.sa_mask);
	sa.sa_flags = SA_RESTART | SA_SIGINFO;

	for (i = 1; i < 32; ++i) {
		if (i != SIGURG && i != SIGCHLD && i != SIGCONT && i != SIGPIPE && i != SIGINT && i != SIGTERM && i != SIGWINCH && i != SIGHUP) {
			sigaction(i, &sa, 0);
		}
	}
	return 0;
}

#else

int xdag_signal_init(void)
{
	return 0;
}

#endif
