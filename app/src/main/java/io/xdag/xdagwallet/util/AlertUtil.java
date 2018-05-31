package io.xdag.xdagwallet.util;

import android.app.Activity;
import com.tapadoo.alerter.Alerter;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/30.
 *
 * desc :
 */
public class AlertUtil {

    private static final int ALERT_DURATION = 1500;


    public static void show(Activity activity, String message) {
        Alerter.create(activity)
            .setDuration(ALERT_DURATION)
            .setBackgroundColorRes(R.color.colorAccent)
            .setText(message)
            .show();
    }
}
