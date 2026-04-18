package com.ptithcm.payptithcm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.adapters.HomeAdapter;

import java.util.ArrayList;
import java.util.List;
public class HomeFragment extends Fragment {
    GridView gvHome;
    String[] titles = {"Học phí", "Thanh toán", "Lịch sử", "Hóa đơn", "Thông báo", "Hỗ trợ"};
    int[] icons = {R.drawable.ic_fee, R.drawable.ic_pay, R.drawable.ic_history,
            R.drawable.ic_bill, R.drawable.ic_bell, R.drawable.ic_support};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        gvHome = view.findViewById(R.id.gvHome);
        HomeAdapter adapter = new HomeAdapter(getContext(), titles, icons);
        gvHome.setAdapter(adapter);
        return view;
    }
}