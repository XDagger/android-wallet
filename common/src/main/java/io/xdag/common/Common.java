package io.xdag.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorRes;

/**
 * created by ssyijiu  on 2018/5/22
 * <p>
 * desc :
 */

public class Common {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;
    private static boolean sDebug;


    public static void init(Context context, boolean isDebug) {
        sContext = context;
        sDebug = isDebug;
    }


    public static Context getContext() {
        return sContext;
    }

    public static boolean isDebug() {
        return sDebug;
    }

    public static Resources getResources() {
        return getContext().getResources();
    }


    public static int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }
}
