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
    private final Context context;
    private final List<HistoryItem> historyList;

    public HistoryAdapter(Context context, List<HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @Override public int getCount()               { return historyList != null ? historyList.size() : 0; }
    @Override public Object getItem(int position) { return historyList.get(position); }
    @Override public long getItemId(int position) { return historyList.get(position).getPaymentId(); }
    @Override public boolean hasStableIds()       { return true; }

    static class ViewHolder {
        TextView tvFeeName, tvStatus, tvAmount, tvMethod, tvDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
            holder = new ViewHolder();
            holder.tvFeeName = convertView.findViewById(R.id.tvHistoryFeeName);
            holder.tvStatus  = convertView.findViewById(R.id.tvHistoryStatus);
            holder.tvAmount  = convertView.findViewById(R.id.tvHistoryAmount);
            holder.tvMethod  = convertView.findViewById(R.id.tvHistoryMethod);
            holder.tvDate    = convertView.findViewById(R.id.tvHistoryDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HistoryItem item = historyList.get(position);

        holder.tvFeeName.setText(item.getFeeName());
        holder.tvAmount.setText(String.format("%,d đ", item.getAmount()));
        holder.tvMethod.setText(item.getMethod() != null ? item.getMethod() : "");
        holder.tvDate.setText(item.getDate());

        // Mau trang thai giao dich
        if ("SUCCESS".equals(item.getStatus())) {
            holder.tvStatus.setText("✓ Thành công");
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvStatus.setText("✗ Thất bại");
            holder.tvStatus.setTextColor(Color.parseColor("#CE0707"));
        }

        return convertView;
    }
}
