package io.xdag.xdagwallet.config;

import io.xdag.common.util.SPUtil;

/**
 * created by lxm on 2018/7/18.
 *
 * desc : The config for xdag wallet
 */
public class Config {

    public static final String POLL_ADDRESS = "xdagmine.com:13654";

    private static final String CONFIG_KEY_IS_RESTORE = "config_key_is_restore";


    public static void setRestore(boolean restore) {
        SPUtil.putBoolean(CONFIG_KEY_IS_RESTORE, restore);
    }


    public static boolean isRestore() {
        return SPUtil.getBoolean(CONFIG_KEY_IS_RESTORE, false);
    }

}
