package io.xdag.xdagwallet.dialog;

import android.content.Context;
import android.support.annotation.NonNull;

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
        setPositiveButton(R.string.ensure, null);
    }
}
