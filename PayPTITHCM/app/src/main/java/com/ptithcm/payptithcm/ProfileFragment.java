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

        btnLogout.setOnClickListener(v -> {
            // Xoa session va quay ve man hinh dang nhap
            new SharedPrefs(getContext()).clearUser();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadProfile() {
        String mssv = new SharedPrefs(getContext()).getUser();
        Student student = DatabaseHelper.getInstance(getContext()).getStudentById(mssv);

        if (student != null) {
            tvName.setText(student.getFullName());
            tvMSSV.setText("MSSV: " + student.getStudentId());
            tvClass.setText("Lop: " + (student.getClassName() != null ? student.getClassName() : "N/A"));
            tvFaculty.setText("Khoa: " + (student.getFaculty() != null ? student.getFaculty() : "N/A"));
            tvEmail.setText("Email: " + student.getEmail());
            tvPhone.setText("SDT: " + (student.getPhone() != null ? student.getPhone() : "Chua cap nhat"));
        }
    }
}
