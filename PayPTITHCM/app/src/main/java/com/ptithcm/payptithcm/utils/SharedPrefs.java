package com.ptithcm.payptithcm.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    private static final String PREF_NAME   = "TuitionPrefs";
    private static final String KEY_MSSV    = "MSSV";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_TOKEN   = "TOKEN";
    private static final String KEY_EMAIL   = "EMAIL";
    private static final String KEY_ROLE    = "ROLE";

    private final SharedPreferences sharedPreferences;

    public SharedPrefs(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String mssv) {
        sharedPreferences.edit()
                .putString(KEY_MSSV, mssv)
                .putString(KEY_ROLE, "STUDENT")
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public void saveUser(String userId, String role) {
        sharedPreferences.edit()
                .putString(KEY_MSSV, userId)
                .putString(KEY_ROLE, role)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public void saveSession(String mssv, String token, String email) {
        sharedPreferences.edit()
                .putString(KEY_MSSV, mssv)
                .putString(KEY_TOKEN, token)
                .putString(KEY_EMAIL, email)
                .putString(KEY_ROLE, "STUDENT")
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public String getUser() {
        return sharedPreferences.getString(KEY_MSSV, "");
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, "STUDENT");
    }

    public boolean isAccountant() {
        return "ACCOUNTANT".equals(getRole());
    }

    /** Trả về "Bearer <token>" cho Retrofit header */
    public String getBearerToken() {
        String token = getToken();
        return token.isEmpty() ? "" : "Bearer " + token;
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false)
                && !getUser().isEmpty();
    }

    public boolean hasToken() {
        return !getToken().isEmpty();
    }

    public void clearUser() {
        sharedPreferences.edit().clear().apply();
    }
}
