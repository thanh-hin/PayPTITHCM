package com.ptithcm.payptithcm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.models.Student;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    EditText etMSSV, etPassword, etTotp;
    Button btnLogin;
    TextView tvSystemOTP;
    SharedPrefs prefs;
    String generatedOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new SharedPrefs(this);
        if (prefs.isLoggedIn()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        etMSSV      = findViewById(R.id.etMSSV);
        etPassword  = findViewById(R.id.etPassword);
        etTotp      = findViewById(R.id.etTotp);
        btnLogin    = findViewById(R.id.btnLogin);
        tvSystemOTP = findViewById(R.id.tvSystemOTP);

        refreshOTP();

        btnLogin.setOnClickListener(v -> doLogin());

        // Click vao OTP label de lam moi OTP
        tvSystemOTP.setOnClickListener(v -> refreshOTP());
    }

    private void doLogin() {
        String mssv     = etMSSV.getText().toString().trim();
        String pass     = etPassword.getText().toString().trim();
        String otpInput = etTotp.getText().toString().trim();

        // --- Validate MSSV ---
        if (TextUtils.isEmpty(mssv)) {
            etMSSV.setError("Vui lòng nhập MSSV");
            etMSSV.requestFocus();
            return;
        }
        if (mssv.length() < 7 || mssv.length() > 10) {
            etMSSV.setError("MSSV phải có 7-10 ký tự");
            etMSSV.requestFocus();
            return;
        }

        // --- Validate mật khẩu ---
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // --- Validate OTP ---
        if (TextUtils.isEmpty(otpInput)) {
            etTotp.setError("Vui lòng nhập mã OTP");
            etTotp.requestFocus();
            return;
        }
        if (!otpInput.equals(generatedOTP)) {
            etTotp.setError("Mã OTP không đúng!");
            refreshOTP();
            etTotp.setText("");
            Toast.makeText(this, "OTP sai! Đã cập nhật OTP mới.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Kiểm tra MSSV + mật khẩu trong SQLite ---
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Student student = db.authenticateStudent(mssv, pass);

        if (student != null) {
            prefs.saveUser(mssv);
            String name = (student.getFullName() != null && !student.getFullName().isEmpty())
                    ? student.getFullName() : mssv;
            Toast.makeText(this, "Xin chào, " + name + "!", Toast.LENGTH_SHORT).show();
            goToMain();
        } else {
            Toast.makeText(this, "MSSV hoặc mật khẩu không đúng!", Toast.LENGTH_LONG).show();
            refreshOTP();
            etTotp.setText("");
            etPassword.requestFocus();
        }
    }

    private void refreshOTP() {
        generatedOTP = String.valueOf(100000 + new Random().nextInt(900000));
        tvSystemOTP.setText(generatedOTP);
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
