package com.ptithcm.payptithcm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.ptithcm.payptithcm.models.Student;
import com.ptithcm.payptithcm.utils.DatabaseHelper;

public class HomeFragment extends Fragment {
    GridView gvHome;
    TextView tvHomeName, tvTotalUnpaid, tvDebtCount, tvTotalPaid;

    String[] titles = {
            "Khoản phí",
          //  "Biên lai",
            "Thông báo",
            "Lịch sử",
            "Hỗ trợ",
          //  "Liên hệ"
    };

    int[] icons = {
            R.drawable.ic_fee,
       //     R.drawable.ic_bill,
            R.drawable.ic_bell,
            R.drawable.ic_history,
            R.drawable.ic_support,
         //   R.drawable.ic_contact
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ các View mới
        tvHomeName = view.findViewById(R.id.tvHomeName);
        tvTotalUnpaid = view.findViewById(R.id.tvTotalUnpaid);
        tvDebtCount = view.findViewById(R.id.tvDebtCount);
        tvTotalPaid = view.findViewById(R.id.tvTotalPaid);
        gvHome = view.findViewById(R.id.gvHome);

        // Load dữ liệu hiển thị lên Card
        loadHomeData();

        // Setup GridView
        HomeAdapter adapter = new HomeAdapter(getContext(), titles, icons);
        gvHome.setAdapter(adapter);

        gvHome.setOnItemClickListener((parent, v, position, id) -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity main = (MainActivity) getActivity();
                if (position == 0) main.navigateTo(R.id.nav_fees);
                else if (position == 2) main.navigateTo(R.id.nav_history);
            }
        });

        return view;
    }

    private void loadHomeData() {
        if (getContext() == null) return;

        String mssv = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("loggedInMSSV", "21520001");

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());

        // 1. Lấy tên sinh viên
        Student student = dbHelper.getStudentById(mssv);
        if (student != null) {
            tvHomeName.setText(student.getFullName());
        }

        // 2. Lấy tổng tiền chưa đóng và số khoản nợ
        long totalUnpaid = dbHelper.getTotalUnpaid(mssv);
        int debtCount = dbHelper.countUnpaidFees(mssv);

        tvTotalUnpaid.setText(String.format("%,d đ", totalUnpaid));
        tvDebtCount.setText(String.format("%02d", debtCount));

        // 3. Lấy tổng tiền đã đóng (Tính từ bảng Payment)
        long totalPaid = calculateTotalPaid(mssv);
        tvTotalPaid.setText(String.format("%,d đ", totalPaid));
    }

    private long calculateTotalPaid(String mssv) {
        long total = 0;
        try {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM Payment WHERE student_id = ?", new String[]{mssv});
            if (cursor.moveToFirst()) {
                total = cursor.getLong(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
}