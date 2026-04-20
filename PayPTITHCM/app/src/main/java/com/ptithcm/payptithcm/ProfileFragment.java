package com.ptithcm.payptithcm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.activities.LoginActivity;
import com.ptithcm.payptithcm.models.Student;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

public class ProfileFragment extends Fragment {
    TextView tvName, tvMSSV, tvClass, tvFaculty, tvEmail, tvPhone;
    Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName    = view.findViewById(R.id.tvProfileName);
        tvMSSV    = view.findViewById(R.id.tvProfileMSSV);
        tvClass   = view.findViewById(R.id.tvProfileClass);
        tvFaculty = view.findViewById(R.id.tvProfileFaculty);
        tvEmail   = view.findViewById(R.id.tvProfileEmail);
        tvPhone   = view.findViewById(R.id.tvProfilePhone);
        btnLogout = view.findViewById(R.id.btnLogout);

        loadProfile();

        btnLogout.setOnClickListener(v -> confirmLogout());

        return view;
    }

    private void loadProfile() {
        if (getContext() == null) return;
        String mssv = new SharedPrefs(getContext()).getUser();
        Student student = DatabaseHelper.getInstance(getContext()).getStudentById(mssv);

        if (student != null) {
            tvName.setText(student.getFullName() != null ? student.getFullName() : "");
            tvMSSV.setText("MSSV: " + student.getStudentId());
            tvClass.setText("Lớp: " + notEmpty(student.getClassName()));
            tvFaculty.setText("Khoa: " + notEmpty(student.getFaculty()));
            tvEmail.setText("Email: " + notEmpty(student.getEmail()));
            tvPhone.setText("SĐT: " + notEmpty(student.getPhone()));
        }
    }

    /** Hien 'N/A' neu null hoac rong */
    private String notEmpty(String val) {
        return (val != null && !val.isEmpty()) ? val : "N/A";
    }

    /** Xac nhan truoc khi dang xuat, tranh bam nham */
    private void confirmLogout() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> doLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void doLogout() {
        if (getContext() == null) return;
        new SharedPrefs(getContext()).clearUser();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
