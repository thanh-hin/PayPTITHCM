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
    private Context context;
    private List<FeeItem> feeList;
    private Runnable onSelectionChanged;

    public FeeAdapter(Context context, List<FeeItem> feeList, Runnable onSelectionChanged) {
        this.context = context;
        this.feeList = feeList;
        this.onSelectionChanged = onSelectionChanged;
    }

    @Override
    public int getCount() { return feeList != null ? feeList.size() : 0; }

    @Override
    public Object getItem(int position) { return feeList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_fee, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvFeeName);
        TextView tvAmount = convertView.findViewById(R.id.tvAmount);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);
        TextView tvDeadline = convertView.findViewById(R.id.tvDeadline);
        CheckBox cbSelect = convertView.findViewById(R.id.cbSelect);

        FeeItem item = feeList.get(position);

        tvName.setText(item.getName());
        tvAmount.setText(String.format("%,d d", item.getAmount()));
        tvDeadline.setText("Han: " + item.getDeadline());

        // Hien thi trang thai voi mau sac tuong ung
        String status = item.getStatus();
        if ("PAID".equals(status)) {
            tvStatus.setText("Da dong");
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            cbSelect.setEnabled(false);
            cbSelect.setChecked(false);
            convertView.setAlpha(0.6f);
        } else if ("OVERDUE".equals(status)) {
            tvStatus.setText("Qua han!");
            tvStatus.setTextColor(Color.parseColor("#F57F17"));
            cbSelect.setEnabled(true);
            convertView.setAlpha(1.0f);
        } else {
            tvStatus.setText("Chua dong");
            tvStatus.setTextColor(Color.parseColor("#CE0707"));
            cbSelect.setEnabled(true);
            convertView.setAlpha(1.0f);
        }

        // Tranh callback chong cheo khi ListView recycle view
        cbSelect.setOnCheckedChangeListener(null);
        cbSelect.setChecked(item.isSelected());
        cbSelect.setTag(position);

        cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Object tag = buttonView.getTag();
            if (tag != null && (int) tag == position) {
                item.setSelected(isChecked);
                if (onSelectionChanged != null) onSelectionChanged.run();
            }
        });

        // Click toan bo dong cung toggle checkbox
        convertView.setOnClickListener(v -> {
            if (cbSelect.isEnabled()) {
                cbSelect.setChecked(!cbSelect.isChecked());
            }
        });

        return convertView;
    }
}
