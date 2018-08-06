package io.xdag.xdagwallet.api.xdagscan;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * created by lxm on 2018/7/6.
 *
 * desc :
 */
public interface XdagScanApi {

    /**
     * get the block detail
     *
     * @param address the block
     */
    @GET("{address}")
    Observable<BlockDetailModel> getBlockDetail(@Path("address") String address);

}
