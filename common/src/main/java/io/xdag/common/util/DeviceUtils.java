package io.xdag.common.util;

import android.os.Build;

/**
 * Created by ssyijiu on 2022/11/10.
 */
public class DeviceUtils {

    public static boolean afterQ() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q;
    }
}
