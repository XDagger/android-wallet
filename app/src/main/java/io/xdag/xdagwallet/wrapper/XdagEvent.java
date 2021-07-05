package io.xdag.xdagwallet.wrapper;

public class XdagEvent {

    /**
     * xdag procedure type start
     */
    public static int en_procedure_init_wallet = 0;
    public static int en_procedure_xfer_coin = 1;
    public static int en_procedure_work_thread = 2;
    public static int en_procedure_pool_thread = 3;
    /**
     * xdag procedure type end
     * */

    /**
     * xdag event type start
     */
    public static final int en_event_type_pwd = 0x1000;
    public static final int en_event_set_pwd = 0x1001;
    public static final int en_event_retype_pwd = 0x1002;
    public static final int en_event_set_rdm = 0x1003;
    public static final int en_event_pwd_not_same = 0x1004;
    public static final int en_event_pwd_error = 0x1005;
    public static final int en_event_pwd_format_error = 0x1006;
    //dnet wallet storage error
    public static final int en_event_open_dnetfile_error = 0x2000;
    public static final int en_event_open_walletfile_error = 0x2001;
    public static final int en_event_load_storage_error = 0x2002;
    public static final int en_event_write_dnet_file_error = 0x2003;
    public static final int en_event_add_trust_host_error = 0x2004;
    //xfer error
    public static final int en_event_nothing_transfer = 0x3000;
    public static final int en_event_balance_too_small = 0x3001;
    public static final int en_event_invalid_recv_address = 0x3002;
    public static final int en_event_xdag_transfered = 0x3003;
    //miner net thread error
    public static final int en_event_connect_pool_timeout = 0x4000;
    public static final int en_event_make_block_error = 0x4001;
    //invoke print log or update ui
    public static final int en_event_xdag_log_print = 0x5000;
    public static final int en_event_update_progress = 0x5001;
    public static final int en_event_update_state = 0x5002;
    //block thread error(work_thread)
    public static final int en_event_cannot_create_block = 0x7000;
    public static final int en_event_cannot_find_block = 0x7001;
    public static final int en_event_cannot_load_block = 0x7002;
    public static final int en_event_cannot_create_socket = 0x7003;
    public static final int en_event_host_is_not_given = 0x7004;
    public static final int en_event_cannot_reslove_host = 0x7005;
    public static final int en_event_port_is_not_given = 0x7006;
    public static final int en_event_cannot_connect_to_pool = 0x7007;
    public static final int en_event_socket_isclosed = 0x7008;
    public static final int en_event_socket_hangup = 0x7009;
    public static final int en_event_socket_error = 0x700a;
    public static final int en_event_read_socket_error = 0x700b;
    public static final int en_event_write_socket_error = 0x700c;
    public static final int en_event_disconneted_finished = 0x700d;
    public static final int en_event_unkown = 0xf000;
    /**
     * xdag event type end
     * */

    /**
     * xdag log level start
     */
    public static final int en_xdag_no_error = 1;
    public static final int en_xdag_fatal = 2;
    public static final int en_xdag_critical = 3;
    public static final int en_xdag_internal = 4;
    public static final int en_xdag_error = 5;
    public static final int en_xdag_warning = 6;
    public static final int en_xdag_message = 7;
    public static final int en_xdag_info = 8;
    public static final int en_xdag_debug = 9;
    public static final int en_xdag_trace = 10;
    /**
     * xdag log level end
     * */

    /**
     * xdag address load state start
     */
    public static final int en_address_not_ready = 0;
    public static final int en_address_ready = 1;
    /**
     * xdag address load state start
     * */

    /**
     * xdag balance load state start
     */
    public static final int en_balance_not_ready = 0;
    public static final int en_balance_ready = 1;
    /**
     * xdag balance load state end
     * */

    /**
     * xdag program state start
     */
    public static final int NINT = 0;
    public static final int INIT = 1;
    public static final int KEYS = 2;
    public static final int REST = 3;
    public static final int LOAD = 4;
    public static final int STOP = 5;
    public static final int WTST = 6;
    public static final int WAIT = 7;
    public static final int TTST = 8;
    public static final int TRYP = 9;
    public static final int CTST = 10;
    public static final int CONN = 11;
    public static final int XFER = 12;
    public static final int PTST = 13;
    public static final int POOL = 14;
    public static final int MTST = 15;
    public static final int MINE = 16;
    public static final int STST = 17;
    public static final int SYNC = 18;
    public static final int TIME = 19;
    /**
     * xdag program state end
     */

    public int procedureType;
    public int eventType;
    public int logLevel;
    public int programState;
    public int addressLoadState;
    public int balanceLoadState;
    public String state;
    public String address;
    public String balance;
    public String errorMsg;
    public String appLogMsg;

    public XdagEvent(int eventType){
        this.eventType = eventType;
    }
    public XdagEvent(int procedureType,
                     int eventType,
                     int logLevel,
                     int programState,
                     int addressLoadState,
                     int balanceLoadState,
                     String state,
                     String address,
                     String balance,
                     String errorMsg,
                     String appLogMsg) {

        this.procedureType = procedureType;
        this.eventType = eventType;
        this.logLevel = logLevel;
        this.programState = programState;
        this.addressLoadState = addressLoadState;
        this.balanceLoadState = balanceLoadState;
        this.state = state;
        this.address = address;
        this.balance = balance;
        this.errorMsg = errorMsg;
        this.appLogMsg = appLogMsg;
    }
}
