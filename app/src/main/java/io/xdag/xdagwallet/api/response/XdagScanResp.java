package io.xdag.xdagwallet.api.response;

import android.text.TextUtils;

/**
 * created by lxm on 2018/7/6.
 *
 * desc :
 */
public class XdagScanResp<T> {

    public String errno;
    public String message;
    public T data;


    public boolean success() {
        return TextUtils.equals(errno, "0");
    }
}
