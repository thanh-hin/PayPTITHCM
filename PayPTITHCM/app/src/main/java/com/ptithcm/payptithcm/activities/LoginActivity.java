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
import com.ptithcm.payptithcm.network.ApiClient;
import com.ptithcm.payptithcm.network.models.ApiResponse;
import com.ptithcm.payptithcm.network.models.LoginRequest;
import com.ptithcm.payptithcm.network.models.LoginResponse;
import com.ptithcm.payptithcm.network.models.OtpRequest;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etIdentifier, etPassword, etTotp;
    Button btnSendOtp, btnLogin;
    TextView tvHint;
    SharedPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new SharedPrefs(this);
        if (prefs.isLoggedIn()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        etIdentifier = findViewById(R.id.etMSSV);
        etPassword   = findViewById(R.id.etPassword);
        etTotp       = findViewById(R.id.etTotp);
        btnSendOtp   = findViewById(R.id.btnSendOtp);
        btnLogin     = findViewById(R.id.btnLogin);
        tvHint       = findViewById(R.id.tvHint);

        btnSendOtp.setOnClickListener(v -> doSendOtp());
        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doSendOtp() {
        String identifier = etIdentifier.getText().toString().trim();
        if (TextUtils.isEmpty(identifier)) {
            etIdentifier.setError("Vui lòng nhập MSSV hoặc Email");
            etIdentifier.requestFocus();
            return;
        }

        btnSendOtp.setEnabled(false);
        btnSendOtp.setText("Đang gửi...");

        // Thử gọi API
        ApiClient.getService().sendOtp(new OtpRequest(identifier))
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        btnSendOtp.setEnabled(true);
                        btnSendOtp.setText("Gửi OTP");
                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            String msg = response.body().message;
                            Toast.makeText(LoginActivity.this, "📧 " + msg, Toast.LENGTH_LONG).show();
                            tvHint.setText("💡 OTP đã gửi đến email của bạn. Xem log server để lấy OTP khi test.");
                            etTotp.requestFocus();
                        } else {
                            String errMsg = response.body() != null ? response.body().message : "Không tìm thấy tài khoản";
                            Toast.makeText(LoginActivity.this, errMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        btnSendOtp.setEnabled(true);
                        btnSendOtp.setText("Gửi OTP");
                        // Offline fallback: login local
                        Toast.makeText(LoginActivity.this,
                                "⚠️ Không kết nối server. Dùng chế độ offline.",
                                Toast.LENGTH_SHORT).show();
                        tvHint.setText("💡 Offline mode: nhập '000000' làm OTP để đăng nhập local.");
                    }
                });
    }

    private void doLogin() {
        String identifier = etIdentifier.getText().toString().trim();
        String pass       = etPassword.getText().toString().trim();
        String otp        = etTotp.getText().toString().trim();

        if (TextUtils.isEmpty(identifier)) {
            etIdentifier.setError("Vui lòng nhập MSSV hoặc Email");
            etIdentifier.requestFocus(); return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus(); return;
        }
        if (TextUtils.isEmpty(otp)) {
            etTotp.setError("Vui lòng nhập mã OTP");
            etTotp.requestFocus(); return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        // Thử login qua API
        ApiClient.getService().login(new LoginRequest(identifier, pass, otp))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Đăng nhập");
                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            LoginResponse body = response.body();
                            String mssv = body.student.studentId;
                            prefs.saveSession(mssv, body.token, body.student.email);
                            Toast.makeText(LoginActivity.this,
                                    "Xin chào, " + body.student.fullName + "!", Toast.LENGTH_SHORT).show();
                            goToMain();
                        } else {
                            String msg = response.body() != null ? response.body().message : "Đăng nhập thất bại";
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Đăng nhập");
                        // Offline fallback: kiểm tra local với OTP = 000000
                        if ("000000".equals(otp)) {
                            doLocalLogin(identifier, pass);
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "⚠️ Không kết nối server. Nhập OTP '000000' để đăng nhập offline.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /** Đăng nhập offline (khi không có server) */
    private void doLocalLogin(String identifier, String pass) {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Student student;
        if (identifier.contains("@")) {
            student = db.authenticateByEmail(identifier, pass);
        } else {
            student = db.authenticateStudent(identifier, pass);
        }

        if (student != null) {
            prefs.saveUser(student.getStudentId());
            String name = (student.getFullName() != null && !student.getFullName().isEmpty())
                    ? student.getFullName() : identifier;
            Toast.makeText(this, "Xin chào, " + name + "! (Offline)", Toast.LENGTH_SHORT).show();
            goToMain();
        } else {
            Toast.makeText(this, "MSSV/Email hoặc mật khẩu không đúng!", Toast.LENGTH_LONG).show();
        }
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
