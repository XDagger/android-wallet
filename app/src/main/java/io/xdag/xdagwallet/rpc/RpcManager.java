package io.xdag.xdagwallet.rpc;

import android.util.Log;

import org.web3j.protocol.http.HttpService;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.rpc.error.WebErrorConsumer;
import io.xdag.xdagwallet.rpc.response.XdagSendTransaction;

public class RpcManager {
    private static final RpcManager sInstance = new RpcManager();

    public static RpcManager get() {
        return sInstance;
    }

    public Disposable sendXfer(String transaction){
        return Observable.just(transaction)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        WebXdag web = Web3XdagFactory.build(new HttpService(Config.POOL_TEST));
                        XdagSendTransaction tra = web.xdagSendRawTransaction(transaction).send();
                        Log.i("交易:",tra.getTransactionHash());
                        return tra.getTransactionHash();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::add,new WebErrorConsumer());
    }
    private void add(String address){
        System.out.println(address);
    }
}
