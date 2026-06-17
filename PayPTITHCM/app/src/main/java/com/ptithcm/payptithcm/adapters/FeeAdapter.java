package com.ptithcm.payptithcm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

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

    static class ViewHolder {
        TextView tvName, tvSemesterInfo, tvAmount, tvStatus, tvDeadline;
        CheckBox cbSelect;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_fee, parent, false);
            holder = new ViewHolder();
            holder.tvName         = convertView.findViewById(R.id.tvFeeName);
            holder.tvSemesterInfo = convertView.findViewById(R.id.tvSemesterInfo);
            holder.tvAmount       = convertView.findViewById(R.id.tvAmount);
            holder.tvStatus       = convertView.findViewById(R.id.tvStatus);
            holder.tvDeadline     = convertView.findViewById(R.id.tvDeadline);
            holder.cbSelect       = convertView.findViewById(R.id.cbSelect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FeeItem item = feeList.get(position);

        holder.tvName.setText(item.getName());
        
        // Hiển thị Học kì và Năm học
        if (item.getSchoolYear() != null && !item.getSchoolYear().isEmpty()) {
            holder.tvSemesterInfo.setText(String.format("Học kỳ %s - Năm học %s", item.getSemester(), item.getSchoolYear()));
            holder.tvSemesterInfo.setVisibility(View.VISIBLE);
        } else {
            holder.tvSemesterInfo.setVisibility(View.GONE);
        }

        holder.tvAmount.setText(String.format("%,d đ", item.getAmount()));
        holder.tvDeadline.setText("Hạn đóng: " + formatDate(item.getDeadline()));

        String status = item.getStatus();
        if ("PAID".equals(status)) {
            holder.tvStatus.setText("✓ Đã thanh toán");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_paid));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_paid);
            holder.cbSelect.setEnabled(false);
            holder.cbSelect.setChecked(false);
            holder.cbSelect.setVisibility(View.GONE);
            convertView.setAlpha(0.7f);
        } else if ("OVERDUE".equals(status)) {
            holder.tvStatus.setText("⚠ Quá hạn");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_overdue));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_overdue);
            holder.cbSelect.setEnabled(true);
            holder.cbSelect.setVisibility(View.VISIBLE);
            convertView.setAlpha(1.0f);
        } else {
            holder.tvStatus.setText("Chưa thanh toán");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_unpaid));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_unpaid);
            holder.cbSelect.setEnabled(true);
            holder.cbSelect.setVisibility(View.VISIBLE);
            convertView.setAlpha(1.0f);
        }

        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(item.isSelected());

        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (onSelectionChanged != null) onSelectionChanged.run();
        });

        convertView.setOnClickListener(v -> {
            if (holder.cbSelect.isEnabled() && holder.cbSelect.getVisibility() == View.VISIBLE) {
                holder.cbSelect.setChecked(!holder.cbSelect.isChecked());
            }
        });

        return convertView;
    }

    private String formatDate(String raw) {
        if (raw == null || raw.length() < 10) return raw != null ? raw : "N/A";
        try {
            // raw format: YYYY-MM-DD...
            String[] parts = raw.substring(0, 10).split("-");
            if (parts.length == 3) return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception ignored) {}
        return raw;
    }
}
