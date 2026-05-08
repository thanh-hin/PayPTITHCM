package com.ptithcm.payptithcm.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    private static final String PREF_NAME    = "TuitionPrefs";
    private static final String KEY_MSSV     = "MSSV";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences sharedPreferences;

    public SharedPrefs(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /** Lưu session sau khi đăng nhập thành công */
    public void saveUser(String mssv) {
        sharedPreferences.edit()
                .putString(KEY_MSSV, mssv)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    /** Lấy MSSV đang đăng nhập */
    public String getUser() {
        return sharedPreferences.getString(KEY_MSSV, "");
    }

    /** Kiểm tra đã đăng nhập chưa */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false)
                && !getUser().isEmpty();
    }

    /** Xoá toàn bộ session (đăng xuất) */
    public void clearUser() {
        sharedPreferences.edit().clear().apply();
    }
}
