package com.ptithcm.payptithcm.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ptithcm.payptithcm.models.FeeItem;
import com.ptithcm.payptithcm.models.HistoryItem;
import com.ptithcm.payptithcm.models.Student;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "payptithcm.db";
    private static final int DATABASE_VERSION = 4; // v4:

    private static DatabaseHelper instance;

    // Singleton - dùng chung 1 kết nối
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng lớp học
        db.execSQL("CREATE TABLE Class (" +
                "class_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "class_name TEXT NOT NULL UNIQUE," +
                "faculty TEXT," +
                "course_year INTEGER)");

        // Bảng sinh viên
        db.execSQL("CREATE TABLE Student (" +
                "student_id TEXT PRIMARY KEY," +
                "full_name TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "phone TEXT," +
                "class_id INTEGER," +
                "FOREIGN KEY(class_id) REFERENCES Class(class_id))");

        // Bảng khoản phí của sinh viên
        db.execSQL("CREATE TABLE StudentFee (" +
                "student_fee_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id TEXT NOT NULL," +
                "fee_name TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "deadline TEXT NOT NULL," +
                "status TEXT DEFAULT 'UNPAID'," +
                "paid_date TEXT," +
                "FOREIGN KEY(student_id) REFERENCES Student(student_id))");

        // Bảng lịch sử thanh toán
        db.execSQL("CREATE TABLE Payment (" +
                "payment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id TEXT NOT NULL," +
                "fee_name TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "method TEXT," +
                "status TEXT DEFAULT 'SUCCESS'," +
                "transaction_id TEXT UNIQUE," +
                "payment_date TEXT NOT NULL," +
                "FOREIGN KEY(student_id) REFERENCES Student(student_id))");

        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        // ===== LỚP HỌC =====
        db.execSQL("INSERT INTO Class (class_name, faculty, course_year) VALUES ('D21CQCN01-N', 'Công nghệ thông tin', 2021)");
        db.execSQL("INSERT INTO Class (class_name, faculty, course_year) VALUES ('D22CQDT01-N', 'Điện tử viễn thông', 2022)");
        db.execSQL("INSERT INTO Class (class_name, faculty, course_year) VALUES ('D23CQVT01-N', 'Viễn thông', 2023)");

        // ===== SINH VIÊN (mật khẩu SHA-256(MSSV)) =====
        db.execSQL("INSERT INTO Student (student_id, full_name, email, password, phone, class_id) VALUES ('21520001', 'Nguyễn Văn An', 'an@student.ptithcm.edu.vn', '" + HashUtils.sha256("21520001") + "', '0901234567', 1)");
        db.execSQL("INSERT INTO Student (student_id, full_name, email, password, phone, class_id) VALUES ('21520002', 'Trần Thị Bình', 'binh@student.ptithcm.edu.vn', '" + HashUtils.sha256("21520002") + "', '0912345678', 1)");
        db.execSQL("INSERT INTO Student (student_id, full_name, email, password, phone, class_id) VALUES ('22520001', 'Lê Văn Cường', 'cuong@student.ptithcm.edu.vn', '" + HashUtils.sha256("22520001") + "', '0923456789', 2)");
        db.execSQL("INSERT INTO Student (student_id, full_name, email, password, phone, class_id) VALUES ('23520001', 'Phạm Thị Dung', 'dung@student.ptithcm.edu.vn', '" + HashUtils.sha256("23520001") + "', '0934567890', 3)");

        // ===== HỌC PHÍ SV 21520001 (nhiều loại trạng thái để demo) =====
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('21520001', 'Học phí HK2/2025', 9200000, '2025-07-31', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('21520001', 'Phí ký túc xá HK2/2025', 1600000, '2025-07-15', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('21520001', 'Bảo hiểm y tế 2025', 702000, '2025-03-31', 'OVERDUE')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('21520001', 'Phí thẻ sinh viên', 50000, '2025-09-30', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status, paid_date) VALUES ('21520001', 'Học phí HK1/2025', 8800000, '2024-12-31', 'PAID', '2024-12-15')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status, paid_date) VALUES ('21520001', 'Học phí HK2/2024', 8500000, '2024-06-30', 'PAID', '2024-06-10')");

        // ===== HỌC PHÍ SV 21520002 =====
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('21520002', 'Học phí HK2/2025', 9200000, '2025-07-31', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('21520002', 'Bảo hiểm y tế 2025', 702000, '2025-03-31', 'OVERDUE')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status, paid_date) VALUES ('21520002', 'Học phí HK1/2025', 8800000, '2024-12-31', 'PAID', '2024-12-20')");

        // ===== HỌC PHÍ SV 22520001 =====
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('22520001', 'Học phí HK2/2025', 9000000, '2025-07-31', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status, paid_date) VALUES ('22520001', 'Học phí HK1/2025', 8800000, '2024-12-31', 'PAID', '2024-12-05')");

        // ===== HỌC PHÍ SV 23520001 (tân sinh viên) =====
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('23520001', 'Học phí HK1/2025', 9500000, '2025-01-31', 'OVERDUE')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('23520001', 'Phí nhập học', 500000, '2024-09-30', 'OVERDUE')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('23520001', 'Bảo hiểm y tế 2025', 702000, '2025-03-31', 'UNPAID')");

        // ===== LỊCH SỬ THANH TOÁN MẪU =====
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES ('21520001', 'Học phí HK1/2025', 8800000, '🏦  Chuyển khoản ngân hàng', 'SUCCESS', 'TXN20241215001', '2024-12-15 09:30:00')");
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES ('21520001', 'Học phí HK2/2024', 8500000, '📱  Ví điện tử (MoMo, ZaloPay)', 'SUCCESS', 'TXN20240610001', '2024-06-10 14:20:00')");
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES ('21520002', 'Học phí HK1/2025', 8800000, '🏦  Chuyển khoản ngân hàng', 'SUCCESS', 'TXN20241220001', '2024-12-20 10:00:00')");
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES ('22520001', 'Học phí HK1/2025', 8800000, '💵  Tiền mặt', 'SUCCESS', 'TXN20241205001', '2024-12-05 11:00:00')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: " + oldVersion + " -> " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS Payment");
        db.execSQL("DROP TABLE IF EXISTS StudentFee");
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Class");
        onCreate(db);
    }

    // ===== DAO METHODS =====

    /** Đăng nhập: kiểm tra MSSV + mật khẩu (SHA-256) */
    public Student authenticateStudent(String mssv, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String hashedPass = HashUtils.sha256(password);
        Cursor cursor = db.rawQuery(
                "SELECT s.*, c.class_name, c.faculty FROM Student s " +
                "LEFT JOIN Class c ON s.class_id = c.class_id " +
                "WHERE s.student_id = ? AND s.password = ?",
                new String[]{mssv, hashedPass});
        Student student = null;
        if (cursor.moveToFirst()) student = cursorToStudent(cursor);
        cursor.close();
        return student;
    }

    /** Đăng nhập bằng email + mật khẩu (SHA-256) */
    public Student authenticateByEmail(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String hashedPass = HashUtils.sha256(password);
        Cursor cursor = db.rawQuery(
                "SELECT s.*, c.class_name, c.faculty FROM Student s " +
                "LEFT JOIN Class c ON s.class_id = c.class_id " +
                "WHERE s.email = ? AND s.password = ?",
                new String[]{email, hashedPass});
        Student student = null;
        if (cursor.moveToFirst()) student = cursorToStudent(cursor);
        cursor.close();
        return student;
    }

    /** Lấy thông tin sinh viên theo MSSV */
    public Student getStudentById(String mssv) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT s.*, c.class_name, c.faculty FROM Student s " +
                "LEFT JOIN Class c ON s.class_id = c.class_id " +
                "WHERE s.student_id = ?",
                new String[]{mssv});
        Student student = null;
        if (cursor.moveToFirst()) student = cursorToStudent(cursor);
        cursor.close();
        return student;
    }

    private Student cursorToStudent(Cursor cursor) {
        return new Student(
                cursor.getString(cursor.getColumnIndexOrThrow("student_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                safeGetString(cursor, "class_name"),
                safeGetString(cursor, "faculty")
        );
    }

    private String safeGetString(Cursor cursor, String column) {
        int idx = cursor.getColumnIndex(column);
        return (idx >= 0 && !cursor.isNull(idx)) ? cursor.getString(idx) : "";
    }

    /** Lấy danh sách học phí của sinh viên (OVERDUE trước, UNPAID sau, PAID cuối) */
    public List<FeeItem> getStudentFees(String mssv) {
        List<FeeItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM StudentFee WHERE student_id = ? " +
                "ORDER BY CASE status WHEN 'OVERDUE' THEN 1 WHEN 'UNPAID' THEN 2 ELSE 3 END, deadline ASC",
                new String[]{mssv});
        while (cursor.moveToNext()) {
            list.add(new FeeItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("student_fee_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fee_name")),
                    (long) cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    cursor.getString(cursor.getColumnIndexOrThrow("deadline"))
            ));
        }
        cursor.close();
        return list;
    }

    /** Lấy lịch sử thanh toán của sinh viên */
    public List<HistoryItem> getPaymentHistory(String mssv) {
        List<HistoryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Payment WHERE student_id = ? ORDER BY payment_date DESC",
                new String[]{mssv});
        while (cursor.moveToNext()) {
            list.add(new HistoryItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("payment_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("payment_date")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fee_name")),
                    (long) cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    cursor.getString(cursor.getColumnIndexOrThrow("method")),
                    cursor.getString(cursor.getColumnIndexOrThrow("transaction_id"))
            ));
        }
        cursor.close();
        return list;
    }

    /**
     * Lưu thanh toán + cập nhật trạng thái học phí thành PAID.
     * Toàn bộ trong 1 SQLite transaction để đảm bảo atomicity.
     * @return transaction_id nếu thành công, null nếu lỗi
     */
    public String insertPayment(String mssv, List<FeeItem> selectedFees, String method) {
        if (selectedFees == null || selectedFees.isEmpty()) return null;

        SQLiteDatabase db = getWritableDatabase();
        // Strip emoji khỏi method name để lưu gọn hơn
        String cleanMethod = method.replaceAll("[^\\p{L}\\p{Nd}\\s/(),.-]", "").trim();

        String txnId = "TXN" + System.currentTimeMillis();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        db.beginTransaction();
        try {
            for (FeeItem fee : selectedFees) {
                // Verify fee vẫn còn chưa đóng trước khi xử lý
                Cursor check = db.rawQuery(
                    "SELECT status FROM StudentFee WHERE student_fee_id = ? AND student_id = ?",
                    new String[]{String.valueOf(fee.getId()), mssv});
                String currentStatus = null;
                if (check.moveToFirst()) currentStatus = check.getString(0);
                check.close();

                if (!"UNPAID".equals(currentStatus) && !"OVERDUE".equals(currentStatus)) {
                    Log.w(TAG, "Fee " + fee.getId() + " already PAID or not found, skipping");
                    continue; // Bỏ qua fee đã đóng (không throw error để tránh block toàn bộ)
                }

                ContentValues cv = new ContentValues();
                cv.put("student_id", mssv);
                cv.put("fee_name", fee.getName());
                cv.put("amount", fee.getAmount());
                cv.put("method", cleanMethod);
                cv.put("status", "SUCCESS");
                cv.put("transaction_id", txnId + "_" + fee.getId()); // unique per fee
                cv.put("payment_date", now);
                db.insert("Payment", null, cv);

                ContentValues upd = new ContentValues();
                upd.put("status", "PAID");
                upd.put("paid_date", now.substring(0, 10));
                db.update("StudentFee", upd,
                    "student_fee_id = ? AND student_id = ?",
                    new String[]{String.valueOf(fee.getId()), mssv});
            }
            db.setTransactionSuccessful();
            return txnId;
        } catch (Exception e) {
            Log.e(TAG, "insertPayment failed: " + e.getMessage(), e);
            return null;
        } finally {
            db.endTransaction();
        }
    }

    /** Đếm số khoản phí chưa đóng (UNPAID + OVERDUE) */
    public int countUnpaidFees(String mssv) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM StudentFee WHERE student_id = ? AND status != 'PAID'",
                new String[]{mssv});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /** Tính tổng số tiền chưa đóng */
    public long getTotalUnpaid(String mssv) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(amount) FROM StudentFee WHERE student_id = ? AND status != 'PAID'",
                new String[]{mssv});
        long total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) total = (long) cursor.getDouble(0);
        cursor.close();
        return total;
    }
}
