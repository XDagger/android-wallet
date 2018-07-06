package io.xdag.xdagwallet.util;

import android.app.Activity;
import io.xdag.common.util.ClipBoardUtil;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/7/6.
 *
 * desc : copy util
 */
public class CopyUtil {

    public static void copyAddress(Activity activity, String address) {
        ClipBoardUtil.copyToClipBoard(address);
        AlertUtil.show(activity, activity.getString(R.string.copy_address_success));
    }
}
