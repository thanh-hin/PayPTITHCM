package com.ptithcm.payptithcm.network;

import com.ptithcm.payptithcm.network.models.VnpayCreateRequest;
import com.ptithcm.payptithcm.network.models.VnpayCreateResponse;
import com.ptithcm.payptithcm.network.models.VnpayStatusResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VnpayApiService {
    @POST("api/payments/create-vnpay")
    Call<VnpayCreateResponse> createVnpayPayment(
            @Body VnpayCreateRequest request
    );

    @GET("api/payments/vnpay-status/{txnRef}")
    Call<VnpayStatusResponse> getVnpayStatus(
            @Path("txnRef") String txnRef
    );
}