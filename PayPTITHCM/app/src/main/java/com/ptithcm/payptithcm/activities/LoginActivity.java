package com.ptithcm.payptithcm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.models.Student;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_GOOGLE_SIGN_IN = 1001;

    EditText etIdentifier, etPassword, etTotp;
    TextView btnSendOtp; // Changed from Button to TextView to match XML
    Button btnLogin;
    View btnGoogleLogin;
    TextView tvGeneratedOtp;
    SharedPrefs prefs;
    GoogleSignInClient googleSignInClient;
    String currentOtp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new SharedPrefs(this);
        prefs.clearUser();

        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        etIdentifier = findViewById(R.id.etMSSV);
        etPassword = findViewById(R.id.etPassword);
        etTotp = findViewById(R.id.etTotp);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        tvGeneratedOtp = findViewById(R.id.tvGeneratedOtp);

        if (etIdentifier != null) {
            etIdentifier.setText("21520001");
        }

        if (etPassword != null) {
            etPassword.setText("21520001");
        }

        generateAndShowOtp();

        if (btnSendOtp != null) {
            btnSendOtp.setOnClickListener(v -> generateAndShowOtp());
        }

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> doLogin());
        }

        if (btnGoogleLogin != null) {
            btnGoogleLogin.setOnClickListener(v -> signInWithGoogle());
        }
    }

    private void generateAndShowOtp() {
        currentOtp = String.format("%06d", new Random().nextInt(1000000));

        if (tvGeneratedOtp != null) {
            tvGeneratedOtp.setText(currentOtp);
            tvGeneratedOtp.setVisibility(View.VISIBLE);
        }

        if (etTotp != null) {
            etTotp.setText(currentOtp);
        }

        Toast.makeText(this, "Mã OTP mới: " + currentOtp, Toast.LENGTH_SHORT).show();
    }

    private void doLogin() {
        String identifier = etIdentifier.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String otp = etTotp.getText().toString().trim();

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

    private void signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account == null || account.getEmail() == null) {
                    Toast.makeText(this, "Không lấy được email Google", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = account.getEmail();
                loginByGoogleEmail(email);

            } catch (ApiException e) {
                Toast.makeText(this, "Lỗi Google Sign-In: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loginByGoogleEmail(String email) {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Student student = db.authenticateByEmail(email);

        if (student == null) {
            Toast.makeText(this, "Email Google chưa được đăng ký: " + email, Toast.LENGTH_LONG).show();
            return;
        }

        prefs.saveUser(student.getStudentId());
        Toast.makeText(this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
