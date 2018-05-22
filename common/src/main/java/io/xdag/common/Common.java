package io.xdag.common;

import android.content.Context;

/**
 * created by ssyijiu  on 2018/5/22
 * <p>
 * desc :
 */

public class Common {

    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    public Context getContext() {
        return sContext;
    }
}
