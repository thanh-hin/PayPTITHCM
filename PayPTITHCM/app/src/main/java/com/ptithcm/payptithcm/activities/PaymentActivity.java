package com.ptithcm.payptithcm.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.models.FeeItem;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    TextView tvFeeDetail, tvTotalAmount;
    RadioGroup rgMethod;
    Button btnConfirm, btnCancel;

    long totalAmount;
    String feeDetail;
    ArrayList<Integer> selectedIds;
    boolean isProcessing = false;

    // Launcher cho MoMo
    private final ActivityResultLauncher<Intent> momoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // MoMo thành công → lưu vào DB
                    String mssv = new SharedPrefs(this).getUser();
                    finishPayment(mssv, "Ví điện tử (MoMo)");
                } else {
                    isProcessing = false;
                    btnConfirm.setEnabled(true);
                }
            });

    // Launcher cho QR ngân hàng
    private final ActivityResultLauncher<Intent> bankQRLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    String mssv = new SharedPrefs(this).getUser();
                    finishPayment(mssv, "Chuyển khoản ngân hàng");
                } else {
                    isProcessing = false;
                    btnConfirm.setEnabled(true);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvFeeDetail   = findViewById(R.id.tvFeeDetail);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        rgMethod      = findViewById(R.id.rgMethod);
        btnConfirm    = findViewById(R.id.btnConfirm);
        btnCancel     = findViewById(R.id.btnCancel);

        Bundle bundle = getIntent().getBundleExtra("data");
        if (bundle != null) {
            totalAmount = bundle.getLong("TOTAL_AMOUNT", 0);
            feeDetail   = bundle.getString("FEE_DETAIL", "");
            selectedIds = bundle.getIntegerArrayList("SELECTED_IDS");
        }

        if (selectedIds == null || selectedIds.isEmpty()) {
            Toast.makeText(this, "Không có khoản phí nào được chọn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvFeeDetail.setText(feeDetail != null && !feeDetail.isEmpty() ? feeDetail : "Không có thông tin");
        tvTotalAmount.setText(String.format("Tổng cộng: %,d đ", totalAmount));

        RadioButton rbFirst = (RadioButton) rgMethod.getChildAt(0);
        if (rbFirst != null) rbFirst.setChecked(true);

        btnCancel.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> confirmPayment());
    }

    private void confirmPayment() {
        if (isProcessing) return;

        int checkedId = rgMethod.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rbSelected = findViewById(checkedId);
        String rawText = rbSelected.getText().toString().trim();
        String method = rawText.replaceAll("^[^\\p{L}]+", "").trim();
        if (method.isEmpty()) method = rawText;

        final String finalMethod = method;

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage("Số tiền: " + String.format("%,d đ", totalAmount)
                        + "\nPhương thức: " + method
                        + "\n\nBạn có chắc chắn muốn thanh toán không?")
                .setPositiveButton("Xác nhận", (dialog, which) -> doPayment(finalMethod))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void doPayment(String method) {
        if (isProcessing) return;
        isProcessing = true;
        btnConfirm.setEnabled(false);

        String mssv = new SharedPrefs(this).getUser();

        // Route theo phương thức
        if (method.contains("MoMo") || method.contains("Ví") || method.contains("điện tử")) {
            // Chuyển sang màn MoMo
            Intent intent = new Intent(this, MomoPaymentActivity.class);
            intent.putExtra("TOTAL_AMOUNT", totalAmount);
            intent.putExtra("FEE_DETAIL", feeDetail);
            momoLauncher.launch(intent);

        } else if (method.contains("Chuyển khoản") || method.contains("ngân hàng")) {
            // Chuyển sang màn QR
            Intent intent = new Intent(this, BankQRActivity.class);
            intent.putExtra("TOTAL_AMOUNT", totalAmount);
            intent.putExtra("MSSV", mssv);
            intent.putExtra("FEE_DETAIL", feeDetail);
            bankQRLauncher.launch(intent);

        } else {
            // Tiền mặt: xử lý trực tiếp
            finishPayment(mssv, method);
        }
    }

    private void finishPayment(String mssv, String method) {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<FeeItem> allFees = db.getStudentFees(mssv);
        List<FeeItem> toPayList = new ArrayList<>();
        if (selectedIds != null) {
            for (FeeItem fee : allFees) {
                if (selectedIds.contains(fee.getId()) && !"PAID".equals(fee.getStatus())) {
                    toPayList.add(fee);
                }
            }
        }

        if (toPayList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy khoản phí hợp lệ!", Toast.LENGTH_LONG).show();
            isProcessing = false;
            btnConfirm.setEnabled(true);
            setResult(RESULT_OK);
            finish();
            return;
        }

        String txnId = db.insertPayment(mssv, toPayList, method);
        if (txnId != null) {
            new AlertDialog.Builder(this)
                    .setTitle("✓ Thanh toán thành công!")
                    .setMessage("Mã giao dịch: " + txnId
                            + "\nSố tiền: " + String.format("%,d đ", totalAmount)
                            + "\nPhương thức: " + method)
                    .setPositiveButton("OK", (dialog, which) -> {
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            isProcessing = false;
            btnConfirm.setEnabled(true);
            Toast.makeText(this, "Có lỗi khi lưu giao dịch! Vui lòng thử lại.", Toast.LENGTH_LONG).show();
        }
    }
}
