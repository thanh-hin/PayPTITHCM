package com.ptithcm.payptithcm.models;

public class FeeItem {
    private int id;
    private String name;
    private long amount;
    private String status;    // UNPAID, PAID, OVERDUE
    private String deadline;
    private boolean selected;

    public FeeItem() {}

    public FeeItem(int id, String name, long amount, String status, String deadline) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.status = status;
        this.deadline = deadline;
        this.selected = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
