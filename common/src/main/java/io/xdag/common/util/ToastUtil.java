package io.xdag.common.util;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import io.xdag.common.Common;

public class ToastUtil {

    private static final Toast TOAST = Toast.makeText(Common.getContext(), "", Toast.LENGTH_SHORT);
    private static final int TOAST_DEFAULT_OFF_Y = DensityUtil.dp2px(64);


    private ToastUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("ToastUtil cannot be instantiated !");
    }


    public static void show(int resId) {
        String msg = Common.getResources().getString(resId);
        show(msg);
    }


    public static void show(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        // Toast 的默认位置是 Bottom 向上偏移 64dp
        TOAST.setGravity(Gravity.BOTTOM, 0, TOAST_DEFAULT_OFF_Y);
        TOAST.setText(msg);
        TOAST.show();
    }


    public static void showCenter(int resId) {
        String msg = Common.getResources().getString(resId);
        showCenter(msg);
    }


    public static void showCenter(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        TOAST.setGravity(Gravity.CENTER, 0, 0);
        TOAST.setText(msg);
        TOAST.show();
    }
}
