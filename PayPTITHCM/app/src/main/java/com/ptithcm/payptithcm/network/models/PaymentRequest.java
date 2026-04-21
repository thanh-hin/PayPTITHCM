package com.ptithcm.payptithcm.network.models;

import java.util.List;

public class PaymentRequest {
    public String studentId;
    public List<Integer> feeIds;
    public String method;

    public PaymentRequest(String studentId, List<Integer> feeIds, String method) {
        this.studentId = studentId;
        this.feeIds = feeIds;
        this.method = method;
    }
}
