package com.ptithcm.payptithcm.network.models;

import java.util.List;

public class VnpayCreateRequest {
    public String studentId;
    public List<Integer> feeIds;
    public long amount;

    public VnpayCreateRequest(String studentId, List<Integer> feeIds, long amount) {
        this.studentId = studentId;
        this.feeIds = feeIds;
        this.amount = amount;
    }
}