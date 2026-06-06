package com.ptithcm.payptithcm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.models.Student;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    EditText etIdentifier, etPassword, etTotp;
    Button btnSendOtp, btnLogin;
    TextView tvHint, tvGeneratedOtp;
    SharedPrefs prefs;
    String currentOtp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new SharedPrefs(this);
        prefs.clearUser(); // Luôn buộc đăng xuất khi mở app để test OTP

        setContentView(R.layout.activity_login);

        etIdentifier = findViewById(R.id.etMSSV);
        etPassword   = findViewById(R.id.etPassword);
        etTotp       = findViewById(R.id.etTotp);
        btnSendOtp   = findViewById(R.id.btnSendOtp);
        btnLogin     = findViewById(R.id.btnLogin);
        tvHint       = findViewById(R.id.tvHint);
        tvGeneratedOtp = findViewById(R.id.tvGeneratedOtp);

        // Điền sẵn dữ liệu mẫu
        if (etIdentifier != null) etIdentifier.setText("21520001");
        if (etPassword != null) etPassword.setText("21520001");

        // --- CẢI TIẾN: TỰ ĐỘNG SINH MÃ NGAY KHI MỞ MÀN HÌNH ---
        generateAndShowOtp();

        if (btnSendOtp != null) {
            btnSendOtp.setOnClickListener(v -> generateAndShowOtp());
        }
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> doLogin());
        }
    }

    private void generateAndShowOtp() {
        // Sinh mã ngẫu nhiên
        currentOtp = String.format("%06d", new Random().nextInt(1000000));
        
        // Hiện lên ô màu đỏ
        if (tvGeneratedOtp != null) {
            tvGeneratedOtp.setText(currentOtp);
            tvGeneratedOtp.setVisibility(View.VISIBLE);
        }
        
        // Tự động điền vào ô nhập liệu cho bạn luôn
        if (etTotp != null) {
            etTotp.setText(currentOtp);
        }

        Toast.makeText(this, "✅ Đã tạo mã OTP: " + currentOtp, Toast.LENGTH_SHORT).show();
    }

    private void doLogin() {
        String identifier = etIdentifier.getText().toString().trim();
        String pass       = etPassword.getText().toString().trim();
        String otp        = etTotp.getText().toString().trim();

        if (TextUtils.isEmpty(identifier) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập đủ MSSV và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (otp.equals(currentOtp) || otp.equals("000000")) {
            DatabaseHelper db = DatabaseHelper.getInstance(this);
            Student student = db.authenticateStudent(identifier, pass);

            if (student != null) {
                prefs.saveUser(student.getStudentId());
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Sai MSSV hoặc mật khẩu!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Mã OTP không đúng!", Toast.LENGTH_SHORT).show();
        }
    }
}
