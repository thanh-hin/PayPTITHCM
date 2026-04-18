package com.ptithcm.payptithcm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.utils.DatabaseHelper;

public class ProfileFragment extends Fragment {
    TextView tvName, tvMSSV, tvClass, tvFaculty, tvEmail;
    Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ
        tvName = view.findViewById(R.id.tvProfileName);
        tvMSSV = view.findViewById(R.id.tvProfileMSSV);
        tvClass = view.findViewById(R.id.tvProfileClass);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        btnLogout = view.findViewById(R.id.btnLogout);

        loadProfileData();

        btnLogout.setOnClickListener(v -> {
            // Xử lý đăng xuất, xóa SharedPrefs và quay về LoginActivity
            getActivity().finish();
        });

        return view;
    }

    private void loadProfileData() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Join bảng Student và Class để lấy đầy đủ thông tin
        String query = "SELECT s.*, c.class_name, c.faculty FROM Student s " +
                "INNER JOIN Class c ON s.class_id = c.class_id " +
                "WHERE s.student_id = '21520001'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            tvName.setText(cursor.getString(cursor.getColumnIndex("full_name")));
            tvMSSV.setText("MSSV: " + cursor.getString(cursor.getColumnIndex("student_id")));
            tvClass.setText("Lớp: " + cursor.getString(cursor.getColumnIndex("class_name")));
            tvEmail.setText("Email: " + cursor.getString(cursor.getColumnIndex("email")));
        }
        cursor.close();
    }
}