#ifndef _SYSTEM_H
#define _SYSTEM_H

#ifdef __cplusplus
extern "C" {
#endif

#include <xdaglib/dus/dfsrsa.h>

#if defined(_WIN32) || defined(_WIN64)

#include <Windows.h>
#define inline              __inline
#include "../dus/dfsrsa.h"

#define strtok_r            strtok_s
#define localtime_r(a, b)   localtime_s(b, a)
#ifdef _WIN32
#define sleep(x)            Sleep((x) * 1000)
#else
#define sleep(x)            _sleep(x)
#endif
#define pthread_self_ptr()  pthread_self().p

#define xdag_mkdir(d)           mkdir(d)
#define strdup(x)               _strdup(x)
#define ioctl                   ioctlsocket
#define fcntl(a, b, c)          0
#define close                   closesocket
#define write(a, b, c)          send(a, b, c, 0)
#define read(a, b, c)           recv(a, b, c, 0)
#define sysconf(x)              (512)

#else

//android pthread specific
#if defined(ANDROID) || defined(__ANDROID__)

#define pthread_cancel(t) { t = -1;}
#define pthread_iscancel(t) if(t == -1) {pthread_exit(NULL);}
#define PTHREAD_CANCEL_ENABLE 0
#define PTHREAD_CANCEL_DEFERRED 0
#define pthread_setcancelstate(type,oldstate)
#define pthread_setcanceltype(type,oldstate)

#else
#define pthread_iscancel(t) pthread_testcancel()
#endif

#define pthread_self_ptr()      pthread_self()
#define xdag_mkdir(d)      mkdir(d, 0770)
#define INVALID_SOCKET          -1

#endif

typedef struct {
    dfsrsa_t num[4];
} xdag_diff_t;

#define xdag_diff_max      { -1, -1, -1, -1 }
#define xdag_diff_gt(l, r) (dfsrsa_cmp((l).num, (r).num, 4) > 0)
#define xdag_diff_args(d)  (unsigned long long)(*(uint64_t*)&d.num[2]), (unsigned long long)(*(uint64_t*)&d.num[0])
#define xdag_diff_shr32(p) ((p)->num[0] = (p)->num[1], (p)->num[1] = (p)->num[2], (p)->num[2] = (p)->num[3], (p)->num[3] = 0)
#define xdag_diff_to64(d)  (*(uint64_t*)&d.num[0])

static inline xdag_diff_t xdag_diff_add(xdag_diff_t p, xdag_diff_t q)
{
    xdag_diff_t r;
    dfsrsa_add(r.num, p.num, q.num, 4);
    return r;
}
static inline xdag_diff_t xdag_diff_div(xdag_diff_t p, xdag_diff_t q)
{
    xdag_diff_t r;
    dfsrsa_divmod(p.num, 4, q.num, 4, r.num);
    return r;
}

#ifdef __cplusplus
}
#endif

#endif
