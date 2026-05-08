package com.ptithcm.payptithcm.models;

public class HistoryItem {
    private int paymentId;
    private String date;
    private String feeName;
    private long amount;
    private String status;
    private String method;
    private String transactionId;

    public HistoryItem() {}

    public HistoryItem(int paymentId, String date, String feeName, long amount,
                       String status, String method, String transactionId) {
        this.paymentId = paymentId;
        this.date = date;
        this.feeName = feeName;
        this.amount = amount;
        this.status = status;
        this.method = method;
        this.transactionId = transactionId;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getFeeName() { return feeName; }
    public void setFeeName(String feeName) { this.feeName = feeName; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
