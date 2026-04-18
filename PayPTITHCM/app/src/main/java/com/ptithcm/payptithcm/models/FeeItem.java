package com.ptithcm.payptithcm.models;

public class FeeItem {
    private String name;    // Tên khoản phí [cite: 14]
    private long amount;    // Số tiền [cite: 15]
    private String status;  // Trạng thái (Đã đóng/Chưa đóng) [cite: 16]

    public FeeItem(String name, long amount, String status) {
        this.name = name;
        this.amount = amount;
        this.status = status;
    }

    public String getName() { return name; }
    public long getAmount() { return amount; }
    public String getStatus() { return status; }
}