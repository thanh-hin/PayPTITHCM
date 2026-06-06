package com.ptithcm.payptithcm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.activities.PaymentActivity;
import com.ptithcm.payptithcm.adapters.FeeAdapter;
import com.ptithcm.payptithcm.models.FeeItem;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FeeListFragment extends Fragment {

    ListView lvFees;
    Button btnPayNow;
    TextView tvTotalAmount;
    View tvEmptyFees;
    Spinner spinnerYear, spinnerSemester, spinnerStatus;

    List<FeeItem> allFees = new ArrayList<>();
    List<FeeItem> displayFees = new ArrayList<>();
    FeeAdapter adapter;

    private String selectedStatus = "Tất cả";
    private String selectedYear = "Tất cả";
    private String selectedSemester = "Tất cả";

    private final ActivityResultLauncher<Intent> paymentLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    loadFees();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fee_list, container, false);

        lvFees          = view.findViewById(R.id.lvFees);
        btnPayNow       = view.findViewById(R.id.btnPayNow);
        tvTotalAmount   = view.findViewById(R.id.tvTotalAmount);
        tvEmptyFees     = view.findViewById(R.id.tvEmptyFees);
        spinnerYear     = view.findViewById(R.id.spinnerYear);
        spinnerSemester = view.findViewById(R.id.spinnerSemester);
        spinnerStatus   = view.findViewById(R.id.spinnerStatus);

        loadFees();
        setupDropdowns();
        setupPayButton();
        return view;
    }

    private void loadFees() {
        if (getContext() == null) return;
        String mssv = new SharedPrefs(getContext()).getUser();
        allFees = DatabaseHelper.getInstance(getContext()).getStudentFees(mssv);
        
        updateYearDropdown();
        updateSemesterDropdown();
        updateStatusDropdown();
        applyFilters();
    }

    private void setupDropdowns() {
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent == spinnerYear) selectedYear = parent.getItemAtPosition(position).toString();
                else if (parent == spinnerSemester) selectedSemester = parent.getItemAtPosition(position).toString();
                else if (parent == spinnerStatus) selectedStatus = parent.getItemAtPosition(position).toString();
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerYear.setOnItemSelectedListener(listener);
        spinnerSemester.setOnItemSelectedListener(listener);
        spinnerStatus.setOnItemSelectedListener(listener);
    }

    private void updateYearDropdown() {
        Set<String> years = new HashSet<>();
        years.add("Năm học");
        for (FeeItem item : allFees) {
            if (item.getSchoolYear() != null) years.add(item.getSchoolYear());
        }
        List<String> yearList = new ArrayList<>(years);
        Collections.sort(yearList, (a, b) -> {
            if (a.equals("Năm học")) return -1;
            if (b.equals("Năm học")) return 1;
            return b.compareTo(a);
        });
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, yearList);
        spinnerYear.setAdapter(adapter);
    }

    private void updateSemesterDropdown() {
        List<String> semesters = new ArrayList<>();
        semesters.add("Học kỳ");
        semesters.add("Học kỳ 1");
        semesters.add("Học kỳ 2");
        semesters.add("Học kỳ 3");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, semesters);
        spinnerSemester.setAdapter(adapter);
    }

    private void updateStatusDropdown() {
        List<String> statuses = new ArrayList<>();
        statuses.add("Trạng thái");
        statuses.add("Chưa đóng");
        statuses.add("Đã đóng");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses);
        spinnerStatus.setAdapter(adapter);
    }

    private void applyFilters() {
        displayFees = allFees.stream()
                .filter(f -> {
                    if (selectedStatus.equals("Trạng thái") || selectedStatus.equals("Tất cả")) return true;
                    if (selectedStatus.equals("Chưa đóng")) return !"PAID".equals(f.getStatus());
                    if (selectedStatus.equals("Đã đóng")) return "PAID".equals(f.getStatus());
                    return true;
                })
                .filter(f -> selectedYear.equals("Năm học") || selectedYear.equals("Tất cả") || selectedYear.equals(f.getSchoolYear()))
                .filter(f -> {
                    if (selectedSemester.equals("Học kỳ") || selectedSemester.equals("Tất cả")) return true;
                    int sem = Integer.parseInt(selectedSemester.replaceAll("[^0-9]", ""));
                    return f.getSemester() == sem;
                })
                .collect(Collectors.toList());

        adapter = new FeeAdapter(getContext(), displayFees, this::updateTotal);
        lvFees.setAdapter(adapter);

        boolean empty = displayFees.isEmpty();
        tvEmptyFees.setVisibility(empty ? View.VISIBLE : View.GONE);
        lvFees.setVisibility(empty ? View.GONE : View.VISIBLE);
        updateTotal();
    }

    private void updateTotal() {
        long total = 0;
        int count = 0;
        for (FeeItem item : displayFees) {
            if (item.isSelected()) {
                total += item.getAmount();
                count++;
            }
        }
        tvTotalAmount.setText(String.format("%,d đ", total));
        btnPayNow.setEnabled(count > 0);
        btnPayNow.setText(count == 0 ? "Thanh toán" : "Thanh toán (" + count + ")");
    }

    private void setupPayButton() {
        btnPayNow.setOnClickListener(v -> {
            List<FeeItem> selected = displayFees.stream().filter(FeeItem::isSelected).collect(Collectors.toList());
            if (selected.isEmpty()) return;

            long total = 0;
            StringBuilder details = new StringBuilder();
            ArrayList<Integer> ids = new ArrayList<>();
            for (FeeItem item : selected) {
                total += item.getAmount();
                ids.add(item.getId());
                details.append("• ").append(item.getName()).append(": ").append(String.format("%,d đ", item.getAmount())).append("\n");
            }

            Intent intent = new Intent(getActivity(), PaymentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong("TOTAL_AMOUNT", total);
            bundle.putString("FEE_DETAIL", details.toString().trim());
            bundle.putIntegerArrayList("SELECTED_IDS", ids);
            intent.putExtra("data", bundle);
            paymentLauncher.launch(intent);
        });
    }
}
