package com.ptithcm.payptithcm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.models.Student;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

public class LoginActivity extends AppCompatActivity {

    EditText etIdentifier, etPassword;
    Button btnLogin;
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
        btnLogin     = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String identifier = etIdentifier.getText().toString().trim();
        String pass       = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(identifier)) {
            etIdentifier.setError("Vui lòng nhập MSSV hoặc Email");
            etIdentifier.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Student student;
        if (identifier.contains("@")) {
            student = db.authenticateByEmail(identifier, pass);
        } else {
            student = db.authenticateStudent(identifier, pass);
        }

        btnLogin.setEnabled(true);
        btnLogin.setText("Đăng nhập");

        if (student != null) {
            prefs.saveUser(student.getStudentId());
            Toast.makeText(this, "Xin chào, " + student.getFullName() + "!", Toast.LENGTH_SHORT).show();
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
