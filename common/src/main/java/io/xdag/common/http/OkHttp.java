package io.xdag.common.http;

import com.readystatesoftware.chuck.ChuckInterceptor;

import java.util.Collections;

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

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
            .build();

        builder = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            // .connectionSpecs(Collections.singletonList(spec))
        ;

        if (Common.isDebug()) {
            // chuck
            builder.addInterceptor(new ChuckInterceptor(Common.getContext()));
        }

    }


    public OkHttpClient build() {
        return builder.build();
    }
}
