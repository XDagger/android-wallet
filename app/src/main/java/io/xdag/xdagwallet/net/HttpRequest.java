package io.xdag.xdagwallet.net;

import android.app.Activity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.model.BlockDetailModel;
import io.xdag.xdagwallet.model.ConfigModel;
import io.xdag.xdagwallet.model.VersionModel;
import io.xdag.xdagwallet.net.error.ErrorConsumer;
import io.xdag.xdagwallet.net.error.NoTransactionException;
import io.xdag.xdagwallet.net.rx.Detail2AddressListFunction;
import io.xdag.xdagwallet.net.rx.Detail2TranListFunction;
import io.xdag.xdagwallet.util.AlertUtil;
import java.util.List;

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


    public Disposable getBlockList(Activity activity, String address, Consumer<List<BlockDetailModel.BlockAsAddress>> consumer) {
        String baseUrl = Config.getTransactionHost();
        return ApiServer.createTransactionApi(baseUrl).getBlockDetail(address)
            .observeOn(AndroidSchedulers.mainThread())
            .map(new Detail2AddressListFunction())
            .subscribe(consumer, throwable -> {

                // no transaction
                if (throwable instanceof NoTransactionException) {
                    AlertUtil.show(activity, throwable.getMessage());
                    return;
                }

                // if failed request api2 again
                ApiServer.createTransactionApi(ApiServer.BASE_URL_TRANSACTION2)
                    .getBlockDetail(address)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Detail2AddressListFunction())
                    .subscribe(consumer, new ErrorConsumer(activity));
            });
    }

    public Disposable getBlockDetail(Activity activity, String address, Consumer<List<BlockDetailModel.BlockAsAddress>> consumer) {
        String baseUrl = Config.getTransactionHost();
        return ApiServer.createTransactionApi(baseUrl).getBlockDetail(address)
            .observeOn(AndroidSchedulers.mainThread())
            .map(new Detail2TranListFunction())
            .subscribe(consumer, throwable -> {

                // no transaction
                if (throwable instanceof NoTransactionException) {
                    AlertUtil.show(activity, throwable.getMessage());
                    return;
                }

                // if failed request api2 again
                ApiServer.createTransactionApi(ApiServer.BASE_URL_TRANSACTION2)
                    .getBlockDetail(address)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Detail2TranListFunction())
                    .subscribe(consumer, new ErrorConsumer(activity));
            });
    }

}
