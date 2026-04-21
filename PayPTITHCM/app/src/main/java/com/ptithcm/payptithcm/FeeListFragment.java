package com.ptithcm.payptithcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.activities.PaymentActivity;
import com.ptithcm.payptithcm.adapters.FeeAdapter;
import com.ptithcm.payptithcm.models.FeeItem;
import com.ptithcm.payptithcm.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FeeListFragment extends Fragment {
    ListView lvFees;
    TextView tvTotalAmount;
    Button btnPayNow;
    TextView btnFilterUnpaid, btnFilterPaid, btnFilterOverdue;

    List<FeeItem> fullData;
    List<FeeItem> filteredData;
    FeeAdapter adapter;
    String currentStatusFilter = "UNPAID";

    private final ActivityResultLauncher<Intent> paymentLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    loadFeeData();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fee_list, container, false);

        lvFees = view.findViewById(R.id.lvFees);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        btnPayNow = view.findViewById(R.id.btnPayNow);
        
        btnFilterUnpaid = view.findViewById(R.id.btnFilterUnpaid);
        btnFilterPaid = view.findViewById(R.id.btnFilterPaid);
        btnFilterOverdue = view.findViewById(R.id.btnFilterOverdue);

        setupFilters();
        loadFeeData();

        btnPayNow.setOnClickListener(v -> handlePayment());

        return view;
    }

    private void setupFilters() {
        if (btnFilterUnpaid == null) return;

        btnFilterUnpaid.setOnClickListener(v -> {
            currentStatusFilter = "UNPAID";
            updateFilterUI(btnFilterUnpaid);
            applyFilter();
        });
        btnFilterPaid.setOnClickListener(v -> {
            currentStatusFilter = "PAID";
            updateFilterUI(btnFilterPaid);
            applyFilter();
        });
        btnFilterOverdue.setOnClickListener(v -> {
            currentStatusFilter = "OVERDUE";
            updateFilterUI(btnFilterOverdue);
            applyFilter();
        });
    }

    private void updateFilterUI(TextView selected) {
        btnFilterUnpaid.setBackgroundResource(R.drawable.bg_filter_unselected);
        btnFilterUnpaid.setTextColor(0xFF757575);
        btnFilterPaid.setBackgroundResource(R.drawable.bg_filter_unselected);
        btnFilterPaid.setTextColor(0xFF757575);
        btnFilterOverdue.setBackgroundResource(R.drawable.bg_filter_unselected);
        btnFilterOverdue.setTextColor(0xFF757575);

        selected.setBackgroundResource(R.drawable.bg_filter_selected);
        selected.setTextColor(0xFFFFFFFF);
    }

    private void loadFeeData() {
        if (getContext() == null) return;
        String mssv = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("loggedInMSSV", "21520001");
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        
        fullData = dbHelper.getStudentFees(mssv);
        applyFilter();
    }

    private void applyFilter() {
        if (fullData == null) return;
        filteredData = new ArrayList<>();
        
        for (FeeItem item : fullData) {
            if (item.getStatus().equals(currentStatusFilter)) {
                filteredData.add(item);
            }
        }

        adapter = new FeeAdapter(getContext(), filteredData, this::updateTotalWhenChecked);
        lvFees.setAdapter(adapter);
        
        btnPayNow.setVisibility(currentStatusFilter.equals("PAID") ? View.GONE : View.VISIBLE);
        updateTotalWhenChecked();
    }

    private void updateTotalWhenChecked() {
        long total = 0;
        int count = 0;
        for (FeeItem item : filteredData) {
            if (item.isSelected()) {
                total += item.getAmount();
                count++;
            }
        }
        tvTotalAmount.setText(String.format("%,d đ", total));
        btnPayNow.setEnabled(count > 0);
        btnPayNow.setText(count > 0 ? "Thanh toán đã chọn (" + String.format("%,dđ", total) + ")" : "Thanh toán");
    }

    private void handlePayment() {
        List<FeeItem> selected = new ArrayList<>();
        long sum = 0;
        for (FeeItem item : filteredData) {
            if (item.isSelected()) {
                selected.add(item);
                sum += item.getAmount();
            }
        }
        
        if (sum == 0) {
            Toast.makeText(getContext(), "Vui lòng chọn khoản phí!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("TOTAL_AMOUNT", sum);
        ArrayList<Integer> ids = new ArrayList<>();
        for (FeeItem item : selected) ids.add(item.getId());
        bundle.putIntegerArrayList("SELECTED_IDS", ids);
        intent.putExtra("data", bundle);
        paymentLauncher.launch(intent);
    }
}