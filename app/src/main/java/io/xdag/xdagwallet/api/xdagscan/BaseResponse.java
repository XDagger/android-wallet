package io.xdag.xdagwallet.api.xdagscan;

import android.text.TextUtils;

/**
 * created by lxm on 2018/7/6.
 *
 * desc :
 */
public class BaseResponse<T> {

    public String errno;
    public String message;
    public T data;


    public boolean isSuccess() {
        return TextUtils.equals(errno, "0");
    }
}
