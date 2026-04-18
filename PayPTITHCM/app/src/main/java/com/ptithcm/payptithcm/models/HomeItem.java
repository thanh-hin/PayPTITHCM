package com.ptithcm.payptithcm.models;

public class HomeItem {
    private String title; // Tên chức năng (Học phí, Thanh toán...)
    private int icon;     // ID của icon trong drawable (R.drawable.ic_...)

    public HomeItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    // Getter để Adapter có thể lấy dữ liệu ra hiển thị
    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    // Setter (nếu sau này Hiền muốn thay đổi dữ liệu động)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}