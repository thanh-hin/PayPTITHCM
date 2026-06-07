package com.ptithcm.payptithcm.network.models;

import java.util.List;

public class PaymentsResponse {
    public boolean success;
    public List<PaymentData> payments;

    public static class PaymentData {
        public int paymentId;
        public String date;
        public String feeName;
        public long amount;
        public String method;
        public String status;
        public String transactionId;
    }
}
