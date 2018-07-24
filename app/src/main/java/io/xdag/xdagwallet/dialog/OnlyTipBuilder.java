package io.xdag.xdagwallet.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

/**
 * created by ssyijiu  on 2018/7/25
 */
public class OnlyTipBuilder extends AlertDialog.Builder {


    public OnlyTipBuilder(@NonNull Context context) {
        super(context);
    }

    public OnlyTipBuilder(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        setCancelable(false);
    }

}
