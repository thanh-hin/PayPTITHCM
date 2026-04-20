package com.ptithcm.payptithcm.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    private static final String PREF_NAME = "TuitionPrefs";
    private static final String KEY_MSSV = "MSSV";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    private SharedPreferences sharedPreferences;

    public SharedPrefs(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String mssv) {
        sharedPreferences.edit()
                .putString(KEY_MSSV, mssv)
                .putBoolean(KEY_LOGGED_IN, true)
                .commit();
    }

    public String getUser() {
        return sharedPreferences.getString(KEY_MSSV, "");
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false)
                && !getUser().isEmpty();
    }

    public void clearUser() {
        sharedPreferences.edit().clear().commit();
    }
}
