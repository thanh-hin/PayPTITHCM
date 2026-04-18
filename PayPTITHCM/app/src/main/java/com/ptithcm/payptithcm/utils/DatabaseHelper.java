package com.ptithcm.payptithcm.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "payptithcm_final.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper instance;

    // Singleton pattern để dùng chung 1 kết nối duy nhất
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
        // 1. Class
        db.execSQL("CREATE TABLE Class (class_id INTEGER PRIMARY KEY AUTOINCREMENT, class_name TEXT NOT NULL UNIQUE, faculty TEXT, course_year INTEGER, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // 2. Student
        db.execSQL("CREATE TABLE Student (student_id TEXT PRIMARY KEY, full_name TEXT NOT NULL, email TEXT NOT NULL UNIQUE, password_hash TEXT NOT NULL, phone TEXT, avatar_url TEXT, class_id INTEGER, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(class_id) REFERENCES Class(class_id) ON DELETE SET NULL)");

        // 3. FeeType
        db.execSQL("CREATE TABLE FeeType (type_code TEXT PRIMARY KEY, type_name TEXT NOT NULL, description TEXT, icon TEXT, color TEXT, sort_order INTEGER DEFAULT 0)");

        // 4. StudentFee (Bảng quan trọng nhất cho Fragment_Fee_List)
        db.execSQL("CREATE TABLE StudentFee (" +
                "student_fee_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id TEXT NOT NULL, " +
                "fee_name TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "deadline TEXT NOT NULL, " +
                "status TEXT DEFAULT 'UNPAID', " + // UNPAID, PAID, OVERDUE
                "paid_date TEXT, " +
                "FOREIGN KEY(student_id) REFERENCES Student(student_id) ON DELETE CASCADE)");

        // 5. Payment & PaymentDetail
        db.execSQL("CREATE TABLE Payment (payment_id INTEGER PRIMARY KEY AUTOINCREMENT, student_id TEXT NOT NULL, total_amount REAL, payment_date DATETIME DEFAULT CURRENT_TIMESTAMP, status TEXT, transaction_id TEXT)");
        db.execSQL("CREATE TABLE PaymentDetail (payment_detail_id INTEGER PRIMARY KEY AUTOINCREMENT, payment_id INTEGER, student_fee_id INTEGER, amount REAL, FOREIGN KEY(payment_id) REFERENCES Payment(payment_id) ON DELETE CASCADE)");

        // 6. Notification
        db.execSQL("CREATE TABLE Notification (notification_id INTEGER PRIMARY KEY AUTOINCREMENT, student_id TEXT, title TEXT, content TEXT, is_read INTEGER DEFAULT 0, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

        seedInitialData(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Thêm dữ liệu mẫu để Hiền test app ngay
        db.execSQL("INSERT INTO Class (class_name, faculty, course_year) VALUES ('CNTT2021', 'Công nghệ thông tin', 2021)");
        db.execSQL("INSERT INTO Student (student_id, full_name, email, password_hash, class_id) VALUES ('21520001', 'Nguyễn Văn An', 'an@student.edu.vn', '123456', 1)");

        // Dữ liệu học phí theo yêu cầu
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('21520001', 'Học phí học kỳ 1', 8500000, '2025-05-30', 'UNPAID')");
        db.execSQL("INSERT INTO StudentFee (student_id, fee_name, amount, deadline, status) VALUES ('21520001', 'Phí ký túc xá HK1', 2000000, '2025-06-15', 'UNPAID')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS StudentFee");
        db.execSQL("DROP TABLE IF EXISTS Student");
        onCreate(db);
    }
}