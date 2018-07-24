package io.xdag.xdagwallet.api.xdagscan;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * created by lxm on 2018/7/6.
 *
 * desc :  http://139.99.120.77:8888/apidoc/
 */
public interface XdagScanApi {

    /**
     * get the block detail
     *
     * @param address the block
     */
    @GET("api/block/{address}")
    Observable<BlockDetailModel> getBlockDetail(@Path("address") String address);

}
