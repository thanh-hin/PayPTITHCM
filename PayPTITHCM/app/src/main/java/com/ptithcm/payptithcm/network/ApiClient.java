package com.ptithcm.payptithcm.network;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // 10.0.2.2 = localhost của máy host khi chạy từ Android Emulator
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    private static Retrofit instance;

    public static synchronized ApiService getService() {
        if (instance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(msg -> Log.d("API", msg));
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .build();

            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance.create(ApiService.class);
    }
}
