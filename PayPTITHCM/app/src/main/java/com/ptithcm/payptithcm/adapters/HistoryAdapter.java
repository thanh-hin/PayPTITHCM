package com.ptithcm.payptithcm.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.models.HistoryItem;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private List<HistoryItem> historyList;

    public HistoryAdapter(Context context, List<HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @Override
    public int getCount() { return historyList != null ? historyList.size() : 0; }

    @Override
    public Object getItem(int position) { return historyList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        }

        TextView tvFeeName = convertView.findViewById(R.id.tvHistoryFeeName);
        TextView tvStatus  = convertView.findViewById(R.id.tvHistoryStatus);
        TextView tvAmount  = convertView.findViewById(R.id.tvHistoryAmount);
        TextView tvMethod  = convertView.findViewById(R.id.tvHistoryMethod);
        TextView tvDate    = convertView.findViewById(R.id.tvHistoryDate);

        HistoryItem item = historyList.get(position);

        tvFeeName.setText(item.getFeeName());
        tvAmount.setText(String.format("%,d d", item.getAmount()));
        tvMethod.setText(item.getMethod() != null ? item.getMethod() : "");
        tvDate.setText(item.getDate());

        // Mau trang thai giao dich
        if ("SUCCESS".equals(item.getStatus())) {
            tvStatus.setText("Thanh cong");
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            tvStatus.setText("That bai");
            tvStatus.setTextColor(Color.parseColor("#CE0707"));
        }

        return convertView;
    }
}
