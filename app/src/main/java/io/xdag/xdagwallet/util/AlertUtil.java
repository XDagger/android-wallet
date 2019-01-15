package io.xdag.xdagwallet.util;

import android.app.Activity;

import com.tapadoo.alerter.Alerter;

import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/30.
 * <p>
 * desc :
 */
public class AlertUtil {

    private static final int ALERT_DURATION = 3000;


    public static void show(Activity activity, int res) {
        show(activity, activity.getString(res));
    }


    public static void show(Activity activity, String message) {
        if (activity != null) {
            Alerter.create(activity)
                .setDuration(ALERT_DURATION)
                .hideIcon()
                .setTextAppearance(R.style.AlertTextStyle)
                .setBackgroundColorRes(R.color.colorAccent)
                .setText(message)
                .show();
        } else {
            ToastUtil.showCenter(message);
        }

    }
}
