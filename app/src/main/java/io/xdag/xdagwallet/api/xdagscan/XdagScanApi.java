package io.xdag.xdagwallet.api.xdagscan;

import io.reactivex.Observable;
import io.xdag.xdagwallet.api.ApiServer;
import io.xdag.xdagwallet.api.xdagscan.BlockDetailModel;
import io.xdag.xdagwallet.api.xdagscan.BaseResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * created by lxm on 2018/7/6.
 *
 * desc :  http://139.99.120.77:8888/apidoc/
 */
public interface XdagScanApi {

    String BLOCK_DETAIL = "api/blocks/detail";

    /**
     * get the block detail
     *
     * @param address the block
     * @param pageno current page form 1
     */
    @GET(BLOCK_DETAIL)
    Observable<BaseResponse<BlockDetailModel>> getBlockDetail(
        @Query("address") String address,
        @Query("pageno") int pageno,
        @Query("pagesize") int pagesize);

}
