package io.xdag.xdagwallet.api;

import io.reactivex.Observable;
import io.xdag.xdagwallet.model.ConfigModel;
import io.xdag.xdagwallet.model.VersionModel;
import retrofit2.http.GET;

/**
 * created by lxm on 2018/7/29.
 */
public interface Api {

    /**
     * get update info
     *
     * @return {@link VersionModel}
     */
    @GET("ssyijiu/android-wallet/master/update.json")
    Observable<VersionModel> getVersionInfo();

    /**
     * get config info
     *
     * @return {@link ConfigModel}
     */
    @GET("ssyijiu/android-wallet/master/config.json")
    Observable<ConfigModel> getConfigInfo();

}
