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
import com.ptithcm.payptithcm.utils.DatabaseHelper;

import java.util.List;

public class AccountantFeeAdapter extends BaseAdapter {
    private final Context context;
    private final List<DatabaseHelper.AccountantFeeRecord> feeList;
    private final Runnable onSelectionChanged;

    public AccountantFeeAdapter(Context context,
                                List<DatabaseHelper.AccountantFeeRecord> feeList,
                                Runnable onSelectionChanged) {
        this.context = context;
        this.feeList = feeList;
        this.onSelectionChanged = onSelectionChanged;
    }

    @Override public int getCount() { return feeList != null ? feeList.size() : 0; }
    @Override public Object getItem(int position) { return feeList.get(position); }
    @Override public long getItemId(int position) { return feeList.get(position).getId(); }
    @Override public boolean hasStableIds() { return true; }

    static class ViewHolder {
        TextView tvStudent, tvFeeName, tvSemesterInfo, tvAmount, tvStatus, tvDeadline;
        CheckBox cbSelect;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_accountant_fee, parent, false);
            holder = new ViewHolder();
            holder.cbSelect = convertView.findViewById(R.id.cbSelect);
            holder.tvStudent = convertView.findViewById(R.id.tvStudent);
            holder.tvFeeName = convertView.findViewById(R.id.tvFeeName);
            holder.tvSemesterInfo = convertView.findViewById(R.id.tvSemesterInfo);
            holder.tvAmount = convertView.findViewById(R.id.tvAmount);
            holder.tvStatus = convertView.findViewById(R.id.tvStatus);
            holder.tvDeadline = convertView.findViewById(R.id.tvDeadline);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DatabaseHelper.AccountantFeeRecord item = feeList.get(position);
        boolean isPaid = "PAID".equals(item.getStatus());

        holder.tvStudent.setText(item.getStudentId() + " - " + item.getStudentName());
        holder.tvFeeName.setText(item.getFeeName());
        holder.tvSemesterInfo.setText("Hoc ky " + item.getSemester() + " - Nam hoc " + item.getSchoolYear());
        holder.tvAmount.setText(String.format("%,d d", item.getAmount()));
        holder.tvDeadline.setText("Han dong: " + formatDate(item.getDeadline()));

        if (isPaid) {
            holder.tvStatus.setText("Da dong");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_paid));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_paid);
            holder.cbSelect.setEnabled(false);
            holder.cbSelect.setChecked(false);
            holder.cbSelect.setVisibility(View.GONE);
            convertView.setAlpha(0.72f);
        } else if ("OVERDUE".equals(item.getStatus())) {
            holder.tvStatus.setText("Qua han");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_overdue));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_overdue);
            holder.cbSelect.setEnabled(true);
            holder.cbSelect.setVisibility(View.VISIBLE);
            convertView.setAlpha(1f);
        } else {
            holder.tvStatus.setText("Chua dong");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_unpaid));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_unpaid);
            holder.cbSelect.setEnabled(true);
            holder.cbSelect.setVisibility(View.VISIBLE);
            convertView.setAlpha(1f);
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
            String[] parts = raw.substring(0, 10).split("-");
            if (parts.length == 3) return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception ignored) {}
        return raw;
    }
}
