/* basic variables, T13.714-T13.895 $DVS:time$ */

#ifndef XDAG_WRAPPER_H
#define XDAG_WRAPPER_H

#include <time.h>

#ifdef __cplusplus
extern "C" {
#endif

#define MAX_XDAG_ADDRESS_LEN 32
#define MAX_XDAG_BANLANCE_LEN 32
#define MAX_XDAG_STATE_LEN 1024
#define MAX_XDAG_ERR_MSG_LEN 1024
#define MAX_XDAG_LOG_BUF_SIZE 4096

typedef enum {
    en_procedure_init_wallet    = 0,
    en_procedure_xfer_coin      = 1,
    en_procedure_work_thread    = 2,
    en_procedure_pool_thread    = 3
} en_xdag_procedure_type;

typedef enum {
    en_event_type_pwd               = 0x1000,
    en_event_set_pwd                = 0x1001,
    en_event_retype_pwd             = 0x1002,
    en_event_set_rdm                = 0x1003,
    en_event_pwd_not_same           = 0x1004,
    en_event_pwd_error              = 0x1005,
    en_event_pwd_format_error       = 0x1006,

    //dnet wallet storage error
    en_event_open_dnetfile_error    = 0x2000,
    en_event_open_walletfile_error  = 0x2001,
    en_event_load_storage_error     = 0x2002,
    en_event_write_dnet_file_error  = 0x2003,
    en_event_add_trust_host_error   = 0x2004,

    //xfer error
    en_event_nothing_transfer       = 0x3000,
    en_event_balance_too_small      = 0x3001,
    en_event_invalid_recv_address   = 0x3002,
    en_event_xdag_transfered        = 0x3003,

    //miner net thread error
    en_event_connect_pool_timeout   = 0x4000,
    en_event_make_block_error       = 0x4001,

    //invoke print log or update ui
    en_event_xdag_log_print         = 0x5000,
    en_event_update_progress        = 0x5001,
    en_event_update_state           = 0x5002,

    //block thread error(work_thread)


    en_event_cannot_create_block    = 0x7000,
    en_event_cannot_find_block      = 0x7001,
    en_event_cannot_load_block      = 0x7002,
    en_event_cannot_create_socket   = 0x7003,
    en_event_host_is_not_given      = 0x7004,
    en_event_cannot_reslove_host    = 0x7005,
    en_event_port_is_not_given      = 0x7006,
    en_event_cannot_connect_to_pool = 0x7007,
    en_event_socket_isclosed        = 0x7008,
    en_event_socket_hangup          = 0x7009,
    en_event_socket_error           = 0x700a,
    en_event_read_socket_error      = 0x700b,
    en_event_write_socket_error     = 0x700c,


    en_event_unkown                 = 0xf000,
} en_xdag_event_type;

typedef enum tag_en_xdag_app_log_level{
    en_xdag_no_error,
    en_xdag_fatal,
    en_xdag_critical,
    en_xdag_internal,
    en_xdag_error,
    en_xdag_warning,
    en_xdag_message,
    en_xdag_info,
    en_xdag_debug,
    en_xdag_trace
}en_xdag_app_log_level;

typedef enum tag_en_address_load_state{
    en_address_not_ready,
    en_address_ready,
} en_address_load_state;

typedef enum tag_en_balance_load_state{
    en_balance_not_ready,
    en_balance_ready,
} en_balance_load_state;

typedef enum tag_en_xdag_program_state{
    NINT,
    INIT,
    KEYS,
    REST,
    LOAD,
    STOP,
    WTST,
    WAIT,
    TTST,
    TRYP,
    CTST,
    CONN,
    XFER,
    PTST,
    POOL,
    MTST,
    MINE,
    STST,
    SYNC
} en_xdag_program_state;

typedef struct {
    en_xdag_procedure_type  procedure_type;
    en_xdag_event_type      event_type;
    en_xdag_app_log_level   log_level;
    en_xdag_program_state   xdag_program_state;
    en_address_load_state   xdag_address_state;
    en_balance_load_state   xdag_balance_state;
    char state[MAX_XDAG_STATE_LEN + 1];
    char address[MAX_XDAG_ADDRESS_LEN + 1];
    char balance[MAX_XDAG_BANLANCE_LEN + 1];
    char error_msg[MAX_XDAG_ERR_MSG_LEN + 1];
    char app_log_msg[MAX_XDAG_LOG_BUF_SIZE + 1];
} st_xdag_event;

typedef struct {
    char    *xdag_pwd;
    char    *xdag_retype_pwd;
    char    *xdag_rdm;
    char    *xdag_xfer_account;
    char    *xdag_xfer_num;
    char    *xdag_state;
    char    *xdag_balance;
}st_xdag_app_msg;


/* callback object and callback func for app ui */
extern st_xdag_app_msg* (*g_app_callback_func)(const void* callback_object,st_xdag_event *event);
extern void* g_callback_object;

extern st_xdag_app_msg* xdag_malloc_app_msg();
extern void xdag_free_app_msg(st_xdag_app_msg* msg);

extern const char* xdag_get_version();

extern void xdag_wrapper_init(const void* callback_object,
                              st_xdag_app_msg* (*callback_func)(const void* callback_object,st_xdag_event *event));

extern void xdag_send_coin(const char* amount,const char* address);

extern void report_ui_walletinit_event(en_xdag_event_type event_type,const char* err_msg);
extern void report_ui_xfer_event(en_xdag_event_type event_type,const char* err_msg);


extern void xdag_wrapper_uninit();

#ifdef __cplusplus
}
#endif

#endif
