package io.xdag.xdagwallet.widget;

import android.app.Activity;
import com.tapadoo.alerter.Alerter;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/30.
 *
 * desc :
 */
public class AlertWrap {

    public static void show(Activity activity, String message) {
        Alerter.create(activity)
            .setDuration(1500)
            .setBackgroundColorRes(R.color.colorAccent)
            .setText(message)
            .show();
    }
}
