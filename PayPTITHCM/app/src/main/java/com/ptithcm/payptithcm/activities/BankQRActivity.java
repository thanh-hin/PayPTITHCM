package com.ptithcm.payptithcm.activities;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ptithcm.payptithcm.R;

public class BankQRActivity extends AppCompatActivity {

    TextView tvQrAmount, tvBankName, tvAccountNo, tvAccountName, tvTransferContent, tvCountdown;
    ImageView ivQrCode;
    Button btnConfirmTransfer, btnCancel;

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_qr);

        tvQrAmount       = findViewById(R.id.tvQrAmount);
        tvBankName       = findViewById(R.id.tvBankName);
        tvAccountNo      = findViewById(R.id.tvAccountNo);
        tvAccountName    = findViewById(R.id.tvAccountName);
        tvTransferContent = findViewById(R.id.tvTransferContent);
        tvCountdown      = findViewById(R.id.tvCountdown);
        ivQrCode         = findViewById(R.id.ivQrCode);
        btnConfirmTransfer = findViewById(R.id.btnConfirmTransfer);
        btnCancel        = findViewById(R.id.btnCancelQR);

        long totalAmount = getIntent().getLongExtra("TOTAL_AMOUNT", 0);
        String mssv = getIntent().getStringExtra("MSSV");
        if (mssv == null) mssv = "00000000";
        String feeDetail = getIntent().getStringExtra("FEE_DETAIL");

        String content = buildTransferContent(mssv, feeDetail);
        String qrContent = buildQrContent(totalAmount, mssv, content);

        tvQrAmount.setText(String.format("%,d đ", totalAmount));
        tvBankName.setText("BIDV - Ngân hàng Đầu tư và Phát triển VN");
        tvAccountNo.setText("12310000" + (mssv != null ? mssv : "00000000"));
        tvAccountName.setText("TRUONG DHDTVT TPHCM - PTITHCM");
        tvTransferContent.setText(content);

        generateQR(qrContent);
        startCountdown();

        btnConfirmTransfer.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận đã chuyển khoản?")
                    .setMessage("Vui lòng chuyển khoản đúng số tiền và nội dung trước khi xác nhận.")
                    .setPositiveButton("Đã chuyển", (d, w) -> {
                        if (countDownTimer != null) countDownTimer.cancel();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton("Chưa", null)
                    .show();
        });

        btnCancel.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private String buildTransferContent(String mssv, String feeDetail) {
        if (mssv == null) return "THANH TOAN HOC PHI";
        String normalized = feeDetail != null
                ? feeDetail.toUpperCase()
                          .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "A")
                          .replaceAll("[èéẹẻẽêềếệểễ]", "E")
                          .replaceAll("[ìíịỉĩ]", "I")
                          .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "O")
                          .replaceAll("[ùúụủũưừứựửữ]", "U")
                          .replaceAll("[ỳýỵỷỹ]", "Y")
                          .replaceAll("[đ]", "D")
                          .replaceAll("[^A-Z0-9\\s/]", "")
                          .replaceAll("\\s+", " ")
                          .trim()
                : "HOC PHI";
        // Giữ tối đa 50 ký tự
        if (normalized.length() > 50) normalized = normalized.substring(0, 50);
        return mssv + " " + normalized;
    }

    private String buildQrContent(long amount, String mssv, String content) {
        // Định dạng VietQR đơn giản (plain text để ZXing encode)
        return "BIDV|12310000" + mssv + "|" + amount + "|" + content;
    }

    private void generateQR(String content) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 600, 600);
            ivQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Không thể tạo mã QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCountdown() {
        // Đếm ngược 15 phút
        countDownTimer = new CountDownTimer(15 * 60 * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvCountdown.setText(String.format("Mã QR hết hạn sau: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("⚠️ Mã QR đã hết hạn");
                btnConfirmTransfer.setEnabled(false);
                Toast.makeText(BankQRActivity.this,
                        "Mã QR đã hết hạn. Vui lòng quay lại và tạo mới.", Toast.LENGTH_LONG).show();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
