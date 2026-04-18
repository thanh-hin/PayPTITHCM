package com.ptithcm.payptithcm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter; // QUAN TRỌNG: Phải có dòng này
import android.widget.TextView;
import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.models.FeeItem;
import java.util.List;

// Kế thừa BaseAdapter để ListView nhận diện được là một ListAdapter
public class FeeAdapter extends BaseAdapter {
    private Context context;
    private List<FeeItem> feeList;

    public FeeAdapter(Context context, List<FeeItem> feeList) {
        this.context = context;
        this.feeList = feeList;
    }

    @Override
    public int getCount() {
        return feeList != null ? feeList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return feeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_fee, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvFeeName);
        TextView tvAmount = convertView.findViewById(R.id.tvAmount);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);

        FeeItem item = feeList.get(position);
        tvName.setText(item.getName());
        tvAmount.setText(String.format("%,d đ", item.getAmount()));
        tvStatus.setText(item.getStatus());

        return convertView;
    }
}