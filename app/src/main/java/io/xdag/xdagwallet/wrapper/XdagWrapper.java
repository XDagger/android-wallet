package io.xdag.xdagwallet.wrapper;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class XdagWrapper {
    static {
        System.loadLibrary("xdag");
    }


    private static final String TAG = "XdagWallet";
    private static XdagWrapper instance = null;


    private XdagWrapper() {
    }


    public static XdagWrapper getInstance() {
        if (instance == null) {
            synchronized (XdagWrapper.class) {
                if (instance == null) {
                    instance = new XdagWrapper();
                }
            }
        }
        return instance;
    }


    public int XdagConnectToPool(String poolAddr) {
        return XdagConnect(poolAddr);
    }


    public int XdagDisConnectFromPool() {
        return XdagDisConnect();
    }


    public int XdagXferToAddress(String address, String amount) {
        return XdagXfer(address, amount);
    }


    public int XdagWrapperInit() {
        return XdagInit();
    }


    public int XdagWrapperUnInit() {
        return XdagUnInit();
    }


    public int XdagNotifyMsg() {
        return XdagNotifyMsg("");
    }

    public int XdagNotifyMsg(String authInfo) {
        return XdagNotifyNativeMsg(authInfo);
    }


    private native int XdagInit();


    private native int XdagUnInit();


    private native int XdagConnect(String poolAddr);


    private native int XdagDisConnect();


    private native int XdagXfer(String address, String amount);


    private native int XdagNotifyNativeMsg(String authInfo);


    public void updateUi(XdagEvent event) {

    }


    public static void nativeCallbackFunc(XdagEvent event) {
        Log.i(TAG, " receive event event type " + event.eventType
                + " balance " + event.balance
                + " state " + event.state
                + " thread id " + Thread.currentThread().getId());
        EventBus.getDefault().post(event);
    }
}
