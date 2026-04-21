package com.ptithcm.payptithcm.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.payptithcm.R;

import java.util.Random;

public class MomoPaymentActivity extends AppCompatActivity {

    TextView tvAmount, tvFeeName;
    EditText etPhone;
    Button btnConfirmMomo, btnCancelMomo;
    boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momo_payment);

        tvAmount     = findViewById(R.id.tvMomoAmount);
        tvFeeName    = findViewById(R.id.tvMomoFeeName);
        etPhone      = findViewById(R.id.etMomoPhone);
        btnConfirmMomo = findViewById(R.id.btnConfirmMomo);
        btnCancelMomo  = findViewById(R.id.btnCancelMomo);

        long totalAmount = getIntent().getLongExtra("TOTAL_AMOUNT", 0);
        String feeName = getIntent().getStringExtra("FEE_DETAIL");

        tvAmount.setText(String.format("%,d đ", totalAmount));
        tvFeeName.setText(feeName != null ? feeName : "");

        btnCancelMomo.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        btnConfirmMomo.setOnClickListener(v -> {
            if (isProcessing) return;
            String phone = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phone) || phone.length() != 10) {
                etPhone.setError("Vui lòng nhập số điện thoại MoMo hợp lệ (10 số)");
                etPhone.requestFocus();
                return;
            }
            showProcessingDialog(phone, totalAmount);
        });
    }

    private void showProcessingDialog(String phone, long amount) {
        isProcessing = true;
        btnConfirmMomo.setEnabled(false);

        // Hiện dialog loading
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_processing, null);
        AlertDialog loadingDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        loadingDialog.show();

        // Giả lập 2 giây xử lý
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadingDialog.dismiss();

            // 90% thành công
            boolean success = new Random().nextInt(10) < 9;

            if (success) {
                String txnId = "MOMO" + System.currentTimeMillis();
                new AlertDialog.Builder(this)
                        .setTitle("✅ Thanh toán MoMo thành công!")
                        .setMessage("Số tiền: " + String.format("%,d đ", amount)
                                + "\nTừ SĐT: " + phone
                                + "\nMã GD: " + txnId)
                        .setPositiveButton("OK", (d, w) -> {
                            setResult(RESULT_OK);
                            finish();
                        })
                        .setCancelable(false)
                        .show();
            } else {
                isProcessing = false;
                btnConfirmMomo.setEnabled(true);
                new AlertDialog.Builder(this)
                        .setTitle("❌ Thanh toán thất bại")
                        .setMessage("Giao dịch không thành công. Vui lòng kiểm tra số dư MoMo và thử lại.")
                        .setPositiveButton("Thử lại", null)
                        .setNegativeButton("Huỷ", (d, w) -> {
                            setResult(RESULT_CANCELED);
                            finish();
                        })
                        .show();
            }
        }, 2000);
    }
}
