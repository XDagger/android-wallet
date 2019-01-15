package io.xdag.common.http;

import com.readystatesoftware.chuck.ChuckInterceptor;

import io.xdag.common.Common;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

/**
 * create the okhttp client
 */
public class OkHttp {

    private final OkHttpClient.Builder builder;


    OkHttp() {
        builder = new OkHttpClient.Builder()
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
