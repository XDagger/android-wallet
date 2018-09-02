package io.xdag.xdagwallet.net.error;

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

    public ErrorConsumer() {
    }

    public ErrorConsumer(Activity activity) {
        this.activity = activity;
    }


    @Override public void accept(Throwable throwable) {
        MLog.i(throwable.getMessage());
        if(activity != null) {
            AlertUtil.show(activity, throwable.getMessage());
        }
    }
}
