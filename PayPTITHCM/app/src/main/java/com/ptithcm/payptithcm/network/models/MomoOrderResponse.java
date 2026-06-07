package com.ptithcm.payptithcm.network.models;

public class MomoOrderResponse {
    public boolean success;
    public String message;
    public MomoOrderData order;

    public static class MomoOrderData {
        public String orderId;
        public String amount;
        public String userEmail;
        public String status;
    }
}