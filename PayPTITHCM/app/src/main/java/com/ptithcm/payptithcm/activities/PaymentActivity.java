package com.ptithcm.payptithcm.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.models.FeeItem;
import com.ptithcm.payptithcm.network.VnpayApiClient;
import com.ptithcm.payptithcm.network.models.VnpayCreateRequest;
import com.ptithcm.payptithcm.network.models.VnpayCreateResponse;
import com.ptithcm.payptithcm.network.models.VnpayStatusResponse;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    TextView tvFeeDetail, tvTotalAmount;
    RadioGroup rgMethod;
    MaterialButton btnConfirm, btnCancel;

    long totalAmount;
    String feeDetail;
    ArrayList<Integer> selectedIds;
    boolean isProcessing = false;

    private String currentVnpayTxnRef = null;
    private boolean waitingVnpayResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();
        loadData();
    }

    private void initViews() {
        tvFeeDetail   = findViewById(R.id.tvFeeDetail);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        rgMethod      = findViewById(R.id.rgMethod);
        btnConfirm    = findViewById(R.id.btnConfirm);
        btnCancel     = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> confirmPayment());
    }

    private void loadData() {
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

        tvFeeDetail.setText(feeDetail != null && !feeDetail.isEmpty() ? feeDetail : "Không có nội dung thanh toán");
        tvTotalAmount.setText(String.format("%,d đ", totalAmount));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (waitingVnpayResult && currentVnpayTxnRef != null) {
            checkVnpayStatus();
        }
    }

    private void confirmPayment() {
        if (isProcessing) return;

        int checkedId = rgMethod.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkedId != R.id.rbVnpay) {
            Toast.makeText(this, "Sinh viên chỉ được thanh toán trực tuyến qua VNPAY!", Toast.LENGTH_SHORT).show();
            return;
        }

        String methodName = "VNPAY";

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Thanh toán " + String.format("%,d đ", totalAmount) + " bằng " + methodName + "?")
                .setPositiveButton("Tiếp tục", (dialog, which) -> doPayment(checkedId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void doPayment(int checkedId) {
        isProcessing = true;
        btnConfirm.setEnabled(false);
        btnConfirm.setText("ĐANG XỬ LÝ...");

        startVnpayPayment();
    }

    private void startVnpayPayment() {
        String mssv = new SharedPrefs(this).getUser();
        VnpayCreateRequest request = new VnpayCreateRequest(mssv, selectedIds, totalAmount);

        VnpayApiClient.getService()
                .createVnpayPayment(request)
                .enqueue(new Callback<VnpayCreateResponse>() {
                    @Override
                    public void onResponse(Call<VnpayCreateResponse> call, Response<VnpayCreateResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            currentVnpayTxnRef = response.body().transactionId;
                            waitingVnpayResult = true;
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(response.body().paymentUrl)));
                        } else {
                            resetProcessingState();
                            Toast.makeText(PaymentActivity.this, "Lỗi khởi tạo thanh toán", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VnpayCreateResponse> call, Throwable t) {
                        resetProcessingState();
                        Toast.makeText(PaymentActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkVnpayStatus() {
        VnpayApiClient.getService()
                .getVnpayStatus(currentVnpayTxnRef)
                .enqueue(new Callback<VnpayStatusResponse>() {
                    @Override
                    public void onResponse(Call<VnpayStatusResponse> call, Response<VnpayStatusResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            if ("SUCCESS".equals(response.body().status)) {
                                waitingVnpayResult = false;
                                finishPayment(new SharedPrefs(PaymentActivity.this).getUser(), "VNPAY");
                            } else if ("FAILED".equals(response.body().status)) {
                                waitingVnpayResult = false;
                                resetProcessingState();
                                Toast.makeText(PaymentActivity.this, "Thanh toán thất bại", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<VnpayStatusResponse> call, Throwable t) {}
                });
    }

    private void resetProcessingState() {
        isProcessing = false;
        btnConfirm.setEnabled(true);
        btnConfirm.setText("XÁC NHẬN");
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
            finish();
            return;
        }

        String txnId = db.insertPayment(mssv, toPayList, method);
        if (txnId != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Thành công")
                    .setMessage("Giao dịch hoàn tất!\nMã GD: " + txnId)
                    .setPositiveButton("Xong", (dialog, which) -> {
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            resetProcessingState();
            Toast.makeText(this, "Lỗi lưu dữ liệu!", Toast.LENGTH_LONG).show();
        }
    }
}
