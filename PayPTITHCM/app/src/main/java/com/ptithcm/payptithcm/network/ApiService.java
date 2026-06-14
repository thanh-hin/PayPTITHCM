package com.ptithcm.payptithcm.network;

import com.ptithcm.payptithcm.network.models.ApiResponse;
import com.ptithcm.payptithcm.network.models.ContactResponse;
import com.ptithcm.payptithcm.network.models.FeesResponse;
import com.ptithcm.payptithcm.network.models.LoginRequest;
import com.ptithcm.payptithcm.network.models.LoginResponse;
import com.ptithcm.payptithcm.network.models.OtpRequest;
import com.ptithcm.payptithcm.network.models.PaymentRequest;
import com.ptithcm.payptithcm.network.models.PaymentsResponse;
import com.ptithcm.payptithcm.network.models.StudentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/send-otp")
    Call<ApiResponse> sendOtp(@Body OtpRequest request);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/students/{id}")
    Call<StudentResponse> getStudent(
            @Header("Authorization") String bearerToken,
            @Path("id") String studentId);

    @GET("api/fees/{studentId}")
    Call<FeesResponse> getFees(
            @Header("Authorization") String bearerToken,
            @Path("studentId") String studentId);

    @GET("api/payments/{studentId}")
    Call<PaymentsResponse> getPayments(
            @Header("Authorization") String bearerToken,
            @Path("studentId") String studentId);

    @POST("api/payments")
    Call<ApiResponse> postPayment(
            @Header("Authorization") String bearerToken,
            @Body PaymentRequest request);

    @GET("api/contact")
    Call<ContactResponse> getContact();
}
