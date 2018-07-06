package io.xdag.xdagwallet.api;

import io.xdag.common.http.ApiFactory;

/**
 * created by lxm on 2018/7/6.
 *
 * desc : http://139.99.120.77:8888/apidoc/
 */
public class XdagScanServer {

    private static final String BASE_URL = "http://139.99.120.77:8888/";
    static final String BLOCK_DETAIL = "api/blocks/detail";


    public static XdagScanApi getApi() {
        return ApiFactory.getInstance().createApi(BASE_URL, XdagScanApi.class);
    }
}
