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

        // Da dang nhap roi thi vao thang MainActivity
        prefs = new SharedPrefs(this);
        if (prefs.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etMSSV     = findViewById(R.id.etMSSV);
        etPassword = findViewById(R.id.etPassword);
        etTotp     = findViewById(R.id.etTotp);
        btnLogin   = findViewById(R.id.btnLogin);
        tvSystemOTP= findViewById(R.id.tvSystemOTP);

        // Sinh OTP ngau nhien 6 chu so
        refreshOTP();

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String mssv     = etMSSV.getText().toString().trim();
        String pass     = etPassword.getText().toString().trim();
        String otpInput = etTotp.getText().toString().trim();

        // --- Validate ---
        if (TextUtils.isEmpty(mssv)) {
            etMSSV.setError("Vui long nhap MSSV");
            etMSSV.requestFocus(); return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Vui long nhap mat khau");
            etPassword.requestFocus(); return;
        }
        if (TextUtils.isEmpty(otpInput)) {
            etTotp.setError("Vui long nhap ma OTP");
            etTotp.requestFocus(); return;
        }

        // --- Kiem tra OTP ---
        if (!otpInput.equals(generatedOTP)) {
            etTotp.setError("Ma OTP khong dung!");
            refreshOTP();          // cap nhat OTP moi ngay
            etTotp.setText("");
            Toast.makeText(this, "OTP sai! Da cap nhat OTP moi.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Kiem tra MSSV + mat khau trong SQLite ---
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Student student = db.authenticateStudent(mssv, pass);

        if (student != null) {
            prefs.saveUser(mssv);
            Toast.makeText(this, "Xin chao, " + student.getFullName() + "!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "MSSV hoac mat khau khong dung!", Toast.LENGTH_LONG).show();
            refreshOTP();
            etTotp.setText("");
        }
    }

    private void refreshOTP() {
        generatedOTP = String.valueOf(100000 + new Random().nextInt(900000));
        tvSystemOTP.setText(generatedOTP);
    }
}
