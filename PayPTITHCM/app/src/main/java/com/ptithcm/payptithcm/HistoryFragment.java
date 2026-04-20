package com.ptithcm.payptithcm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.adapters.HistoryAdapter;
import com.ptithcm.payptithcm.models.HistoryItem;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.List;

public class HistoryFragment extends Fragment {
    ListView lvHistory;
    TextView tvEmptyHistory;

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
        // Reload khi quay lai tab (sau khi co thanh toan moi)
        loadHistory();
    }

    private void loadHistory() {
        if (getContext() == null) return;
        String mssv = new SharedPrefs(getContext()).getUser();
        List<HistoryItem> historyList = DatabaseHelper.getInstance(getContext())
                .getPaymentHistory(mssv);

        if (historyList.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            lvHistory.setVisibility(View.GONE);
        } else {
            tvEmptyHistory.setVisibility(View.GONE);
            lvHistory.setVisibility(View.VISIBLE);
            HistoryAdapter adapter = new HistoryAdapter(getContext(), historyList);
            lvHistory.setAdapter(adapter);
        }
    }
}
