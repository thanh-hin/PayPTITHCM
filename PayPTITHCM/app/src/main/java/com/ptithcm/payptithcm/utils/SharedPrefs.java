package com.ptithcm.payptithcm.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    private static final String PREF_NAME = "TuitionPrefs";
    private SharedPreferences sharedPreferences;

    public SharedPrefs(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String mssv) {
        sharedPreferences.edit().putString("MSSV", mssv).apply();
    }

    public String getUser() {
        return sharedPreferences.getString("MSSV", "");
    }
}