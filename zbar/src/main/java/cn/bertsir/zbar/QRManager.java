package cn.bertsir.zbar;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Bert on 2017/9/22.
 */

public class QRManager {

    private static QRManager instance;
    private QRConfig options;

    public OnScanResultCallback resultCallback;


    public synchronized static QRManager getInstance() {
        if (instance == null) {
            instance = new QRManager();
        }
        return instance;
    }


    public OnScanResultCallback getResultCallback() {
        return resultCallback;
    }


    public QRManager init(QRConfig options) {
        this.options = options;
        return this;
    }


    public void startScan(Activity activity, OnScanResultCallback resultCall) {

        if (options == null) {
            options = new QRConfig.Builder().create();
        }
        Intent intent = new Intent(activity, QRActivity.class);
        intent.putExtra(QRConfig.EXTRA_THIS_CONFIG, options);
        activity.startActivity(intent);
        // 绑定图片接口回调函数事件
        resultCallback = resultCall;
    }


    public interface OnScanResultCallback {
        /**
         * 处理成功
         * 多选
         */
        void onScanSuccess(String result);

        void onScanFailed();

    }
}
