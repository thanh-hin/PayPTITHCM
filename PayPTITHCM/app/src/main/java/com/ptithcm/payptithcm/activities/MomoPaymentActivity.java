package com.ptithcm.payptithcm.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.network.ApiClient;
import com.ptithcm.payptithcm.network.models.MomoOrderResponse;
import com.ptithcm.payptithcm.network.models.MomoPaymentRequest;
import com.ptithcm.payptithcm.network.models.MomoPaymentResponse;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MomoPaymentActivity extends AppCompatActivity {

    TextView tvAmount, tvFeeName;
    EditText etPhone;
    Button btnConfirmMomo, btnCancelMomo;

    boolean isProcessing = false;
    boolean paymentOpened = false;
    boolean paymentFinished = false;

    AlertDialog loadingDialog;
    AlertDialog waitingDialog;

    long totalAmount;
    String feeDetail;
    String currentOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momo_payment);

        tvAmount = findViewById(R.id.tvMomoAmount);
        tvFeeName = findViewById(R.id.tvMomoFeeName);
        etPhone = findViewById(R.id.etMomoPhone);
        btnConfirmMomo = findViewById(R.id.btnConfirmMomo);
        btnCancelMomo = findViewById(R.id.btnCancelMomo);

        totalAmount = getIntent().getLongExtra("TOTAL_AMOUNT", 0);
        feeDetail = getIntent().getStringExtra("FEE_DETAIL");

        tvAmount.setText(String.format("%,d đ", totalAmount));
        tvFeeName.setText(feeDetail != null ? feeDetail : "");

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

            createMomoPayment();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (paymentOpened && currentOrderId != null && !currentOrderId.isEmpty() && !paymentFinished) {
            checkMomoPaymentStatus();
        }
    }

    private void createMomoPayment() {
        isProcessing = true;
        btnConfirmMomo.setEnabled(false);
        btnCancelMomo.setEnabled(false);
        showLoading();

        String studentId = new SharedPrefs(this).getUser();

        String orderInfo = "Thanh toan hoc phi PTITHCM";
        if (studentId != null && !studentId.isEmpty()) {
            orderInfo = orderInfo + " - " + studentId;
        }

        String email = "";
        if (studentId != null && !studentId.isEmpty()) {
            email = studentId + "@student.ptithcm.edu.vn";
        }

        MomoPaymentRequest request = new MomoPaymentRequest(totalAmount, orderInfo, email);

        ApiClient.getService().createMomoPayment(request).enqueue(new Callback<MomoPaymentResponse>() {
            @Override
            public void onResponse(Call<MomoPaymentResponse> call, Response<MomoPaymentResponse> response) {
                hideLoading();

                if (!response.isSuccessful() || response.body() == null) {
                    showError("Không tạo được giao dịch MoMo. Mã lỗi: " + response.code());
                    return;
                }

                MomoPaymentResponse data = response.body();

                if (!data.success || data.payUrl == null || data.payUrl.isEmpty()) {
                    String message = data.message != null ? data.message : "MoMo trả về lỗi";
                    showError(message);
                    return;
                }

                currentOrderId = data.orderId;
                openMomoPayment(data.payUrl);
            }

            @Override
            public void onFailure(Call<MomoPaymentResponse> call, Throwable t) {
                hideLoading();
                showError("Không kết nối được backend: " + t.getMessage());
            }
        });
    }

    private void openMomoPayment(String payUrl) {
        paymentOpened = true;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl));
        startActivity(intent);

        showWaitingDialog();
    }

    private void checkMomoPaymentStatus() {
        showCheckingStatus();

        ApiClient.getService().getMomoOrderStatus(currentOrderId).enqueue(new Callback<MomoOrderResponse>() {
            @Override
            public void onResponse(Call<MomoOrderResponse> call, Response<MomoOrderResponse> response) {
                hideWaitingDialog();

                if (!response.isSuccessful() || response.body() == null) {
                    showPaymentNotCompleted("Chưa kiểm tra được trạng thái giao dịch. Mã lỗi: " + response.code());
                    return;
                }

                MomoOrderResponse data = response.body();

                if (!data.success || data.order == null) {
                    String message = data.message != null ? data.message : "Không tìm thấy đơn thanh toán";
                    showPaymentNotCompleted(message);
                    return;
                }

                if ("PAID".equalsIgnoreCase(data.order.status)) {
                    paymentFinished = true;
                    showPaymentSuccess();
                } else if ("FAILED".equalsIgnoreCase(data.order.status)) {
                    showError("Giao dịch MoMo thất bại.");
                } else {
                    showPaymentNotCompleted("Giao dịch chưa hoàn tất. Vui lòng thanh toán trên MoMo rồi quay lại app.");
                }
            }

            @Override
            public void onFailure(Call<MomoOrderResponse> call, Throwable t) {
                hideWaitingDialog();
                showPaymentNotCompleted("Không kết nối được backend để kiểm tra trạng thái: " + t.getMessage());
            }
        });
    }

    private void showPaymentSuccess() {
        new AlertDialog.Builder(this)
                .setTitle("Thanh toán MoMo thành công")
                .setMessage("Hệ thống đã xác nhận giao dịch thành công.")
                .setPositiveButton("OK", (dialog, which) -> {
                    setResult(RESULT_OK);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showPaymentNotCompleted(String message) {
        isProcessing = false;
        btnConfirmMomo.setEnabled(true);
        btnCancelMomo.setEnabled(true);

        new AlertDialog.Builder(this)
                .setTitle("Chưa xác nhận thanh toán")
                .setMessage(message)
                .setPositiveButton("Kiểm tra lại", (dialog, which) -> checkMomoPaymentStatus())
                .setNegativeButton("Huỷ", (dialog, which) -> {
                    setResult(RESULT_CANCELED);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showLoading() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_processing, null);
        loadingDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        loadingDialog.show();
    }

    private void showWaitingDialog() {
        waitingDialog = new AlertDialog.Builder(this)
                .setTitle("Đang chờ thanh toán")
                .setMessage("Bạn hãy hoàn tất thanh toán trên trang MoMo. Khi quay lại app, hệ thống sẽ tự kiểm tra trạng thái giao dịch.")
                .setPositiveButton("Kiểm tra ngay", (dialog, which) -> checkMomoPaymentStatus())
                .setNegativeButton("Huỷ", (dialog, which) -> {
                    setResult(RESULT_CANCELED);
                    finish();
                })
                .create();

        waitingDialog.show();
    }

    private void showCheckingStatus() {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.setMessage("Đang kiểm tra trạng thái giao dịch...");
        }
    }

    private void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void hideWaitingDialog() {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    private void showError(String message) {
        isProcessing = false;
        btnConfirmMomo.setEnabled(true);
        btnCancelMomo.setEnabled(true);

        new AlertDialog.Builder(this)
                .setTitle("Thanh toán thất bại")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}