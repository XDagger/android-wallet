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


    /**
     * 获取SD卡路径，使用前先判断 SDCard 是否可用，不可用自行处理
     *
     * @return sdcard path
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }


    /**
     * 获取SD卡的可用容量 单位byte
     *
     * @return the byte sdcard can use
     */
    public static long getUsableSize() {
        if (isAvailable()) {
            File file = new File(getSDCardPath());
            return file.getUsableSpace();
        }
        return 0;
    }


    /**
     * 获取SD卡的可用容量的描述
     */
    public static String getUsableSizeDesc() {
        return Formatter.formatFileSize(Common.getContext(), getUsableSize());
    }

}
