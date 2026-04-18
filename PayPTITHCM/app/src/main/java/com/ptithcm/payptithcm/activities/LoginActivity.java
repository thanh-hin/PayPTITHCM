package com.ptithcm.payptithcm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.utils.SharedPrefs;

public class LoginActivity extends AppCompatActivity {
    EditText etUser, etPass;
    Button btnLogin;
    SharedPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etMSSV);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        prefs = new SharedPrefs(this);

        btnLogin.setOnClickListener(v -> {
            String mssv = etUser.getText().toString();
            if(!mssv.isEmpty()){
                prefs.saveUser(mssv);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }
}