package com.ptithcm.payptithcm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ptithcm.payptithcm.R;

public class HomeAdapter extends BaseAdapter {
    private final Context context;
    private final String[] titles;
    private final int[] icons;

    public HomeAdapter(Context context, String[] titles, int[] icons) {
        this.context = context;
        this.titles = titles;
        this.icons = icons;
    }

    @Override public int getCount()          { return titles != null ? titles.length : 0; }
    @Override public Object getItem(int i)   { return titles[i]; }
    @Override public long getItemId(int i)   { return i; }

    static class ViewHolder {
        ImageView img;
        TextView txt;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_home, viewGroup, false);
            holder = new ViewHolder();
            holder.img = view.findViewById(R.id.imgIcon);
            holder.txt = view.findViewById(R.id.tvTitle);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.img.setImageResource(icons[i]);
        holder.txt.setText(titles[i]);
        return view;
    }
}
