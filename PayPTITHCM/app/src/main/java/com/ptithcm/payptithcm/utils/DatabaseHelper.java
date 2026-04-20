package com.ptithcm.payptithcm.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ptithcm.payptithcm.models.FeeItem;
import com.ptithcm.payptithcm.models.HistoryItem;
import com.ptithcm.payptithcm.models.Student;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "payptithcm.db";
    private static final int DATABASE_VERSION = 2;
    private static DatabaseHelper instance;

    // Singleton - dung chung 1 ket noi
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
        // Bang lop hoc
        db.execSQL("CREATE TABLE Class (" +
                "class_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "class_name TEXT NOT NULL UNIQUE," +
                "faculty TEXT," +
                "course_year INTEGER)");

        // Bang sinh vien
        db.execSQL("CREATE TABLE Student (" +
                "student_id TEXT PRIMARY KEY," +
                "full_name TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "phone TEXT," +
                "class_id INTEGER," +
                "FOREIGN KEY(class_id) REFERENCES Class(class_id))");

        // Bang khoan phi cua sinh vien
        db.execSQL("CREATE TABLE StudentFee (" +
                "student_fee_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id TEXT NOT NULL," +
                "fee_name TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "deadline TEXT NOT NULL," +
                "status TEXT DEFAULT 'UNPAID'," +
                "paid_date TEXT," +
                "FOREIGN KEY(student_id) REFERENCES Student(student_id))");

        // Bang lich su thanh toan
        db.execSQL("CREATE TABLE Payment (" +
                "payment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id TEXT NOT NULL," +
                "fee_name TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "method TEXT," +
                "status TEXT DEFAULT 'SUCCESS'," +
                "transaction_id TEXT," +
                "payment_date TEXT NOT NULL," +
                "FOREIGN KEY(student_id) REFERENCES Student(student_id))");

        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        // Them lop
        db.execSQL("INSERT INTO Class (class_name, faculty, course_year) VALUES " +
                "('D21CQCN01-N', 'Cong nghe thong tin', 2021)");
        db.execSQL("INSERT INTO Class (class_name, faculty, course_year) VALUES " +
                "('D22CQDT01-N', 'Dien tu vien thong', 2022)");

        // Sinh vien mau - password = MSSV de test de
        db.execSQL("INSERT INTO Student (student_id, full_name, email, password, phone, class_id) " +
                "VALUES ('21520001', 'Nguyen Van An', 'an@student.ptithcm.edu.vn', '21520001', '0901234567', 1)");
        db.execSQL("INSERT INTO Student (student_id, full_name, email, password, phone, class_id) " +
                "VALUES ('21520002', 'Tran Thi Bich', 'bich@student.ptithcm.edu.vn', '21520002', '0912345678', 1)");
        db.execSQL("INSERT INTO Student (student_id, full_name, email, password, phone, class_id) " +
                "VALUES ('22520001', 'Le Van Cuong', 'cuong@student.ptithcm.edu.vn', '22520001', '0923456789', 2)");

        // Hoc phi SV 21520001 - co nhieu trang thai khac nhau de test
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) " +
                "VALUES ('21520001', 'Hoc phi HK2/2025', 9000000, '2025-06-30', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) " +
                "VALUES ('21520001', 'Phi ky tuc xa HK2/2025', 1500000, '2025-06-15', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) " +
                "VALUES ('21520001', 'Bao hiem y te 2025', 702000, '2025-03-31', 'OVERDUE')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) " +
                "VALUES ('21520001', 'Phi the sinh vien', 50000, '2025-05-01', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status, paid_date) " +
                "VALUES ('21520001', 'Hoc phi HK1/2024', 8500000, '2024-11-30', 'PAID', '2024-10-12')");

        // Hoc phi SV 21520002
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) " +
                "VALUES ('21520002', 'Hoc phi HK2/2025', 9000000, '2025-06-30', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) " +
                "VALUES ('21520002', 'Bao hiem y te 2025', 702000, '2025-03-31', 'UNPAID')");

        // Lich su thanh toan mau
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) " +
                "VALUES ('21520001', 'Hoc phi HK1/2024', 8500000, 'Chuyen khoan', 'SUCCESS', 'TXN20241012001', '2024-10-12 09:30:00')");
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) " +
                "VALUES ('21520001', 'Bao hiem y te 2024', 702000, 'Vi dien tu', 'SUCCESS', 'TXN20240901001', '2024-09-01 14:00:00')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Payment");
        db.execSQL("DROP TABLE IF EXISTS StudentFee");
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Class");
        onCreate(db);
    }

    // ===== DAO METHODS =====

    /** Dang nhap: kiem tra MSSV + mat khau */
    public Student authenticateStudent(String mssv, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT s.*, c.class_name, c.faculty FROM Student s " +
                "LEFT JOIN Class c ON s.class_id = c.class_id " +
                "WHERE s.student_id = ? AND s.password = ?",
                new String[]{mssv, password});
        Student student = null;
        if (cursor.moveToFirst()) {
            student = cursorToStudent(cursor);
        }
        cursor.close();
        return student;
    }

    /** Lay thong tin sinh vien theo MSSV */
    public Student getStudentById(String mssv) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT s.*, c.class_name, c.faculty FROM Student s " +
                "LEFT JOIN Class c ON s.class_id = c.class_id " +
                "WHERE s.student_id = ?",
                new String[]{mssv});
        Student student = null;
        if (cursor.moveToFirst()) {
            student = cursorToStudent(cursor);
        }
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

    /** Lay danh sach hoc phi cua sinh vien */
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

    /** Lay lich su thanh toan cua sinh vien */
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
     * Luu thanh toan + cap nhat trang thai hoc phi thanh PAID
     * @return transaction_id neu thanh cong, null neu loi
     */
    public String insertPayment(String mssv, List<FeeItem> selectedFees, String method) {
        SQLiteDatabase db = getWritableDatabase();
        String txnId = "TXN" + System.currentTimeMillis();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        db.beginTransaction();
        try {
            for (FeeItem fee : selectedFees) {
                ContentValues cv = new ContentValues();
                cv.put("student_id", mssv);
                cv.put("fee_name", fee.getName());
                cv.put("amount", fee.getAmount());
                cv.put("method", method);
                cv.put("status", "SUCCESS");
                cv.put("transaction_id", txnId);
                cv.put("payment_date", now);
                db.insert("Payment", null, cv);

                ContentValues upd = new ContentValues();
                upd.put("status", "PAID");
                upd.put("paid_date", now.substring(0, 10));
                db.update("StudentFee", upd, "student_fee_id = ?",
                        new String[]{String.valueOf(fee.getId())});
            }
            db.setTransactionSuccessful();
            return txnId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.endTransaction();
        }
    }

    /** Dem so khoan phi chua dong (UNPAID + OVERDUE) */
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

    /** Tinh tong so tien chua dong */
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
