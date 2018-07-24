package io.xdag.common.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

/**
 * created by ssyijiu  on 2018/7/25
 */
public class AlertBuilder extends AlertDialog.Builder {

    public AlertBuilder(@NonNull Context context) {
        super(context);
        init();
    }

    protected void init() {
    }
}
