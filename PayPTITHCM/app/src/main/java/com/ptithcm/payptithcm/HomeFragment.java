package com.ptithcm.payptithcm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.activities.MainActivity;
import com.ptithcm.payptithcm.adapters.HomeAdapter;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

public class HomeFragment extends Fragment {
    GridView gvHome;
    TextView tvWelcome, tvUnpaidSummary;

    String[] titles = {"Học phí", "Lịch sử", "Cá nhân"};
    int[] icons = {
            R.drawable.ic_fee,
            R.drawable.ic_history,
            R.drawable.ic_support
    };
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

        gvHome.setOnItemClickListener((parent, v, position, id) -> {
            // Su dung navigateTo cua MainActivity de tranh NPE
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateTo(navIds[position]);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cap nhat thong tin khi quay lai tab nay (vi du: sau khi thanh toan xong)
        loadWelcome();
    }

    private void loadWelcome() {
        if (getContext() == null) return; // Guard: fragment chua attach
        String mssv = new SharedPrefs(getContext()).getUser();
        DatabaseHelper db = DatabaseHelper.getInstance(getContext());

        com.ptithcm.payptithcm.models.Student student = db.getStudentById(mssv);
        if (student != null && student.getFullName() != null) {
            tvWelcome.setText("Xin chào, " + student.getFullName() + "!");
        } else {
            tvWelcome.setText("Xin chào, " + mssv + "!");
        }

        int unpaidCount   = db.countUnpaidFees(mssv);
        long totalUnpaid  = db.getTotalUnpaid(mssv);

        if (unpaidCount > 0) {
            tvUnpaidSummary.setText(unpaidCount + " khoản phí chưa đóng: "
                    + String.format("%,d đ", totalUnpaid));
        } else {
            tvUnpaidSummary.setText("✓ Đã đóng đầy đủ học phí!");
        }
    }
}
