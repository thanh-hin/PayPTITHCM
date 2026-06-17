package com.ptithcm.payptithcm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ptithcm.payptithcm.activities.MainActivity;
import com.ptithcm.payptithcm.adapters.HomeAdapter;
import com.ptithcm.payptithcm.models.Student;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    GridView gvHome;
    TextView tvHomeName, tvTotalUnpaid, tvDebtCount, tvTotalPaid;
    LineChart lineChart;

    String[] titles = {"Khoản phí", "Thông tin", "Lịch sử", "Hỗ trợ"};
    int[] icons = {R.drawable.ic_fee, R.drawable.ic_bell, R.drawable.ic_history, R.drawable.ic_support};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvHomeName = view.findViewById(R.id.tvHomeName);
        tvTotalUnpaid = view.findViewById(R.id.tvTotalUnpaid);
        tvDebtCount = view.findViewById(R.id.tvDebtCount);
        tvTotalPaid = view.findViewById(R.id.tvTotalPaid);
        gvHome = view.findViewById(R.id.gvHome);
        lineChart = view.findViewById(R.id.lineChart);

        loadHomeData();

        gvHome.setAdapter(new HomeAdapter(getContext(), titles, icons));
        gvHome.setOnItemClickListener((parent, v, position, id) -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity main = (MainActivity) getActivity();
                if (position == 0) main.navigateTo(R.id.nav_fees);
                else if (position == 1) main.navigateTo(R.id.nav_info);
                else if (position == 2) main.navigateTo(R.id.nav_history);
                else if (position == 3) main.navigateTo(R.id.nav_support);
                else Toast.makeText(getContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHomeData();
    }

    private void loadHomeData() {
        if (getContext() == null) return;

        SharedPrefs prefs = new SharedPrefs(getContext());
        String mssv = prefs.getUser();
        if (mssv.isEmpty()) mssv = "21520001";

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        Student student = dbHelper.getStudentById(mssv);
        if (student != null) tvHomeName.setText(student.getFullName());

        tvTotalUnpaid.setText(String.format("%,d đ", dbHelper.getTotalUnpaid(mssv)));
        tvDebtCount.setText(String.format("%02d", dbHelper.countUnpaidFees(mssv)));
        tvTotalPaid.setText(String.format("%,d đ", calculateTotalPaid(mssv)));

        setupChart(mssv);
    }

    private void setupChart(String mssv) {
        if (lineChart == null) return;

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
        List<DatabaseHelper.FeeStats> statsList = dbHelper.getFeeStatsBySemester(mssv);

        if (statsList == null || statsList.isEmpty()) {
            lineChart.setNoDataText("Chưa có dữ liệu thống kê");
            lineChart.invalidate();
            return;
        }

        ArrayList<Entry> paidEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < statsList.size(); i++) {
            DatabaseHelper.FeeStats stats = statsList.get(i);
            paidEntries.add(new Entry(i, stats.paid / 1000000f));
            labels.add(stats.label);
        }

        // --- ĐƯỜNG: ĐÃ ĐÓNG (Màu xanh lá) ---
        LineDataSet paidSet = new LineDataSet(paidEntries, "Học phí đã đóng (Tr.đ)");
        paidSet.setColor(Color.parseColor("#16A34A"));
        paidSet.setCircleColor(Color.parseColor("#16A34A"));
        paidSet.setLineWidth(3f);
        paidSet.setCircleRadius(4f);
        paidSet.setDrawCircleHole(true);
        paidSet.setCircleHoleColor(Color.WHITE);
        paidSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        paidSet.setDrawFilled(true);
        paidSet.setDrawValues(true);
        paidSet.setValueTextSize(9f);
        paidSet.setFillColor(Color.parseColor("#DCFCE7"));
        paidSet.setFillAlpha(100);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(paidSet);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);

        // Cấu hình chung cho biểu đồ
        lineChart.getDescription().setEnabled(false);
        lineChart.setExtraOffsets(5, 10, 5, 10);
        lineChart.animateY(1000);
        lineChart.getAxisRight().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setTextSize(9f);
        xAxis.setLabelRotationAngle(-25f);
        xAxis.setDrawGridLines(false);
        xAxis.setYOffset(5f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#EEEEEE"));
        leftAxis.setSpaceTop(40f);

        Legend l = lineChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setYOffset(5f);

        lineChart.invalidate();
    }

    private long calculateTotalPaid(String mssv) {
        long total = 0;
        try {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM Payment WHERE student_id = ?", new String[]{mssv});
            if (cursor.moveToFirst()) total = cursor.getLong(0);
            cursor.close();
        } catch (Exception ignored) {}
        return total;
    }
}
