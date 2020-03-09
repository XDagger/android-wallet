package io.xdag.xdagwallet.net;

import android.app.Activity;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.xdag.xdagwallet.model.BlockDetailModel;
import io.xdag.xdagwallet.model.ConfigModel;
import io.xdag.xdagwallet.model.VersionModel;
import io.xdag.xdagwallet.net.error.ErrorConsumer;
import io.xdag.xdagwallet.net.rx.Detail2AddressListFunction;
import io.xdag.xdagwallet.net.rx.Detail2TranListFunction;

/**
 * created by lxm on 2018/8/31.
 */
public class HttpRequest {

    private static final HttpRequest sInstance = new HttpRequest();


    public static HttpRequest get() {
        return sInstance;
    }


    private HttpRequest() {
    }


    public Disposable getConfigInfo(Consumer<ConfigModel> consumer) {
        return ApiServer.createConfigApi().getConfigInfo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer, new ErrorConsumer());
    }


    public Disposable getVersionInfo(Consumer<VersionModel> consumer) {
        return ApiServer.createConfigApi().getVersionInfo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer, new ErrorConsumer());
    }


    public Disposable getTransactions(Activity activity, String address, Consumer<List<BlockDetailModel.BlockAsAddress>> consumer) {
        return ApiServer.createTransactionApi().getBlockDetail(address)
            .observeOn(AndroidSchedulers.mainThread())
            .map(new Detail2AddressListFunction())
            .subscribe(consumer, new ErrorConsumer(activity));
    }


    public Disposable getTransactionDetail(Activity activity, String address, Consumer<List<BlockDetailModel.BlockAsAddress>> consumer) {
        return ApiServer.createTransactionApi().getBlockDetail(address)
            .observeOn(AndroidSchedulers.mainThread())
            .map(new Detail2TranListFunction())
            .subscribe(consumer, new ErrorConsumer(activity));
    }

}
