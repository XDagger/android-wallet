package io.xdag.xdagwallet.config;

import io.xdag.common.util.SPUtil;
import io.xdag.xdagwallet.net.ApiServer;

/**
 * created by lxm on 2018/7/18.
 * <p>
 * desc : The config for xdag wallet
 */
public class Config {

    private static final String DEFAULT_POOL = "xdagmine.com:13654";
    private static final String CONFIG_KEY_POOL = "config_key_pool";
    private static final String CONFIG_KEY_IS_USER_BACKUP = "config_key_is_user_backup";
    private static final String CONFIG_KEY_NOT_SHOW_USAGE = "config_key_not_show_explain";
    private static final String CONFIG_KEY_NOT_REMIND_ROOT = "config_key_not_remind_root";
    private static final String CONFIG_KEY_TRAN_HOST = "config_key_tran_host";
    private static final String CONFIG_KEY_ADDRESS = "config_key_address";


    public static void setPoolAddress(String poolAddress) {
        SPUtil.putString(CONFIG_KEY_POOL, poolAddress);
    }


    public static String getPoolAddress() {
        return SPUtil.getString(CONFIG_KEY_POOL, DEFAULT_POOL);
    }


    public static void setUserBackup(boolean backup) {
        SPUtil.putBoolean(CONFIG_KEY_IS_USER_BACKUP, backup);
    }


    public static boolean isUserBackup() {
        return SPUtil.getBoolean(CONFIG_KEY_IS_USER_BACKUP, false);
    }


    public static void setNotDisplayUsage(boolean notShow) {
        SPUtil.putBoolean(CONFIG_KEY_NOT_SHOW_USAGE, notShow);
    }


    public static boolean isNotDisplayUsage() {
        return SPUtil.getBoolean(CONFIG_KEY_NOT_SHOW_USAGE, false);
    }

    public static void setNotRemindRoot(boolean notRemind) {
        SPUtil.putBoolean(CONFIG_KEY_NOT_REMIND_ROOT, notRemind);
    }


    public static boolean isRemindRoot() {
        return !SPUtil.getBoolean(CONFIG_KEY_NOT_REMIND_ROOT, false);
    }

    public static void setTransactionHost(String host) {
        SPUtil.putString(CONFIG_KEY_TRAN_HOST, host);
    }


    public static String getTransactionHost() {
        return SPUtil.getString(CONFIG_KEY_TRAN_HOST, ApiServer.BASE_URL_TRANSACTION);
    }


    public static void setAddress(String address) {
        SPUtil.putString(CONFIG_KEY_ADDRESS, address);
    }

    public static String getAddress() {
        return SPUtil.getString(CONFIG_KEY_ADDRESS, "");
    }
}
