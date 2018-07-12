package io.xdag.xdagwallet.util;

import android.app.Activity;

import com.tapadoo.alerter.Alerter;

import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/30.
 * <p>
 * desc :
 */
public class AlertUtil {

    private static final int ALERT_DURATION = 2000;


    public static void show(Activity activity, int res) {
        show(activity, activity.getString(res));
    }


    public static void show(Activity activity, String message) {
        Alerter.create(activity)
                .setDuration(ALERT_DURATION)
                .hideIcon()
                .setTextAppearance(R.style.AlertTextStyle)
                .setBackgroundColorRes(R.color.colorAccent)
                .setText(message)
                .show();
    }
}
