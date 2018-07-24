package io.xdag.xdagwallet.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import cn.bertsir.zbar.QRConfig;
import cn.bertsir.zbar.QRManager;
import cn.bertsir.zbar.QRUtils;
import io.xdag.common.Common;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/31.
 *
 * desc :
 */
public class ZbarUtil {

    static {
        QRConfig config = new QRConfig.Builder()
            .setShowDes(false)
            .setCornerColor(Common.getColor(R.color.colorPrimary))
            .setLineColor(Color.WHITE)
            .setPlaySound(true)
            .setTitleText("Scan QRCode")
            .setTitleBackgroudColor(Common.getColor(R.color.colorPrimary))
            .setTitleTextColor(Color.WHITE)
            .create();

        QRManager.getInstance().init(config);
    }


    public static void startScan(final Activity activity, QRManager.OnScanResultCallback callback) {
        QRManager.getInstance().startScan(activity, callback);
    }


    public static Bitmap createQRCode(String content) {
        return QRUtils.getInstance().createQRCode(content);
    }
}
