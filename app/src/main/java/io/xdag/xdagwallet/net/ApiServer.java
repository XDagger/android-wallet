package io.xdag.xdagwallet.net;

import io.xdag.common.http.ApiFactory;
import io.xdag.xdagwallet.net.api.ConfigApi;
import io.xdag.xdagwallet.net.api.TransactionApi;

/**
 * created by lxm on 2018/7/6.
 * <p>
 * desc :
 */
public class ApiServer {

    public static final String BASE_URL_TRANSACTION = "https://explorer.xdag.io/api/block/";
    public static final String BASE_URL_TRANSACTION2 = "http://139.99.124.100/api/block/";
    private static final String BASE_URL_GITHUB = "https://raw.githubusercontent.com/";


    static TransactionApi getTransactionApi(String baseUrl) {
        return ApiFactory.getInstance().createApi(baseUrl, TransactionApi.class);
    }

    static ConfigApi getGitHubApi() {
        return ApiFactory.getInstance().createApi(BASE_URL_GITHUB, ConfigApi.class);
    }
}
