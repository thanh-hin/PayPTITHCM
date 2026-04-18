package com.ptithcm.payptithcm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.models.HistoryItem;
import com.ptithcm.payptithcm.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    ListView lvHistory;
    List<HistoryItem> historyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        lvHistory = view.findViewById(R.id.lvHistory);

        loadHistoryFromDB();
        return view;
    }

    private void loadHistoryFromDB() {
        historyList = new ArrayList<>();
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query bảng Payment dựa trên student_id
        Cursor cursor = db.rawQuery("SELECT * FROM Payment ORDER BY payment_date DESC", null);

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("payment_date"));
            double amount = cursor.getDouble(cursor.getColumnIndex("total_amount"));
            String status = cursor.getString(cursor.getColumnIndex("status"));
            String transId = cursor.getString(cursor.getColumnIndex("transaction_id"));

            historyList.add(new HistoryItem(date, "Thanh toán học phí", (long)amount, status));
        }
        cursor.close();

        // Bạn dùng HistoryAdapter để hiển thị nhé
        HistoryAdapter adapter = new HistoryAdapter(getContext(), historyList);
        lvHistory.setAdapter(adapter);
    }
}