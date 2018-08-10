package io.xdag.xdagwallet.api;

import io.xdag.common.http.ApiFactory;
import io.xdag.xdagwallet.api.xdagscan.XdagScanApi;

/**
 * created by lxm on 2018/7/6.
 * <p>
 * desc :
 */
public class ApiServer {

    private static final String BASE_URL_XDAGSCAN = "https://explorer.xdag.io/api/block/";
    private static final String BASE_URL_XDAGSCAN2 = "http://139.99.124.100/api/block/";
    private static final String BASE_URL_GITHUB = "https://raw.githubusercontent.com/";


    public static XdagScanApi getXdagScanApi() {
        return ApiFactory.getInstance().createApi(BASE_URL_XDAGSCAN, XdagScanApi.class);
    }

    public static XdagScanApi getXdagScanApi2() {
        return ApiFactory.getInstance().createApi(BASE_URL_XDAGSCAN2, XdagScanApi.class);
    }

    public static Api getApi() {
        return ApiFactory.getInstance().createApi(BASE_URL_GITHUB, Api.class);
    }
}
