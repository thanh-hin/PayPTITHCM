package com.ptithcm.payptithcm;

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

import com.google.android.material.chip.ChipGroup;
import com.ptithcm.payptithcm.activities.PaymentActivity;
import com.ptithcm.payptithcm.adapters.FeeAdapter;
import com.ptithcm.payptithcm.models.FeeItem;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FeeListFragment extends Fragment {

    ListView lvFees;
    Button btnPayNow;
    TextView tvTotalAmount;
    View tvEmptyFees;
    ChipGroup chipGroupFilter, chipGroupSort;

    List<FeeItem> allFees = new ArrayList<>();
    List<FeeItem> displayFees = new ArrayList<>();
    FeeAdapter adapter;

    private String activeFilter = "ALL";  // ALL | UNPAID | OVERDUE | PAID
    private String activeSort   = "DEFAULT"; // DEFAULT | AMOUNT_ASC | AMOUNT_DESC | DEADLINE

    private final ActivityResultLauncher<Intent> paymentLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    loadFees();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fee_list, container, false);

        lvFees          = view.findViewById(R.id.lvFees);
        btnPayNow       = view.findViewById(R.id.btnPayNow);
        tvTotalAmount   = view.findViewById(R.id.tvTotalAmount);
        tvEmptyFees     = view.findViewById(R.id.tvEmptyFees);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        chipGroupSort   = view.findViewById(R.id.chipGroupSort);

        loadFees();
        setupChips(view);
        setupPayButton();
        return view;
    }

    private void setupChips(View view) {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if      (id == R.id.chipUnpaid)  activeFilter = "UNPAID";
            else if (id == R.id.chipOverdue) activeFilter = "OVERDUE";
            else if (id == R.id.chipPaid)    activeFilter = "PAID";
            else                             activeFilter = "ALL";
            applyFilterAndSort();
        });

        chipGroupSort.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if      (id == R.id.chipSortAmountAsc)  activeSort = "AMOUNT_ASC";
            else if (id == R.id.chipSortAmountDesc) activeSort = "AMOUNT_DESC";
            else if (id == R.id.chipSortDeadline)   activeSort = "DEADLINE";
            else                                    activeSort = "DEFAULT";
            applyFilterAndSort();
        });
    }

    private void loadFees() {
        if (getContext() == null) return;
        String mssv = new SharedPrefs(getContext()).getUser();
        allFees = DatabaseHelper.getInstance(getContext()).getStudentFees(mssv);
        applyFilterAndSort();
    }

    private void applyFilterAndSort() {
        // 1. Filter
        List<FeeItem> filtered;
        if ("ALL".equals(activeFilter)) {
            filtered = new ArrayList<>(allFees);
        } else {
            filtered = allFees.stream()
                    .filter(f -> activeFilter.equals(f.getStatus()))
                    .collect(Collectors.toList());
        }

        // 2. Sort
        switch (activeSort) {
            case "AMOUNT_ASC":
                filtered.sort(Comparator.comparingLong(FeeItem::getAmount));
                break;
            case "AMOUNT_DESC":
                filtered.sort((a, b) -> Long.compare(b.getAmount(), a.getAmount()));
                break;
            case "DEADLINE":
                filtered.sort(Comparator.comparing(f -> f.getDeadline() != null ? f.getDeadline() : ""));
                break;
            default:
                // Giữ thứ tự mặc định: OVERDUE → UNPAID → PAID
                filtered.sort((a, b) -> statusOrder(a.getStatus()) - statusOrder(b.getStatus()));
                break;
        }

        displayFees = filtered;

        // Reset selection state (để tránh chọn sai sau filter)
        for (FeeItem item : displayFees) {
            if ("PAID".equals(item.getStatus())) item.setSelected(false);
        }

        adapter = new FeeAdapter(getContext(), displayFees, this::updateTotal);
        lvFees.setAdapter(adapter);

        boolean empty = displayFees == null || displayFees.isEmpty();
        tvEmptyFees.setVisibility(empty ? View.VISIBLE : View.GONE);
        lvFees.setVisibility(empty ? View.GONE : View.VISIBLE);

        updateTotal();
    }

    private int statusOrder(String status) {
        if ("OVERDUE".equals(status)) return 1;
        if ("UNPAID".equals(status))  return 2;
        return 3;
    }

    private void updateTotal() {
        long total = 0;
        List<FeeItem> selected = getSelectedFees();
        for (FeeItem item : selected) total += item.getAmount();
        tvTotalAmount.setText(String.format("%,d đ", total));
        btnPayNow.setEnabled(!selected.isEmpty());
        btnPayNow.setText(selected.isEmpty()
                ? "Chọn khoản phí để thanh toán"
                : "Thanh toán " + selected.size() + " khoản (" + String.format("%,d đ", total) + ")");
    }

    private List<FeeItem> getSelectedFees() {
        List<FeeItem> selected = new ArrayList<>();
        if (displayFees == null) return selected;
        for (FeeItem item : displayFees) {
            if (item.isSelected()) selected.add(item);
        }
        return selected;
    }

    private void setupPayButton() {
        btnPayNow.setOnClickListener(v -> {
            List<FeeItem> selected = getSelectedFees();
            if (selected.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất 1 khoản phí!", Toast.LENGTH_SHORT).show();
                return;
            }
            long total = 0;
            StringBuilder feeNames = new StringBuilder();
            for (FeeItem item : selected) {
                total += item.getAmount();
                feeNames.append("• ").append(item.getName())
                        .append(": ").append(String.format("%,d đ", item.getAmount()))
                        .append("\n");
            }
            Intent intent = new Intent(getActivity(), PaymentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong("TOTAL_AMOUNT", total);
            bundle.putString("FEE_DETAIL", feeNames.toString().trim());
            ArrayList<Integer> ids = new ArrayList<>();
            for (FeeItem item : selected) ids.add(item.getId());
            bundle.putIntegerArrayList("SELECTED_IDS", ids);
            intent.putExtra("data", bundle);
            paymentLauncher.launch(intent);
        });
    }
}
