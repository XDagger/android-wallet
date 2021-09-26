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
import io.xdag.xdagwallet.rpc.response.TransactionState;
import io.xdag.xdagwallet.rpc.response.XdagBalance;
import io.xdag.xdagwallet.rpc.response.XdagSendTransaction;

public class RpcManager {
    private static final RpcManager sInstance = new RpcManager();
    private TransactionList transactionList = new TransactionList();
    public static RpcManager get() {
        return sInstance;
    }

    public Observable<String> sendXfer(String transaction){
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
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> getBalance(String address) {
        return Observable.just(address)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String address) throws Exception {
                        WebXdag web = Web3XdagFactory.build(new HttpService(Config.POOL_TEST));
                        XdagBalance w = web.xdagGetBalance(address).sendAsync().get();
                        Log.i("余额:", w.getBalance());
                        return w.getBalance();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> CheckTransactionStatus(String hash){
        return Observable.just(hash)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        WebXdag web = Web3XdagFactory.build(new HttpService(Config.POOL_TEST));
                        TransactionState tra = web.xdagGetTransactionByHash(hash).send();
                        Log.i("交易状态:",hash+"的状态"+tra.getTransactionDTO().state);
                        Log.i("待处理交易数量",String.valueOf(transactionList.getNum()));
                        transactionList.change(hash,tra.getTransactionDTO().state);
                        return tra.getTransactionDTO().state;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public TransactionList getTransactionList(){
        return transactionList;
    }

}
