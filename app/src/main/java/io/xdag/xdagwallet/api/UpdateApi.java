package io.xdag.xdagwallet.api;

import io.reactivex.Observable;
import io.xdag.xdagwallet.model.UpdateModel;
import retrofit2.http.GET;

/**
 * created by lxm on 2018/7/29.
 */
public interface UpdateApi {


    /**
     * get update info
     */
    @GET("ssyijiu/android-wallet/master/update.json")
    Observable<UpdateModel> getVersionInfo();

}
