package io.xdag.xdagwallet.api;

import io.reactivex.Observable;
import io.xdag.xdagwallet.api.response.XdagScanBlockDetail;
import io.xdag.xdagwallet.api.response.XdagScanResp;
import retrofit2.http.GET;
import retrofit2.http.Query;

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
     * @param pageno current page
     * @param pagesize page size
     */
    @GET(XdagScanServer.BLOCK_DETAIL)
    Observable<XdagScanResp<XdagScanBlockDetail>> getBlockDetail(
        @Query("address") String address,
        @Query("pageno") int pageno,
        @Query("pagesize") int pagesize);

}
