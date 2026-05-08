package com.ptithcm.payptithcm.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.models.FeeItem;

import java.util.List;

public class FeeAdapter extends BaseAdapter {
    private final Context context;
    private final List<FeeItem> feeList;
    private final Runnable onSelectionChanged;

    public FeeAdapter(Context context, List<FeeItem> feeList, Runnable onSelectionChanged) {
        this.context = context;
        this.feeList = feeList;
        this.onSelectionChanged = onSelectionChanged;
    }

    @Override public int getCount()                    { return feeList != null ? feeList.size() : 0; }
    @Override public Object getItem(int position)      { return feeList.get(position); }
    @Override public long getItemId(int position)      { return feeList.get(position).getId(); }
    @Override public boolean hasStableIds()            { return true; }

    // ViewHolder de tranh goi findViewById moi lan scroll
    static class ViewHolder {
        TextView tvName, tvAmount, tvStatus, tvDeadline;
        CheckBox cbSelect;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_fee, parent, false);
            holder = new ViewHolder();
            holder.tvName     = convertView.findViewById(R.id.tvFeeName);
            holder.tvAmount   = convertView.findViewById(R.id.tvAmount);
            holder.tvStatus   = convertView.findViewById(R.id.tvStatus);
            holder.tvDeadline = convertView.findViewById(R.id.tvDeadline);
            holder.cbSelect   = convertView.findViewById(R.id.cbSelect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FeeItem item = feeList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvAmount.setText(String.format("%,d đ", item.getAmount()));
        holder.tvDeadline.setText("Hạn: " + formatDate(item.getDeadline()));

        // Mau sac theo trang thai
        String status = item.getStatus();
        if ("PAID".equals(status)) {
            holder.tvStatus.setText("✓ Đã đóng");
            holder.tvStatus.setTextColor(Color.parseColor("#16A34A"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_paid);
            holder.cbSelect.setEnabled(false);
            holder.cbSelect.setChecked(false);
            convertView.setAlpha(0.65f);
        } else if ("OVERDUE".equals(status)) {
            holder.tvStatus.setText("⚠ Quá hạn!");
            holder.tvStatus.setTextColor(Color.parseColor("#D97706"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_overdue);
            holder.cbSelect.setEnabled(true);
            convertView.setAlpha(1.0f);
        } else {
            holder.tvStatus.setText("Chưa đóng");
            holder.tvStatus.setTextColor(Color.parseColor("#CE0707"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_unpaid);
            holder.cbSelect.setEnabled(true);
            convertView.setAlpha(1.0f);
        }

        // Reset listener truoc khi setChecked tranh callback sai khi recycle
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(item.isSelected());

        final int pos = position;
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (onSelectionChanged != null) onSelectionChanged.run();
        });

        // Click toan bo dong cung toggle checkbox
        final View finalView = convertView;
        finalView.setOnClickListener(v -> {
            if (holder.cbSelect.isEnabled()) {
                holder.cbSelect.setChecked(!holder.cbSelect.isChecked());
            }
        });

        return convertView;
    }

    private String formatDate(String raw) {
        if (raw == null || raw.length() < 10) return raw != null ? raw : "";
        try {
            String[] parts = raw.substring(0, 10).split("-");
            if (parts.length == 3) return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception ignored) {}
        return raw;
    }
}
