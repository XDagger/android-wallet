package io.xdag.xdagwallet.wrapper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import io.reactivex.annotations.Nullable;
import io.xdag.common.tool.MLog;
import io.xdag.common.tool.NoLeakHandler;
import io.xdag.common.util.FileUtil;
import io.xdag.common.util.SDCardUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.AlertUtil;
import java.io.File;

/**
 * created by lxm on 2018/7/18.
 *
 * desc :
 */
public class XdagHandlerWrapper {

    private static final String XDAG_FILE = "xdag";

    private static final int MSG_CONNECT_TO_POOL = 1;
    private static final int MSG_DISCONNECT_FROM_POOL = 2;
    private static final int MSG_XFER_XDAG_COIN = 3;

    private static final String KEY_POOL = "key_pool";
    private static final String KEY_ADDRESS = "key_address";
    private static final String KEY_AMOUNT = "key_amount";

    private Activity mActivity;
    private Handler mXdagHandler;


    public XdagHandlerWrapper(MainActivity activity) {
        mActivity = activity;
        HandlerThread handlerThread = new HandlerThread("XdagProcessThread");
        handlerThread.start();
        mXdagHandler = new XdagHandler(activity, handlerThread.getLooper());
    }


    public void connectToPool(@NonNull String poolAddress) {
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(KEY_POOL, poolAddress);
        msg.arg1 = MSG_CONNECT_TO_POOL;
        msg.setData(data);
        mXdagHandler.sendMessage(msg);
    }


    public void xferXdagCoin(@NonNull String address, @NonNull String amount) {
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(KEY_ADDRESS, address);
        data.putString(KEY_AMOUNT, amount);
        msg.arg1 = MSG_XFER_XDAG_COIN;
        msg.setData(data);
        mXdagHandler.sendMessage(msg);
    }


    static class XdagHandler extends NoLeakHandler<MainActivity> {

        XdagHandler(MainActivity target, Looper looper) {
            super(target, looper);
        }


        @Override protected void onMessageExecute(MainActivity target, Message msg) {
            switch (msg.arg1) {
                case MSG_CONNECT_TO_POOL: {
                    MLog.i("receive msg connect to the pool thread id " +
                        Thread.currentThread().getId());
                    Bundle data = msg.getData();
                    String poolAddr = data.getString(KEY_POOL);
                    XdagWrapper xdagWrapper = XdagWrapper.getInstance();
                    xdagWrapper.XdagConnectToPool(poolAddr);
                }
                break;
                case MSG_DISCONNECT_FROM_POOL: {
                    XdagWrapper xdagWrapper = XdagWrapper.getInstance();
                    xdagWrapper.XdagDisConnectFromPool();
                }
                break;
                case MSG_XFER_XDAG_COIN: {
                    MLog.i("receive msg xfer coin thread id " + Thread.currentThread().getId());
                    Bundle data = msg.getData();
                    String address = data.getString(KEY_ADDRESS);
                    String amount = data.getString(KEY_AMOUNT);
                    XdagWrapper xdagWrapper = XdagWrapper.getInstance();
                    xdagWrapper.XdagXferToAddress(address, amount);
                }
                break;
                default: {
                    MLog.e("unknow command from ui");
                }
            }
        }
    }


    /**
     * create file: sdcard/xdag/
     */
    @Nullable private File createTempFile() {
        if (SDCardUtil.isAvailable()) {
            File file = new File(SDCardUtil.getSDCardPath(), XDAG_FILE);
            if (!file.exists() && !file.mkdirs()) {
                AlertUtil.show(mActivity, R.string.error_file_make_fail);
            } else {
                return file;
            }
        } else {
            AlertUtil.show(mActivity, R.string.error_sdcard_not_available);
        }
        return null;

    }


    /**
     * create file: /data/data/io.xdag.xdagwallet/files/xdag/
     */
    @Nullable public File createXdagFile() {

        File file = new File(mActivity.getFilesDir(), XDAG_FILE);
        if (!file.exists() && !file.mkdirs()) {
            AlertUtil.show(mActivity, R.string.error_file_make_fail);
        } else {
            return file;
        }
        return null;
    }


    public boolean recoverXdagFile() {

        File tempFile = createTempFile();
        File xdagFile = createXdagFile();

        return tempFile != null && xdagFile != null && FileUtil.moveDir(tempFile, xdagFile);
    }
}
