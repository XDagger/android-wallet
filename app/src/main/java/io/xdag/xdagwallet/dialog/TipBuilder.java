package io.xdag.xdagwallet.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import io.xdag.common.base.AlertBuilder;
import io.xdag.xdagwallet.R;

/**
 * created by ssyijiu  on 2018/7/25
 */
public class TipBuilder extends AlertBuilder {


    public TipBuilder(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        setCancelable(false);
    }

    public AlertDialog.Builder setPositiveListener(DialogInterface.OnClickListener listener) {
        setPositiveButton(R.string.ensure, listener);
        return this;
    }
}
