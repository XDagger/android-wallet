package io.xdag.common.http;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.readystatesoftware.chuck.ChuckInterceptor;
import io.xdag.common.Common;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

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
            .retryOnConnectionFailure(true)
            // add log interceptor
            .addInterceptor(getLogInterceptor())
            // set Cookie
            .cookieJar(new PersistentCookieJar(new SetCookieCache(),
                new SharedPrefsCookiePersistor(Common.getContext())));

        if (Common.isDebug()) {
            // chuck
            builder.addInterceptor(new ChuckInterceptor(Common.getContext()));
        }

    }


    public OkHttpClient build() {
        return builder.build();
    }


    private Interceptor getLogInterceptor() {
        return new HttpLoggingInterceptor()
            .setLevel(Common.isDebug()
                      ? HttpLoggingInterceptor.Level.BODY
                      : HttpLoggingInterceptor.Level.NONE);
    }
}
