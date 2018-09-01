package io.xdag.common.http;

import okhttp3.OkHttpClient;

/**
 * the common api factory
 */
public class ApiFactory {

    // single instance
    private static class Lazy {
        static final ApiFactory API_FACTORY = new ApiFactory();
        static final RetrofitClient RETROFIT = new RetrofitClient();
        static final OkHttpClient OK_HTTP = new OkHttp().build();
    }

    private ApiFactory() {
    }


    public static ApiFactory getInstance() {
        return Lazy.API_FACTORY;
    }


    public <T> T createApi(String baseUrl, Class<T> clazz) {
        return Lazy.RETROFIT.build(Lazy.OK_HTTP, baseUrl).create(clazz);
    }
}
