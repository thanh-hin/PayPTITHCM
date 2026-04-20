package com.ptithcm.payptithcm.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvFeeDetail   = findViewById(R.id.tvFeeDetail);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        rgMethod      = findViewById(R.id.rgMethod);
        btnConfirm    = findViewById(R.id.btnConfirm);
        btnCancel     = findViewById(R.id.btnCancel);

        // Nhan du lieu tu FeeListFragment qua Bundle
        Bundle bundle = getIntent().getBundleExtra("data");
        if (bundle != null) {
            totalAmount  = bundle.getLong("TOTAL_AMOUNT", 0);
            feeDetail    = bundle.getString("FEE_DETAIL", "");
            selectedIds  = bundle.getIntegerArrayList("SELECTED_IDS");
        }

        tvFeeDetail.setText(feeDetail != null ? feeDetail : "Khong co thong tin");
        tvTotalAmount.setText(String.format("Tong: %,d d", totalAmount));

        // Mac dinh chon phuong thuc dau tien
        RadioButton rbFirst = (RadioButton) rgMethod.getChildAt(0);
        if (rbFirst != null) rbFirst.setChecked(true);

        btnCancel.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> confirmPayment());
    }

    private void confirmPayment() {
        // Kiem tra da chon phuong thuc thanh toan
        int checkedId = rgMethod.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Vui long chon phuong thuc thanh toan!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rbSelected = findViewById(checkedId);
        String method = rbSelected.getText().toString();

        // Hoi xac nhan truoc khi thanh toan
        new AlertDialog.Builder(this)
                .setTitle("Xac nhan thanh toan")
                .setMessage("Ban co chac muon thanh toan " + String.format("%,d d", totalAmount)
                        + "\nQua: " + method + "?")
                .setPositiveButton("Thanh toan", (dialog, which) -> doPayment(method))
                .setNegativeButton("Huy", null)
                .show();
    }

    private void doPayment(String method) {
        String mssv = new SharedPrefs(this).getUser();

        // Lay danh sach FeeItem da chon tu DB de truyen vao insertPayment
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<FeeItem> allFees = db.getStudentFees(mssv);
        List<FeeItem> toPayList = new ArrayList<>();

        if (selectedIds != null) {
            for (FeeItem fee : allFees) {
                if (selectedIds.contains(fee.getId())) {
                    toPayList.add(fee);
                }
            }
        }

        if (toPayList.isEmpty()) {
            Toast.makeText(this, "Khong tim thay khoan phi!", Toast.LENGTH_SHORT).show();
            return;
        }

        String txnId = db.insertPayment(mssv, toPayList, method);

        if (txnId != null) {
            // Thanh toan thanh cong - hien ket qua
            new AlertDialog.Builder(this)
                    .setTitle("Thanh toan thanh cong!")
                    .setMessage("Ma giao dich: " + txnId +
                            "\nSo tien: " + String.format("%,d d", totalAmount) +
                            "\nPhuong thuc: " + method)
                    .setPositiveButton("OK", (dialog, which) -> {
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            Toast.makeText(this, "Co loi khi thanh toan! Vui long thu lai.", Toast.LENGTH_LONG).show();
        }
    }
}
