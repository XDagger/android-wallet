package io.xdag.xdagwallet.wrapper;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.PrintStream;
import java.util.List;

import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.config.XdagConfig;
import io.xdag.xdagwallet.core.Address;
import io.xdag.xdagwallet.core.Block;
import io.xdag.xdagwallet.core.BlockBuilder;
import io.xdag.xdagwallet.crypto.ECKeyPair;
import io.xdag.xdagwallet.crypto.Keys;
import io.xdag.xdagwallet.crypto.MnemonicUtils;
import io.xdag.xdagwallet.crypto.SecureRandomUtils;
import io.xdag.xdagwallet.util.BasicUtils;
import io.xdag.xdagwallet.util.BytesUtils;
import io.xdag.xdagwallet.util.StringUtils;
import io.xdag.xdagwallet.util.XdagTime;
import io.xdag.xdagwallet.wallet.Wallet;

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
        return 0;
    }


    public int XdagDisConnectFromPool() {
        return 0;
    }


//    public int XdagXferToAddress(String address, String amount,String remark) {
//        return XdagXfer(address, amount,remark);
//    }
public void XdagXferToAddress(ECKeyPair keyPair,String address,String amount,String remark){
    long xdagTime = XdagTime.getCurrentTimestamp();
    Address from = new Address(BasicUtils.address2Hash(Config.getAddress()));
    Address to  = new Address(BasicUtils.address2Hash(address));
    double amount1 = StringUtils.getDouble(amount);
    long amount2 = BasicUtils.xdag2amount(amount1);
    Block block = BlockBuilder.generateTransactionBlock(keyPair,xdagTime,from,to,amount2,remark);
    Log.i("Transaction","New BlockAddress:" + BytesUtils.toHexString(block.getXdagBlock().getData()));
}

    public int XdagWrapperInit() {
        return 0;
    }


    public int XdagWrapperUnInit() {
        return 0;
    }


//    public int XdagNotifyMsg() {
//        return XdagNotifyMsg("");
//    }
//
//    public int XdagNotifyMsg(String authInfo) {
//        return XdagNotifyNativeMsg(authInfo);
//    }
    public int XdagNotifyMsg(String authInfo) {
return 0;
    }



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
