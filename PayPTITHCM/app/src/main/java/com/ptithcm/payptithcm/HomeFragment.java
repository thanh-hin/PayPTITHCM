package com.ptithcm.payptithcm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ptithcm.payptithcm.adapters.HomeAdapter;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

public class HomeFragment extends Fragment {
    GridView gvHome;
    TextView tvWelcome, tvUnpaidSummary;

    String[] titles = {"Hoc phi", "Lich su", "Ca nhan"};
    int[] icons = {
            R.drawable.ic_fee,
            R.drawable.ic_history,
            R.drawable.ic_support
    };
    // Map ten menu -> Fragment index trong bottom nav
    int[] navIds = {R.id.nav_fees, R.id.nav_history, R.id.nav_profile};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcome       = view.findViewById(R.id.tvWelcome);
        tvUnpaidSummary = view.findViewById(R.id.tvUnpaidSummary);
        gvHome          = view.findViewById(R.id.gvHome);

        loadWelcome();

        HomeAdapter adapter = new HomeAdapter(getContext(), titles, icons);
        gvHome.setAdapter(adapter);

        // Click vao o GridView -> chuyen tab tuong ung
        gvHome.setOnItemClickListener((parent, v, position, id) -> {
            BottomNavigationView nav = getActivity().findViewById(R.id.bottom_navigation);
            if (nav != null) {
                nav.setSelectedItemId(navIds[position]);
            }
        });

        return view;
    }

    private void loadWelcome() {
        String mssv = new SharedPrefs(getContext()).getUser();
        DatabaseHelper db = DatabaseHelper.getInstance(getContext());

        // Lay ten sinh vien
        com.ptithcm.payptithcm.models.Student student = db.getStudentById(mssv);
        if (student != null) {
            tvWelcome.setText("Xin chao, " + student.getFullName() + "!");
        } else {
            tvWelcome.setText("Xin chao, " + mssv + "!");
        }

        // Tong hop so khoan chua dong + tong tien
        int unpaidCount = db.countUnpaidFees(mssv);
        long totalUnpaid = db.getTotalUnpaid(mssv);

        if (unpaidCount > 0) {
            tvUnpaidSummary.setText(unpaidCount + " khoan phi chua dong: "
                    + String.format("%,d d", totalUnpaid));
        } else {
            tvUnpaidSummary.setText("Da dong du hoc phi!");
        }
    }
}
