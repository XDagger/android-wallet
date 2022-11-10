package io.xdag.common.util;

import android.os.Environment;
import android.text.format.Formatter;

import java.io.File;

import io.xdag.common.Common;

public class SDCardUtil {

    private SDCardUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("SDCardUtil cannot be instantiated");
    }


    /**
     * 判断SDCard是否可用
     *
     * @return true 可用，false 不可用
     */
    public static boolean isAvailable() {
        String state = Environment.getExternalStorageState();
        return state != null && state.equals(Environment.MEDIA_MOUNTED);
    }
}
