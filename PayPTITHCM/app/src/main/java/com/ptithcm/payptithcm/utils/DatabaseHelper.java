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
    private static final int DATABASE_VERSION = 16;

    private static DatabaseHelper instance;

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
        db.execSQL("CREATE TABLE Class (class_id INTEGER PRIMARY KEY AUTOINCREMENT, class_name TEXT NOT NULL UNIQUE, faculty TEXT, course_year INTEGER)");
        db.execSQL("CREATE TABLE Student (student_id TEXT PRIMARY KEY, full_name TEXT NOT NULL, email TEXT NOT NULL UNIQUE, password TEXT NOT NULL, phone TEXT, class_id INTEGER, FOREIGN KEY(class_id) REFERENCES Class(class_id))");
        db.execSQL("CREATE TABLE Account (username TEXT PRIMARY KEY, password TEXT NOT NULL, role TEXT NOT NULL, student_id TEXT, display_name TEXT, FOREIGN KEY(student_id) REFERENCES Student(student_id))");
        db.execSQL("CREATE TABLE FeeNotice (notice_id INTEGER PRIMARY KEY AUTOINCREMENT, student_id TEXT, class_id INTEGER, title TEXT NOT NULL, content TEXT NOT NULL, amount REAL NOT NULL DEFAULT 0, created_by TEXT, created_at TEXT NOT NULL, FOREIGN KEY(student_id) REFERENCES Student(student_id), FOREIGN KEY(class_id) REFERENCES Class(class_id))");
        db.execSQL("CREATE TABLE StudentFee (student_fee_id INTEGER PRIMARY KEY AUTOINCREMENT, student_id TEXT NOT NULL, fee_name TEXT NOT NULL, semester INTEGER, school_year TEXT, amount REAL NOT NULL, deadline TEXT NOT NULL, status TEXT DEFAULT 'UNPAID', paid_date TEXT, FOREIGN KEY(student_id) REFERENCES Student(student_id))");
        db.execSQL("CREATE TABLE Payment (payment_id INTEGER PRIMARY KEY AUTOINCREMENT, student_id TEXT NOT NULL, fee_name TEXT NOT NULL, amount REAL NOT NULL, method TEXT, status TEXT DEFAULT 'SUCCESS', transaction_id TEXT UNIQUE, payment_date TEXT NOT NULL, FOREIGN KEY(student_id) REFERENCES Student(student_id))");

        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Class (class_name, faculty, course_year) VALUES ('D21CQCN01-N', 'Công nghệ thông tin', 2021)");
        db.execSQL("INSERT INTO Class (class_name, faculty, course_year) VALUES ('D21CQCN02-N', 'Công nghệ thông tin', 2021)");

        db.execSQL("INSERT INTO Student (student_id, full_name, email, password, phone, class_id) VALUES " +
                "('21520001', 'Nguyễn Văn An', 'n22dcat021@student.ptithcm.edu.vn', '" + HashUtils.sha256("21520001") + "', '0901234567', 1)");
        db.execSQL("INSERT INTO Student (student_id, full_name, email, password, phone, class_id) VALUES " +
                "('21520002', 'Trần Thị Bình', 'binh@student.ptithcm.edu.vn', '" + HashUtils.sha256("21520002") + "', '0912345678', 2)");

        db.execSQL("INSERT INTO Account (username, password, role, student_id, display_name) VALUES " +
                "('21520001', '" + HashUtils.sha256("21520001") + "', 'STUDENT', '21520001', 'Nguyễn Văn An')");
        db.execSQL("INSERT INTO Account (username, password, role, student_id, display_name) VALUES " +
                "('21520002', '" + HashUtils.sha256("21520002") + "', 'STUDENT', '21520002', 'Trần Thị Bình')");
        db.execSQL("INSERT INTO Account (username, password, role, student_id, display_name) VALUES " +
                "('ketoan', '" + HashUtils.sha256("123456") + "', 'ACCOUNTANT', NULL, 'Kế toán phòng tài chính')");

        String sId = "21520001";

        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, semester, school_year, amount, deadline, status, paid_date) VALUES ('" + sId + "', 'Học phí', 1, '2022-2023', 7500000, '2022-12-31', 'PAID', '2022-12-10')");
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES ('" + sId + "', 'Học phí HK1 22-23', 7500000, 'Tiền mặt', 'SUCCESS', 'TXN001', '2022-12-10 08:00:00')");

        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, semester, school_year, amount, deadline, status, paid_date) VALUES ('" + sId + "', 'Học phí', 2, '2022-2023', 7800000, '2023-06-30', 'PAID', '2023-06-05')");
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES ('" + sId + "', 'Học phí HK2 22-23', 7800000, 'Tiền mặt', 'SUCCESS', 'TXN002', '2023-06-05 09:00:00')");

        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, semester, school_year, amount, deadline, status, paid_date) VALUES ('" + sId + "', 'Học phí', 1, '2023-2024', 8200000, '2023-12-31', 'PAID', '2023-12-20')");
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES ('" + sId + "', 'Học phí HK1 23-24', 8200000, 'Tiền mặt', 'SUCCESS', 'TXN003', '2023-12-20 10:00:00')");

        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, semester, school_year, amount, deadline, status, paid_date) VALUES ('" + sId + "', 'Học phí', 2, '2023-2024', 8500000, '2024-06-30', 'PAID', '2024-06-10')");
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES ('" + sId + "', 'Học phí HK2 23-24', 8500000, 'Tiền mặt', 'SUCCESS', 'TXN004', '2024-06-10 11:00:00')");

        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, semester, school_year, amount, deadline, status, paid_date) VALUES ('" + sId + "', 'Học phí', 1, '2024-2025', 8800000, '2024-12-31', 'PAID', '2024-12-15')");
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES ('" + sId + "', 'Học phí HK1 24-25', 8800000, 'Tiền mặt', 'SUCCESS', 'TXN005', '2024-12-15 12:00:00')");

        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, semester, school_year, amount, deadline, status) VALUES ('21520001', 'Học phí', 2, '2024-2025', 9200000, '2025-07-31', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, semester, school_year, amount, deadline, status) VALUES ('21520001', 'Lệ phí thi', 2, '2024-2025', 500000, '2025-05-31', 'UNPAID')");

        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, semester, school_year, amount, deadline, status) VALUES ('21520002', 'Học phí', 2, '2024-2025', 9200000, '2025-07-31', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, semester, school_year, amount, deadline, status, paid_date) VALUES ('21520002', 'Học phí', 1, '2024-2025', 8800000, '2024-12-31', 'PAID', '2024-12-20')");
        db.execSQL("INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES ('21520002', 'Học phí HK1 24-25', 8800000, 'VNPAY', 'SUCCESS', 'TXN006', '2024-12-20 10:00:00')");
        db.execSQL("INSERT INTO FeeNotice (student_id, class_id, title, content, amount, created_by, created_at) VALUES (NULL, 1, 'Thong bao dong hoc phi', 'Sinh vien lop D21CQCN01-N vui long hoan thanh hoc phi HK2/2024-2025 truoc ngay 31/07/2025.', 9200000, 'ketoan', '2025-06-01 08:00:00')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Payment");
        db.execSQL("DROP TABLE IF EXISTS StudentFee");
        db.execSQL("DROP TABLE IF EXISTS FeeNotice");
        db.execSQL("DROP TABLE IF EXISTS Account");
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Class");
        onCreate(db);
    }

    public static class AuthUser {
        public String username;
        public String role;
        public String studentId;
        public String displayName;
    }

    public AuthUser authenticateAccount(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String hashedPass = HashUtils.sha256(password);

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Account WHERE username = ? AND password = ?",
                new String[]{username, hashedPass}
        );

        AuthUser user = null;
        if (cursor.moveToFirst()) {
            user = new AuthUser();
            user.username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            user.role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            user.studentId = cursor.getString(cursor.getColumnIndexOrThrow("student_id"));
            user.displayName = cursor.getString(cursor.getColumnIndexOrThrow("display_name"));
        }

        cursor.close();
        return user;
    }

    public Student authenticateStudent(String mssv, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String hashedPass = HashUtils.sha256(password);

        Cursor cursor = db.rawQuery(
                "SELECT s.*, c.class_name, c.faculty FROM Student s LEFT JOIN Class c ON s.class_id = c.class_id WHERE s.student_id = ? AND s.password = ?",
                new String[]{mssv, hashedPass}
        );

        Student student = null;

        if (cursor.moveToFirst()) {
            student = new Student(
                    cursor.getString(cursor.getColumnIndexOrThrow("student_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    cursor.getString(cursor.getColumnIndexOrThrow("class_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("faculty"))
            );
        }

        cursor.close();
        return student;
    }

    public Student authenticateByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT s.*, c.class_name, c.faculty FROM Student s LEFT JOIN Class c ON s.class_id = c.class_id WHERE s.email = ?",
                new String[]{email}
        );

        Student student = null;

        if (cursor.moveToFirst()) {
            student = new Student(
                    cursor.getString(cursor.getColumnIndexOrThrow("student_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    cursor.getString(cursor.getColumnIndexOrThrow("class_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("faculty"))
            );
        }

        cursor.close();
        return student;
    }

    public Student getStudentById(String mssv) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT s.*, c.class_name, c.faculty FROM Student s LEFT JOIN Class c ON s.class_id = c.class_id WHERE s.student_id = ?",
                new String[]{mssv}
        );

        Student student = null;

        if (cursor.moveToFirst()) {
            student = new Student(
                    cursor.getString(cursor.getColumnIndexOrThrow("student_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    cursor.getString(cursor.getColumnIndexOrThrow("class_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("faculty"))
            );
        }

        cursor.close();
        return student;
    }

    public List<FeeItem> getStudentFees(String mssv) {
        List<FeeItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM StudentFee WHERE student_id = ? ORDER BY school_year DESC, semester DESC",
                new String[]{mssv}
        );

        while (cursor.moveToNext()) {
            list.add(new FeeItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("student_fee_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fee_name")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("semester")),
                    cursor.getString(cursor.getColumnIndexOrThrow("school_year")),
                    (long) cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    cursor.getString(cursor.getColumnIndexOrThrow("deadline"))
            ));
        }

        cursor.close();
        return list;
    }

    public static class FeeStats {
        public String label;
        public long paid;
        public long unpaid;
    }

    public List<FeeStats> getFeeStatsBySemester(String mssv) {
        List<FeeStats> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT school_year, semester, SUM(CASE WHEN status = 'PAID' THEN amount ELSE 0 END) as paid, SUM(CASE WHEN status != 'PAID' THEN amount ELSE 0 END) as unpaid FROM StudentFee WHERE student_id = ? GROUP BY school_year, semester ORDER BY school_year ASC, semester ASC",
                new String[]{mssv}
        );

        while (cursor.moveToNext()) {
            FeeStats stats = new FeeStats();
            stats.label = "HK" + cursor.getInt(cursor.getColumnIndexOrThrow("semester")) + "\n" + cursor.getString(cursor.getColumnIndexOrThrow("school_year")).substring(2, 4);
            stats.paid = cursor.getLong(cursor.getColumnIndexOrThrow("paid"));
            stats.unpaid = cursor.getLong(cursor.getColumnIndexOrThrow("unpaid"));
            list.add(stats);
        }

        cursor.close();
        return list;
    }

    public long getTotalUnpaid(String mssv) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(amount) FROM StudentFee WHERE student_id = ? AND status != 'PAID'",
                new String[]{mssv}
        );

        long total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getLong(0);
        }

        cursor.close();
        return total;
    }

    public int countUnpaidFees(String mssv) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM StudentFee WHERE student_id = ? AND status != 'PAID'",
                new String[]{mssv}
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

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
                cv.put("transaction_id", txnId + "_" + fee.getId());
                cv.put("payment_date", now);

                db.insert("Payment", null, cv);

                ContentValues upd = new ContentValues();
                upd.put("status", "PAID");
                upd.put("paid_date", now.substring(0, 10));

                db.update(
                        "StudentFee",
                        upd,
                        "student_fee_id = ?",
                        new String[]{String.valueOf(fee.getId())}
                );
            }

            db.setTransactionSuccessful();
            return txnId;
        } catch (Exception e) {
            return null;
        } finally {
            db.endTransaction();
        }
    }

    public List<HistoryItem> getPaymentHistory(String mssv) {
        List<HistoryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Payment WHERE student_id = ? ORDER BY payment_date DESC",
                new String[]{mssv}
        );

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

    public static class AccountantFeeRecord {
        private final int id;
        private final String studentId;
        private final String studentName;
        private final String feeName;
        private final int semester;
        private final String schoolYear;
        private final long amount;
        private final String status;
        private final String deadline;
        private boolean selected;

        public AccountantFeeRecord(int id, String studentId, String studentName, String feeName,
                                   int semester, String schoolYear, long amount, String status, String deadline) {
            this.id = id;
            this.studentId = studentId;
            this.studentName = studentName;
            this.feeName = feeName;
            this.semester = semester;
            this.schoolYear = schoolYear;
            this.amount = amount;
            this.status = status;
            this.deadline = deadline;
        }

        public int getId() { return id; }
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getFeeName() { return feeName; }
        public int getSemester() { return semester; }
        public String getSchoolYear() { return schoolYear; }
        public long getAmount() { return amount; }
        public String getStatus() { return status; }
        public String getDeadline() { return deadline; }
        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) { this.selected = selected; }
    }

    public List<AccountantFeeRecord> getAllStudentFeesForAccountant() {
        List<AccountantFeeRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT f.*, s.full_name FROM StudentFee f " +
                        "JOIN Student s ON s.student_id = f.student_id " +
                        "ORDER BY CASE f.status WHEN 'OVERDUE' THEN 1 WHEN 'UNPAID' THEN 2 ELSE 3 END, f.deadline ASC",
                null
        );

        while (cursor.moveToNext()) {
            list.add(new AccountantFeeRecord(
                    cursor.getInt(cursor.getColumnIndexOrThrow("student_fee_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("student_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fee_name")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("semester")),
                    cursor.getString(cursor.getColumnIndexOrThrow("school_year")),
                    (long) cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    cursor.getString(cursor.getColumnIndexOrThrow("deadline"))
            ));
        }

        cursor.close();
        return list;
    }

    public String markFeesPaidAtOffice(List<Integer> feeIds, String accountantUsername) {
        if (feeIds == null || feeIds.isEmpty()) return null;

        SQLiteDatabase db = getWritableDatabase();
        String txnId = "OFFICE" + System.currentTimeMillis();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        db.beginTransaction();
        try {
            for (Integer feeId : feeIds) {
                Cursor cursor = db.rawQuery(
                        "SELECT * FROM StudentFee WHERE student_fee_id = ? AND status != 'PAID'",
                        new String[]{String.valueOf(feeId)}
                );

                if (cursor.moveToFirst()) {
                    String studentId = cursor.getString(cursor.getColumnIndexOrThrow("student_id"));
                    String feeName = cursor.getString(cursor.getColumnIndexOrThrow("fee_name"));
                    long amount = (long) cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

                    ContentValues payment = new ContentValues();
                    payment.put("student_id", studentId);
                    payment.put("fee_name", feeName);
                    payment.put("amount", amount);
                    payment.put("method", "Tien mat tai van phong - " + accountantUsername);
                    payment.put("status", "SUCCESS");
                    payment.put("transaction_id", txnId + "_" + feeId);
                    payment.put("payment_date", now);
                    db.insert("Payment", null, payment);

                    ContentValues update = new ContentValues();
                    update.put("status", "PAID");
                    update.put("paid_date", now.substring(0, 10));
                    db.update("StudentFee", update, "student_fee_id = ?", new String[]{String.valueOf(feeId)});
                }

                cursor.close();
            }

            db.setTransactionSuccessful();
            return txnId;
        } catch (Exception e) {
            return null;
        } finally {
            db.endTransaction();
        }
    }

    public static class ClassOption {
        public final int id;
        public final String name;

        public ClassOption(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class StudentOption {
        public final String id;
        public final String name;

        public StudentOption(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return id + " - " + name;
        }
    }

    public static class FeeNoticeRecord {
        public final String title;
        public final String content;
        public final long amount;
        public final String createdAt;

        public FeeNoticeRecord(String title, String content, long amount, String createdAt) {
            this.title = title;
            this.content = content;
            this.amount = amount;
            this.createdAt = createdAt;
        }
    }

    public List<ClassOption> getClassOptions() {
        List<ClassOption> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT class_id, class_name FROM Class ORDER BY class_name", null);

        while (cursor.moveToNext()) {
            list.add(new ClassOption(
                    cursor.getInt(cursor.getColumnIndexOrThrow("class_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("class_name"))
            ));
        }

        cursor.close();
        return list;
    }

    public List<StudentOption> getStudentsByClass(int classId) {
        List<StudentOption> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT student_id, full_name FROM Student WHERE class_id = ? ORDER BY student_id",
                new String[]{String.valueOf(classId)}
        );

        while (cursor.moveToNext()) {
            list.add(new StudentOption(
                    cursor.getString(cursor.getColumnIndexOrThrow("student_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("full_name"))
            ));
        }

        cursor.close();
        return list;
    }

    public Integer getClassIdForStudent(String studentId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT class_id FROM Student WHERE student_id = ?",
                new String[]{studentId}
        );

        Integer classId = null;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            classId = cursor.getInt(0);
        }

        cursor.close();
        return classId;
    }

    public boolean createFeeNotice(String title, String content, long amount, Integer classId, String studentId, String createdBy) {
        SQLiteDatabase db = getWritableDatabase();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        return insertFeeNotice(db, title, content, amount, classId, studentId, createdBy, now) != -1;
    }

    private long insertFeeNotice(SQLiteDatabase db, String title, String content, long amount,
                                 Integer classId, String studentId, String createdBy, String now) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("amount", amount);
        values.put("created_by", createdBy);
        values.put("created_at", now);
        if (classId != null) values.put("class_id", classId);
        if (studentId != null) values.put("student_id", studentId);
        return db.insert("FeeNotice", null, values);
    }

    public boolean createFeeDueRequest(String title, String content, long amount, String deadline,
                                       int semester, String schoolYear,
                                       Integer classId, String studentId, String createdBy) {
        SQLiteDatabase db = getWritableDatabase();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        db.beginTransaction();
        try {
            if (classId != null) {
                insertFeeNotice(db, title, content, amount, classId, null, createdBy, now);
                List<StudentOption> students = getStudentsByClass(classId);
                for (StudentOption student : students) {
                    insertStudentFee(db, student.id, content, semester, schoolYear, amount, deadline);
                }
            } else if (studentId != null) {
                insertFeeNotice(db, title, content, amount, null, studentId, createdBy, now);
                insertStudentFee(db, studentId, content, semester, schoolYear, amount, deadline);
            } else {
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    private void insertStudentFee(SQLiteDatabase db, String studentId, String feeName,
                                  int semester, String schoolYear, long amount, String deadline) {
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("fee_name", feeName);
        values.put("semester", semester);
        values.put("school_year", schoolYear);
        values.put("amount", amount);
        values.put("deadline", deadline);
        values.put("status", "UNPAID");
        db.insert("StudentFee", null, values);
    }

    public List<FeeNoticeRecord> getFeeNoticesForStudent(String studentId) {
        List<FeeNoticeRecord> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT n.title, n.content, n.amount, n.created_at FROM FeeNotice n " +
                        "LEFT JOIN Student s ON s.student_id = ? " +
                        "WHERE n.student_id = ? OR (n.class_id IS NOT NULL AND n.class_id = s.class_id) " +
                        "ORDER BY n.created_at DESC",
                new String[]{studentId, studentId}
        );

        while (cursor.moveToNext()) {
            list.add(new FeeNoticeRecord(
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("content")),
                    (long) cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            ));
        }

        cursor.close();
        return list;
    }

    public int countFeeNotices() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM FeeNotice", null);

        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);

        cursor.close();
        return count;
    }

    public FeeNoticeRecord getLatestFeeNotice() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT title, content, amount, created_at FROM FeeNotice ORDER BY created_at DESC LIMIT 1",
                null
        );

        FeeNoticeRecord record = null;
        if (cursor.moveToFirst()) {
            record = new FeeNoticeRecord(
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("content")),
                    (long) cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            );
        }

        cursor.close();
        return record;
    }
}
