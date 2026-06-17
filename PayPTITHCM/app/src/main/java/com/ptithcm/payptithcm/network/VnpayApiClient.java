package com.ptithcm.payptithcm.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VnpayApiClient {
    private static final String BASE_URL = "http://10.0.2.2:3001/";

    private static Retrofit retrofit = null;
    private static VnpayApiService service = null;

    public static VnpayApiService getService() {
        if (service == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(VnpayApiService.class);
        }

        return service;
    }
}