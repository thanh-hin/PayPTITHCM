package com.ptithcm.payptithcm.network.models;

public class MomoPaymentRequest {
    public long amount;
    public String orderInfo;
    public String email;

    public MomoPaymentRequest(long amount, String orderInfo, String email) {
        this.amount = amount;
        this.orderInfo = orderInfo;
        this.email = email;
    }
}