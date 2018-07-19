package io.xdag.xdagwallet.api.xdagscan;

import android.app.Activity;
import io.reactivex.functions.Consumer;
import io.xdag.common.tool.MLog;
import io.xdag.xdagwallet.util.AlertUtil;

/**
 * created by lxm on 2018/7/19.
 *
 * handle exception
 */
public class ErrorConsumer implements Consumer<Throwable> {

    private Activity activity;


    public ErrorConsumer(Activity activity) {
        this.activity = activity;
    }


    @Override public void accept(Throwable throwable) throws Exception {
        MLog.i(throwable.getMessage());
        AlertUtil.show(activity, throwable.getMessage());
    }
}
