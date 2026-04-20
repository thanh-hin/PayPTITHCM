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

    // Thay startActivityForResult (deprecated) bang ActivityResultLauncher
    private final ActivityResultLauncher<Intent> paymentLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    // Reload sau khi thanh toan thanh cong
                    loadFees();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fee_list, container, false);

        lvFees        = view.findViewById(R.id.lvFees);
        btnPayNow     = view.findViewById(R.id.btnPayNow);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);

        loadFees();
        setupPayButton();
        return view;
    }

    private void loadFees() {
        if (getContext() == null) return; // Guard: fragment chua attach
        String mssv = new SharedPrefs(getContext()).getUser();
        DatabaseHelper db = DatabaseHelper.getInstance(getContext());
        data = db.getStudentFees(mssv);

        adapter = new FeeAdapter(getContext(), data, this::updateTotal);
        lvFees.setAdapter(adapter);
        updateTotal();
    }

    private void updateTotal() {
        long total = 0;
        List<FeeItem> selected = getSelectedFees();
        for (FeeItem item : selected) {
            total += item.getAmount();
        }
        tvTotalAmount.setText(String.format("%,d đ", total));
        btnPayNow.setEnabled(!selected.isEmpty());
        btnPayNow.setText(selected.isEmpty()
                ? "Chọn khoản phí để thanh toán"
                : "Thanh toán " + selected.size() + " khoản (" + String.format("%,d đ", total) + ")");
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
