package com.ptithcm.payptithcm.models;

public class HistoryItem {
    private String date;    // Ngày thanh toán [cite: 21]
    private String feeName; // Khoản phí [cite: 22]
    private long amount;    // Số tiền [cite: 23]
    private String result;  // Trạng thái giao dịch [cite: 24]

    public HistoryItem(String date, String feeName, long amount, String result) {
        this.date = date;
        this.feeName = feeName;
        this.amount = amount;
        this.result = result;
    }
    // Getters...
}