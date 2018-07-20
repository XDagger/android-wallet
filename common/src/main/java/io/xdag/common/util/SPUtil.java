package io.xdag.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import io.xdag.common.Common;

/**
 * created by lxm on 2018/7/20.
 */
public class SPUtil {
    private static SharedPreferences sSP;


    private SPUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("SPUtil cannot be instantiated !");
    }


    private static SharedPreferences getInstance() {

        if (sSP == null) {
            sSP = Common.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sSP;
    }


    public static void putBoolean(String key, boolean value) {
        sSP = getInstance();
        sSP.edit().putBoolean(key, value).apply();
    }


    public static void putString(String key, String value) {
        sSP = getInstance();
        sSP.edit().putString(key, value).apply();
    }


    public static void putInt(String key, int value) {
        sSP = getInstance();
        sSP.edit().putInt(key, value).apply();
    }


    public static boolean getBoolean(String key, boolean defValue) {
        sSP = getInstance();
        return sSP.getBoolean(key, defValue);
    }


    public static String getString(String key, String defValue) {
        sSP = getInstance();
        return sSP.getString(key, defValue);
    }


    public static int getInt(String key, int defValue) {
        sSP = getInstance();
        return sSP.getInt(key, defValue);
    }


    public static void remove(String key) {
        sSP = getInstance();
        sSP.edit().remove(key).apply();
    }


    public static void clear(String key) {
        sSP = getInstance();
        sSP.edit().clear().apply();

    }


    public static boolean contains(String key) {
        sSP = getInstance();
        return sSP.contains(key);

    }


    public static void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (listener != null) {
            sSP = getInstance();
            sSP.registerOnSharedPreferenceChangeListener(listener);
        }

    }


    public static void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (listener != null) {
            sSP = getInstance();
            sSP.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }
}
