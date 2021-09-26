package io.xdag.xdagwallet.wrapper;

import android.annotation.SuppressLint;
import android.app.Activity;


import java.io.File;
import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.Nullable;
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

    public static List<String> WALLET_LIST = Arrays.asList("dnet_key.dat", "wallet.dat", "storage");

    private Activity mActivity;


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

        return tempFile != null && xdagFile != null && FileUtil.moveDir(tempFile, xdagFile);
    }


    public boolean backupWallet() {

        File tempFile = createSDCardFile(mActivity);
        File xdagFile = createXdagFile();

        return tempFile != null && xdagFile != null && FileUtil.copyDir(xdagFile, tempFile);

    }
}
