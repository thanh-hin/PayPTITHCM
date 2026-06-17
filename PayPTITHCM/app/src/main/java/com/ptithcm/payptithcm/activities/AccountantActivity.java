package com.ptithcm.payptithcm.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.payptithcm.R;
import com.ptithcm.payptithcm.adapters.AccountantFeeAdapter;
import com.ptithcm.payptithcm.utils.DatabaseHelper;
import com.ptithcm.payptithcm.utils.SharedPrefs;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountantActivity extends AppCompatActivity {
    TextView tvSummary, tvNoticeSummary, tvTotalAmount;
    EditText etSearchStudent;
    Spinner spinnerStatus;
    ListView lvFees;
    View tvEmpty;
    Button btnConfirmCash, btnCreateNotice, btnLogout;

    List<DatabaseHelper.AccountantFeeRecord> allFees = new ArrayList<>();
    List<DatabaseHelper.AccountantFeeRecord> displayFees = new ArrayList<>();
    List<StudentSearchRow> searchRows = new ArrayList<>();
    AccountantFeeAdapter adapter;
    SharedPrefs prefs;

    private String selectedStatus = "Chua dong";
    private String searchQuery = "";
    private String selectedSearchStudentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountant);

        prefs = new SharedPrefs(this);
        if (!prefs.isAccountant()) {
            finish();
            return;
        }

        tvSummary = findViewById(R.id.tvSummary);
        tvNoticeSummary = findViewById(R.id.tvNoticeSummary);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        etSearchStudent = findViewById(R.id.etSearchStudent);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        lvFees = findViewById(R.id.lvFees);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnConfirmCash = findViewById(R.id.btnConfirmCash);
        btnCreateNotice = findViewById(R.id.btnCreateNotice);
        btnLogout = findViewById(R.id.btnLogout);

        setupStatusFilter();
        setupSearch();
        setupListClick();
        setupActions();
        loadFees();
        updateNoticeSummary();
    }

    private void setupSearch() {
        etSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = normalizeSearchText(s.toString().trim());
                selectedSearchStudentId = "";
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupListClick() {
        lvFees.setOnItemClickListener((parent, view, position, id) -> {
            if (!searchQuery.isEmpty() && selectedSearchStudentId.isEmpty() && position >= 0 && position < searchRows.size()) {
                selectedSearchStudentId = searchRows.get(position).studentId;
                applyFilter();
            }
        });
    }

    private void setupStatusFilter() {
        List<String> statuses = new ArrayList<>();
        statuses.add("Chua dong");
        statuses.add("Da dong");
        statuses.add("Tat ca");

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spinnerStatus.setAdapter(spinnerAdapter);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = parent.getItemAtPosition(position).toString();
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupActions() {
        btnConfirmCash.setOnClickListener(v -> confirmCashPayment());
        btnCreateNotice.setOnClickListener(v -> showCreateNoticeDialog());
        btnLogout.setOnClickListener(v -> {
            prefs.clearUser();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadFees() {
        allFees = DatabaseHelper.getInstance(this).getAllStudentFeesForAccountant();
        applyFilter();
    }

    private void updateNoticeSummary() {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        int count = db.countFeeNotices();
        DatabaseHelper.FeeNoticeRecord latest = db.getLatestFeeNotice();
        if (latest == null) {
            tvNoticeSummary.setText("Thong bao da tao: 0");
        } else {
            tvNoticeSummary.setText("Thong bao da tao: " + count + " | Moi nhat: " + latest.title + " - " + String.format("%,d d", latest.amount));
        }
    }

    private void applyFilter() {
        if (!searchQuery.isEmpty() && selectedSearchStudentId.isEmpty()) {
            showStudentSearchResults();
            return;
        }

        displayFees = allFees.stream()
                .filter(item -> {
                    if (!selectedSearchStudentId.isEmpty()) {
                        if (!selectedSearchStudentId.equals(item.getStudentId())) {
                            return false;
                        }
                    }
                    if ("Tat ca".equals(selectedStatus)) return true;
                    if ("Da dong".equals(selectedStatus)) return "PAID".equals(item.getStatus());
                    return !"PAID".equals(item.getStatus());
                })
                .collect(Collectors.toList());

        adapter = new AccountantFeeAdapter(this, displayFees, this::updateSelection);
        lvFees.setAdapter(adapter);

        boolean empty = displayFees.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        lvFees.setVisibility(empty ? View.GONE : View.VISIBLE);
        updateSummary();
        updateSelection();
    }

    private void showStudentSearchResults() {
        Map<String, StudentSearchRow> unique = new LinkedHashMap<>();
        for (DatabaseHelper.AccountantFeeRecord item : allFees) {
            String studentId = normalizeSearchText(item.getStudentId());
            String studentName = normalizeSearchText(item.getStudentName());
            if (studentId.contains(searchQuery) || studentName.contains(searchQuery)) {
                unique.put(item.getStudentId(), new StudentSearchRow(
                        item.getStudentId(),
                        item.getStudentName(),
                        item.getClassName()
                ));
            }
        }

        searchRows = new ArrayList<>(unique.values());
        List<String> labels = searchRows.stream()
                .map(row -> row.studentName + " - " + row.studentId + " - " + row.className)
                .collect(Collectors.toList());

        ArrayAdapter<String> searchAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels);
        lvFees.setAdapter(searchAdapter);

        displayFees = new ArrayList<>();
        boolean empty = labels.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        lvFees.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvSummary.setText("Tim thay: " + labels.size() + " sinh vien");
        updateSelection();
    }

    private void updateSummary() {
        int paid = 0;
        int unpaid = 0;
        List<DatabaseHelper.AccountantFeeRecord> source = selectedSearchStudentId.isEmpty() ? allFees : displayFees;
        for (DatabaseHelper.AccountantFeeRecord fee : source) {
            if ("PAID".equals(fee.getStatus())) paid++;
            else unpaid++;
        }
        String prefix = selectedSearchStudentId.isEmpty() ? "" : "Ket qua tim kiem | ";
        tvSummary.setText(prefix + "Da dong: " + paid + " | Chua dong: " + unpaid);
    }

    static class StudentSearchRow {
        final String studentId;
        final String studentName;
        final String className;

        StudentSearchRow(String studentId, String studentName, String className) {
            this.studentId = studentId != null ? studentId : "";
            this.studentName = studentName != null ? studentName : "";
            this.className = className != null ? className : "Chua co lop";
        }
    }

    private String normalizeSearchText(String value) {
        if (value == null) return "";
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        normalized = normalized.replace("đ", "d").replace("Đ", "D");
        return normalized.toLowerCase(Locale.ROOT);
    }

    private void updateSelection() {
        long total = 0;
        int count = 0;
        for (DatabaseHelper.AccountantFeeRecord fee : displayFees) {
            if (fee.isSelected()) {
                total += fee.getAmount();
                count++;
            }
        }
        tvTotalAmount.setText(String.format("%,d d", total));
        btnConfirmCash.setEnabled(count > 0);
        btnConfirmCash.setText(count == 0 ? "Xac nhan da thu" : "Xac nhan da thu (" + count + ")");
    }

    private void confirmCashPayment() {
        List<DatabaseHelper.AccountantFeeRecord> selected = displayFees.stream()
                .filter(DatabaseHelper.AccountantFeeRecord::isSelected)
                .collect(Collectors.toList());
        if (selected.isEmpty()) return;

        long total = 0;
        ArrayList<Integer> ids = new ArrayList<>();
        for (DatabaseHelper.AccountantFeeRecord fee : selected) {
            total += fee.getAmount();
            ids.add(fee.getId());
        }

        long finalTotal = total;
        new AlertDialog.Builder(this)
                .setTitle("Xac nhan thu tien mat")
                .setMessage("Ghi nhan " + selected.size() + " khoan phi da thu tai van phong?\nTong tien: " + String.format("%,d d", finalTotal))
                .setPositiveButton("Xac nhan", (dialog, which) -> {
                    String txnId = DatabaseHelper.getInstance(this).markFeesPaidAtOffice(ids, prefs.getUser());
                    if (txnId != null) {
                        Toast.makeText(this, "Da cap nhat thanh cong: " + txnId, Toast.LENGTH_LONG).show();
                        loadFees();
                    } else {
                        Toast.makeText(this, "Khong the cap nhat thanh toan", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Huy", null)
                .show();
    }

    private void showCreateNoticeDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_fee_notice, null);
        EditText etTitle = dialogView.findViewById(R.id.etNoticeTitle);
        EditText etContent = dialogView.findViewById(R.id.etNoticeContent);
        EditText etStudentId = dialogView.findViewById(R.id.etStudentId);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etDeadline = dialogView.findViewById(R.id.etDeadline);
        Spinner spinnerSchoolYear = dialogView.findViewById(R.id.spinnerSchoolYear);
        Spinner spinnerSemester = dialogView.findViewById(R.id.spinnerSemester);
        CheckBox cbWholeClass = dialogView.findViewById(R.id.cbWholeClass);

        etTitle.setText("Thong bao dong hoc phi");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        etDeadline.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));

        List<String> schoolYears = new ArrayList<>();
        schoolYears.add("2024-2025");
        schoolYears.add("2025-2026");
        schoolYears.add("2026-2027");
        ArrayAdapter<String> yearAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, schoolYears);
        spinnerSchoolYear.setAdapter(yearAdapter);

        List<String> semesters = new ArrayList<>();
        semesters.add("Hoc ky 1");
        semesters.add("Hoc ky 2");
        semesters.add("Hoc ky 3");
        ArrayAdapter<String> semesterAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, semesters);
        spinnerSemester.setAdapter(semesterAdapter);
        spinnerSemester.setSelection(1);

        DatabaseHelper db = DatabaseHelper.getInstance(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Gui", null)
                .setNegativeButton("Huy", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            String studentIdInput = etStudentId.getText().toString().trim();
            String amountInput = etAmount.getText().toString().trim();
            String deadline = etDeadline.getText().toString().trim();
            String schoolYear = spinnerSchoolYear.getSelectedItem().toString();
            int semester = spinnerSemester.getSelectedItemPosition() + 1;

            if (title.isEmpty() || content.isEmpty() || studentIdInput.isEmpty() || amountInput.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Vui long nhap day du thong tin", Toast.LENGTH_SHORT).show();
                return;
            }

            long amount;
            try {
                amount = Long.parseLong(amountInput);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "So tien khong hop le", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(this, "So tien phai lon hon 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!deadline.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Toast.makeText(this, "Han dong phai co dang yyyy-MM-dd", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer classId = null;
            String studentId = studentIdInput;
            if (db.getStudentById(studentIdInput) == null) {
                Toast.makeText(this, "Khong tim thay MSSV nay", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cbWholeClass.isChecked()) {
                classId = db.getClassIdForStudent(studentIdInput);
                studentId = null;
                if (classId == null) {
                    Toast.makeText(this, "Sinh vien nay chua co lop", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            boolean ok = db.createFeeDueRequest(title, content, amount, deadline, semester, schoolYear, classId, studentId, prefs.getUser());
            if (ok) {
                Toast.makeText(this, "Da tao khoan phi can dong", Toast.LENGTH_SHORT).show();
                updateNoticeSummary();
                loadFees();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Khong the tao khoan phi", Toast.LENGTH_LONG).show();
            }
        }));

        dialog.show();
    }
}
