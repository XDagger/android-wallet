package io.xdag.common.util;

import android.content.res.Configuration;
import io.xdag.common.Common;

public class DensityUtil {

    private static float density = -1F;
    private static float scaledDensity = -1F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;


    private DensityUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("DensityUtil cannot be instantiated !");
    }


    public static int dp2px(float dpValue) {
        return (int) (dpValue * getDensity() + 0.5F);
    }


    public static int px2dp(float pxValue) {
        return (int) (pxValue / getDensity() + 0.5F);
    }


    public static int sp2px(float spValue) {
        return (int) (spValue * getScaledDensity() + 0.5F);
    }


    public static int getScreenWidth() {
        if (widthPixels <= 0) {
            widthPixels = Common.getResources().getDisplayMetrics().widthPixels;
        }
        return widthPixels;
    }


    public static int getScreenHeight() {
        if (heightPixels <= 0) {
            heightPixels = Common.getResources().getDisplayMetrics().heightPixels;
        }
        return heightPixels;
    }


    private static float getDensity() {
        if (density <= 0F) {
            density = Common.getResources().getDisplayMetrics().density;
        }
        return density;
    }


    private static float getScaledDensity() {
        if (scaledDensity <= 0F) {
            scaledDensity = Common.getResources().getDisplayMetrics().scaledDensity;
        }
        return scaledDensity;
    }


    public static boolean isScreenPortrait() {
        return Common.getResources().getConfiguration().orientation
            == Configuration.ORIENTATION_PORTRAIT;
    }


    public static boolean isScreenLand() {
        return Common.getResources().getConfiguration().orientation
            == Configuration.ORIENTATION_LANDSCAPE;
    }
}
