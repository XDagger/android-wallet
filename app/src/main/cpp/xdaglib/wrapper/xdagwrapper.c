#include "xdagwrapper.h"
#include "../client/xdagmain.h"
#include "../client/version.h"
#include <malloc.h>

st_xdag_app_msg* (*g_app_callback_func)(const void* callback_object,st_xdag_event *event);
void* g_callback_object;

st_xdag_app_msg* xdag_malloc_app_msg(){

    st_xdag_app_msg *msg = (st_xdag_app_msg*)malloc(sizeof(st_xdag_app_msg));

    if(!msg){
        return NULL;
    }

    msg->xdag_pwd = NULL;
    msg->xdag_retype_pwd = NULL;
    msg->xdag_xfer_account = NULL;
    msg->xdag_xfer_num = NULL;
    msg->xdag_state = NULL;
    msg->xdag_balance = NULL;

    return msg;
}
void xdag_free_app_msg(st_xdag_app_msg* msg){

    if(!msg){
        return;
    }

    if(msg->xdag_pwd){
        free(msg->xdag_pwd);
        msg->xdag_pwd = NULL;
    }

    if(msg->xdag_retype_pwd){
        free(msg->xdag_retype_pwd);
        msg->xdag_retype_pwd = NULL;
    }

    if(msg->xdag_xfer_account){
        free(msg->xdag_xfer_account);
        msg->xdag_xfer_account = NULL;
    }

    if(msg->xdag_xfer_num){
        free(msg->xdag_xfer_num);
        msg->xdag_xfer_num = NULL;
    }

    if(msg->xdag_state){
        free(msg->xdag_state);
        msg->xdag_state = NULL;
    }

    if(msg->xdag_balance){
        free(msg->xdag_balance);
        msg->xdag_balance = NULL;
    }
}

const char* xdag_get_version(){
    return XDAG_VERSION;
}

void xdag_send_coin(const char* amount,const char* address){
    xdag_xfer_coin(amount,address);
}

void xdag_wrapper_init(const void* callback_object,
                       st_xdag_app_msg* (*callback_func)(const void* callback_object,st_xdag_event *event)){
    g_app_callback_func = callback_func;
    g_callback_object = callback_object;
}

void report_ui_walletinit_event(en_xdag_event_type event_type,const char* err_msg){

    st_xdag_event event;
    memset(&event,0,sizeof(st_xdag_event));
    event.event_type = event_type;
    event.procedure_type = en_procedure_init_wallet;

    if(err_msg)
        strncpy(event.error_msg,err_msg,strlen(err_msg));

    g_app_callback_func(g_callback_object,&event);
}

void report_ui_xfer_event(en_xdag_event_type event_type,const char* err_msg){

    st_xdag_event event;
    memset(&event,0,sizeof(st_xdag_event));
    event.event_type = event_type;
    event.procedure_type = en_procedure_xfer_coin;

    if(err_msg)
        strncpy(event.error_msg,err_msg,strlen(err_msg));

    g_app_callback_func(g_callback_object,&event);
}

void xdag_wrapper_uninit(){
    xdag_uninit();
}


