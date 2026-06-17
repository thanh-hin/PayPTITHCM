package com.ptithcm.payptithcm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.adapters.HistoryAdapter;
import com.ptithcm.payptithcm.models.HistoryItem;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.List;

public class HistoryFragment extends Fragment {
    ListView lvHistory;
    View tvEmptyHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        lvHistory      = view.findViewById(R.id.lvHistory);
        tvEmptyHistory = view.findViewById(R.id.tvEmptyHistory);
        loadHistory();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        if (getContext() == null) return;
        
        SharedPrefs prefs = new SharedPrefs(getContext());
        String mssv = prefs.getUser();

        List<HistoryItem> historyList = DatabaseHelper.getInstance(getContext()).getPaymentHistory(mssv);

        if (historyList.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            lvHistory.setVisibility(View.GONE);
        } else {
            tvEmptyHistory.setVisibility(View.GONE);
            lvHistory.setVisibility(View.VISIBLE);
            HistoryAdapter adapter = new HistoryAdapter(getContext(), historyList);
            lvHistory.setAdapter(adapter);
            lvHistory.setOnItemClickListener((parent, v, position, id) ->
                showDetailDialog(historyList.get(position)));
        }
    }

    private void showDetailDialog(HistoryItem item) {
        if (getContext() == null) return;
        String status = "SUCCESS".equals(item.getStatus()) ? "✓ Thành công" : "✗ Thất bại";
        String dateFormatted = formatDateTime(item.getDate());
        String detail = "📅 Ngày: " + dateFormatted
                + "\n\n📋 Khoản phí: " + item.getFeeName()
                + "\n\n💰 Số tiền: " + String.format("%,d đ", item.getAmount())
                + "\n\n🏦 Phương thức: " + (item.getMethod() != null ? item.getMethod() : "")
                + "\n\n🔖 Mã giao dịch: " + (item.getTransactionId() != null ? item.getTransactionId() : "")
                + "\n\n✅ Trạng thái: " + status;
        new AlertDialog.Builder(getContext())
                .setTitle("Chi tiết giao dịch")
                .setMessage(detail)
                .setPositiveButton("Đóng", null)
                .show();
    }

    private String formatDateTime(String raw) {
        if (raw == null || raw.length() < 10) return raw != null ? raw : "";
        try {
            String[] parts = raw.substring(0, 10).split("-");
            if (parts.length == 3) {
                String formatted = parts[2] + "/" + parts[1] + "/" + parts[0];
                if (raw.length() >= 16) formatted += " " + raw.substring(11, 16);
                return formatted;
            }
        } catch (Exception ignored) {}
        return raw;
    }
}
