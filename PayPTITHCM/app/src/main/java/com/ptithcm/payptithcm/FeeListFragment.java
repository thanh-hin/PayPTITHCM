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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.activities.PaymentActivity;
import com.ptithcm.payptithcm.adapters.FeeAdapter;
import com.ptithcm.payptithcm.models.FeeItem;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class FeeListFragment extends Fragment {
    ListView lvFees;
    Button btnPayNow;
    TextView tvTotalAmount;
    List<FeeItem> data;
    FeeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fee_list, container, false);

        lvFees = view.findViewById(R.id.lvFees);
        btnPayNow = view.findViewById(R.id.btnPayNow);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);

        loadFees();
        setupPayButton();
        return view;
    }

    private void loadFees() {
        String mssv = new SharedPrefs(getContext()).getUser();
        DatabaseHelper db = DatabaseHelper.getInstance(getContext());
        data = db.getStudentFees(mssv);

        adapter = new FeeAdapter(getContext(), data, this::onSelectionChanged);
        lvFees.setAdapter(adapter);

        // Hien thi tong ban dau
        updateTotal();
    }

    // Callback khi checkbox thay doi
    private void onSelectionChanged() {
        updateTotal();
    }

    private void updateTotal() {
        long total = 0;
        List<FeeItem> selected = getSelectedFees();
        for (FeeItem item : selected) {
            total += item.getAmount();
        }
        tvTotalAmount.setText(String.format("%,d d", total));
        // Cho phep bam nut neu co it nhat 1 khoan duoc chon
        btnPayNow.setEnabled(!selected.isEmpty());
        btnPayNow.setText(selected.isEmpty()
                ? "Chon khoan phi de thanh toan"
                : "Thanh toan " + selected.size() + " khoan");
    }

    private List<FeeItem> getSelectedFees() {
        List<FeeItem> selected = new ArrayList<>();
        if (data == null) return selected;
        for (FeeItem item : data) {
            if (item.isSelected()) selected.add(item);
        }
        return selected;
    }

    private void setupPayButton() {
        btnPayNow.setOnClickListener(v -> {
            List<FeeItem> selected = getSelectedFees();
            if (selected.isEmpty()) {
                Toast.makeText(getContext(), "Vui long chon it nhat 1 khoan phi!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Tinh tong
            long total = 0;
            StringBuilder feeNames = new StringBuilder();
            for (FeeItem item : selected) {
                total += item.getAmount();
                feeNames.append("- ").append(item.getName())
                        .append(": ").append(String.format("%,d d", item.getAmount()))
                        .append("\n");
            }
            // Truyen du lieu sang PaymentActivity qua Intent + Bundle
            Intent intent = new Intent(getActivity(), PaymentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong("TOTAL_AMOUNT", total);
            bundle.putString("FEE_DETAIL", feeNames.toString());
            // Truyen danh sach id cac khoan phi duoc chon
            ArrayList<Integer> ids = new ArrayList<>();
            for (FeeItem item : selected) ids.add(item.getId());
            bundle.putIntegerArrayList("SELECTED_IDS", ids);
            intent.putExtra("data", bundle);
            startActivityForResult(intent, 100);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Reload sau khi thanh toan xong
        if (requestCode == 100 && resultCode == getActivity().RESULT_OK) {
            loadFees();
        }
    }
}
