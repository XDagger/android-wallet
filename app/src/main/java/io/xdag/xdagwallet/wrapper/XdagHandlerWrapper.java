package io.xdag.xdagwallet.wrapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.Nullable;
import io.xdag.common.tool.MLog;
import io.xdag.common.tool.NoLeakHandler;
import io.xdag.common.util.FileUtil;
import io.xdag.common.util.SDCardUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.AlertUtil;

/**
 * created by lxm on 2018/7/18.
 * <p>
 * desc :
 */
public class XdagHandlerWrapper {

    public static final String XDAG_FILE = "xdag";

    private static final int MSG_CONNECT_TO_POOL = 1;
    private static final int MSG_DISCONNECT_FROM_POOL = 2;
    private static final int MSG_XFER_XDAG_COIN = 3;

    private static final String KEY_POOL = "key_pool";
    private static final String KEY_ADDRESS = "key_address";
    private static final String KEY_AMOUNT = "key_amount";
    private static final String KEY_REMARK = "key_remark";

    public static List<String> WALLET_LIST = Arrays.asList("dnet_key.dat", "wallet.dat", "storage");

    private Activity mActivity;
    private Handler mXdagHandler;

    @SuppressLint("StaticFieldLeak")
    private static XdagHandlerWrapper sInstance = null;


    public static XdagHandlerWrapper getInstance(MainActivity activity) {
        synchronized (XdagHandlerWrapper.class) {
            if (sInstance == null) {
                synchronized (XdagHandlerWrapper.class) {
                    sInstance = new XdagHandlerWrapper(activity);
                }
            }
        }

        return sInstance;
    }


    private XdagHandlerWrapper(MainActivity activity) {
        mActivity = activity;
        HandlerThread handlerThread = new HandlerThread("XdagProcessThread");
        handlerThread.start();
        mXdagHandler = new XdagHandler(mActivity, handlerThread.getLooper());
    }


    public void connectToPool(@NonNull String poolAddress) {
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(KEY_POOL, poolAddress);
        msg.what = MSG_CONNECT_TO_POOL;
        msg.setData(data);
        mXdagHandler.sendMessage(msg);
    }


    public void xferXdagCoin(@NonNull String address, @NonNull String amount,@Nullable String remark) {
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(KEY_ADDRESS, address);
        data.putString(KEY_AMOUNT, amount);
        data.putString(KEY_REMARK, remark);
        msg.what = MSG_XFER_XDAG_COIN;
        msg.setData(data);
        mXdagHandler.sendMessage(msg);
    }


    public void disconnectPool() {
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        msg.what = MSG_DISCONNECT_FROM_POOL;
        msg.setData(data);
        mXdagHandler.sendMessage(msg);
    }


    static class XdagHandler extends NoLeakHandler<Activity> {

        XdagHandler(Activity target, Looper looper) {
            super(target, looper);
        }


        @Override
        protected void onMessageExecute(Activity target, Message msg) {
            switch (msg.what) {
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
                    String remark = data.getString(KEY_REMARK);
                    XdagWrapper xdagWrapper = XdagWrapper.getInstance();
                    xdagWrapper.XdagXferToAddress(address, amount,remark);
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
    @Nullable
    public static File createSDCardFile(Activity activity) {
        if (SDCardUtil.isAvailable()) {
            File file = new File(SDCardUtil.getSDCardPath(), XDAG_FILE);
            if (!file.exists() && !file.mkdirs()) {
                AlertUtil.show(activity, R.string.error_file_make_fail);
            } else {
                return file;
            }
        } else {
            AlertUtil.show(activity, R.string.error_sdcard_not_available);
        }
        return null;

    }


    /**
     * create file: /data/data/io.xdag.xdagwallet/files/xdag/
     */
    @Nullable
    private File createXdagFile() {

        File file = new File(mActivity.getFilesDir(), XDAG_FILE);
        if (!file.exists() && !file.mkdirs()) {
            AlertUtil.show(mActivity, R.string.error_file_make_fail);
        } else {
            return file;
        }
        return null;
    }


    public boolean createWallet() {
        return createXdagFile() != null;
    }


    public boolean restoreWallet() {

        File tempFile = createSDCardFile(mActivity);
        File xdagFile = createXdagFile();

        return xdagFile != null && FileUtil.moveDir(tempFile, xdagFile);
    }


    public boolean backupWallet() {

        File tempFile = createSDCardFile(mActivity);
        File xdagFile = createXdagFile();

        return tempFile != null && FileUtil.copyDir(xdagFile, tempFile);

    }


    public boolean isNotConnectedToPool(XdagEvent event) {
        return event.programState < XdagEvent.CONN;
    }
}
