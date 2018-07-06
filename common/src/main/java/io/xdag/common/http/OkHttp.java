package io.xdag.common.http;

import com.readystatesoftware.chuck.ChuckInterceptor;
import io.xdag.common.Common;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

/**
 * create the okhttp client
 */
public class OkHttp {

    private static final int TIME_OUT_CONNECT = 15;
    private static final int TIME_OUT_READ = 15;
    private static final int TIME_OUT_WRITE = 15;

    private final OkHttpClient.Builder builder;


    OkHttp() {
        builder = new OkHttpClient.Builder()
            // set time out
            .connectTimeout(TIME_OUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT_WRITE, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true);
        if (Common.isDebug()) {
            // chuck
            builder.addInterceptor(new ChuckInterceptor(Common.getContext()));
        }

    }


    public OkHttpClient build() {
        return builder.build();
    }
}
