package io.xdag.xdagwallet.wrapper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import io.xdag.common.tool.NoLeakHandler;
import io.xdag.xdagwallet.MainActivity;

/**
 * created by lxm on 2018/7/18.
 *
 * desc :
 */
public class XdagHandlerWrapper {

    private static final int MSG_CONNECT_TO_POOL = 1;
    private static final int MSG_DISCONNECT_FROM_POOL = 2;
    private static final int MSG_XFER_XDAG_COIN = 3;

    private static XdagHandlerWrapper sInstance;


    public static XdagHandlerWrapper getInstance(MainActivity activity) {
        if (sInstance == null) {
            sInstance = new XdagHandlerWrapper(activity);
        }
        return sInstance;
    }


    private HandlerThread mXdagProcessThread;
    private Handler mXdagHandler;


    private XdagHandlerWrapper(MainActivity activity) {
        mXdagProcessThread = new HandlerThread("XdagProcessThread");
        mXdagProcessThread.start();
        mXdagHandler = new XdagHandler(activity);
    }


    static class XdagHandler extends NoLeakHandler<MainActivity> {

        XdagHandler(MainActivity target) {
            super(target);
        }


        @Override protected void onMessageExecute(MainActivity target, Message msg) {
            switch (msg.arg1) {

            }
        }
    }

}
