package io.xdag.common.http;

import com.google.gson.Gson;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * create thr retrofit client
 */
public class RetrofitClient {

    private final Retrofit.Builder builder;


    RetrofitClient() {

        builder = new Retrofit.Builder()
            // gson
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            // rxjava2 adapter set request nei in io thread
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()));

    }


    public Retrofit build(OkHttpClient client, String baseUrl) {
        return builder
            .baseUrl(baseUrl)
            .client(client)
            .build();
    }
}
