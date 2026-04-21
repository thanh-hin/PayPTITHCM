const Database = require('better-sqlite3');
const bcrypt = require('bcryptjs');
const path = require('path');

const DB_PATH = path.join(__dirname, '..', 'payptithcm.db');
const db = new Database(DB_PATH);

// Bật foreign keys
db.pragma('journal_mode = WAL');
db.pragma('foreign_keys = ON');

function init() {
  db.exec(`
    CREATE TABLE IF NOT EXISTS Class (
      class_id   INTEGER PRIMARY KEY AUTOINCREMENT,
      class_name TEXT NOT NULL UNIQUE,
      faculty    TEXT,
      course_year INTEGER
    );

    CREATE TABLE IF NOT EXISTS Student (
      student_id TEXT PRIMARY KEY,
      full_name  TEXT NOT NULL,
      email      TEXT NOT NULL UNIQUE,
      password   TEXT NOT NULL,
      phone      TEXT,
      class_id   INTEGER,
      FOREIGN KEY(class_id) REFERENCES Class(class_id)
    );

    CREATE TABLE IF NOT EXISTS StudentFee (
      student_fee_id INTEGER PRIMARY KEY AUTOINCREMENT,
      student_id     TEXT NOT NULL,
      fee_name       TEXT NOT NULL,
      amount         REAL NOT NULL,
      deadline       TEXT NOT NULL,
      status         TEXT DEFAULT 'UNPAID',
      paid_date      TEXT,
      FOREIGN KEY(student_id) REFERENCES Student(student_id)
    );

    CREATE TABLE IF NOT EXISTS Payment (
      payment_id     INTEGER PRIMARY KEY AUTOINCREMENT,
      student_id     TEXT NOT NULL,
      fee_name       TEXT NOT NULL,
      amount         REAL NOT NULL,
      method         TEXT,
      status         TEXT DEFAULT 'SUCCESS',
      transaction_id TEXT UNIQUE,
      payment_date   TEXT NOT NULL,
      FOREIGN KEY(student_id) REFERENCES Student(student_id)
    );
  `);

  // Seed nếu chưa có dữ liệu
  const count = db.prepare('SELECT COUNT(*) as c FROM Student').get();
  if (count.c === 0) {
    seedData();
  }
}

function seedData() {
  console.log('🌱 Seeding dữ liệu demo...');

  // Classes
  const insertClass = db.prepare(
    'INSERT OR IGNORE INTO Class (class_name, faculty, course_year) VALUES (?, ?, ?)'
  );
  insertClass.run('D21CQCN01-N', 'Công nghệ thông tin', 2021);
  insertClass.run('D22CQDT01-N', 'Điện tử viễn thông', 2022);
  insertClass.run('D23CQVT01-N', 'Viễn thông', 2023);

  // Students — mật khẩu bcrypt (password = MSSV)
  const students = [
    { id: '21520001', name: 'Nguyễn Văn An',   email: 'an@student.ptithcm.edu.vn',   phone: '0901234567', class_id: 1 },
    { id: '21520002', name: 'Trần Thị Bình',   email: 'binh@student.ptithcm.edu.vn', phone: '0912345678', class_id: 1 },
    { id: '22520001', name: 'Lê Văn Cường',    email: 'cuong@student.ptithcm.edu.vn',phone: '0923456789', class_id: 2 },
    { id: '23520001', name: 'Phạm Thị Dung',   email: 'dung@student.ptithcm.edu.vn', phone: '0934567890', class_id: 3 },
  ];

  const insertStudent = db.prepare(
    'INSERT OR IGNORE INTO Student (student_id, full_name, email, password, phone, class_id) VALUES (?, ?, ?, ?, ?, ?)'
  );
  for (const s of students) {
    const hashed = bcrypt.hashSync(s.id, 10); // password = MSSV
    insertStudent.run(s.id, s.name, s.email, hashed, s.phone, s.class_id);
  }

  // Học phí
  const insertFee = db.prepare(
    'INSERT OR IGNORE INTO StudentFee (student_id, fee_name, amount, deadline, status, paid_date) VALUES (?, ?, ?, ?, ?, ?)'
  );
  const fees = [
    ['21520001', 'Học phí HK2/2025',      9200000, '2025-07-31', 'UNPAID',  null],
    ['21520001', 'Phí ký túc xá HK2/2025',1600000, '2025-07-15', 'UNPAID',  null],
    ['21520001', 'Bảo hiểm y tế 2025',     702000, '2025-03-31', 'OVERDUE', null],
    ['21520001', 'Phí thẻ sinh viên',       50000, '2025-09-30', 'UNPAID',  null],
    ['21520001', 'Học phí HK1/2025',      8800000, '2024-12-31', 'PAID',   '2024-12-15'],
    ['21520001', 'Học phí HK2/2024',      8500000, '2024-06-30', 'PAID',   '2024-06-10'],
    ['21520002', 'Học phí HK2/2025',      9200000, '2025-07-31', 'UNPAID',  null],
    ['21520002', 'Bảo hiểm y tế 2025',     702000, '2025-03-31', 'OVERDUE', null],
    ['21520002', 'Học phí HK1/2025',      8800000, '2024-12-31', 'PAID',   '2024-12-20'],
    ['22520001', 'Học phí HK2/2025',      9000000, '2025-07-31', 'UNPAID',  null],
    ['22520001', 'Học phí HK1/2025',      8800000, '2024-12-31', 'PAID',   '2024-12-05'],
    ['23520001', 'Học phí HK1/2025',      9500000, '2025-01-31', 'OVERDUE', null],
    ['23520001', 'Phí nhập học',            500000, '2024-09-30', 'OVERDUE', null],
    ['23520001', 'Bảo hiểm y tế 2025',     702000, '2025-03-31', 'UNPAID',  null],
  ];
  for (const f of fees) insertFee.run(...f);

  // Lịch sử thanh toán mẫu
  const insertPayment = db.prepare(
    'INSERT OR IGNORE INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date) VALUES (?, ?, ?, ?, ?, ?, ?)'
  );
  const payments = [
    ['21520001', 'Học phí HK1/2025', 8800000, 'Chuyển khoản ngân hàng', 'SUCCESS', 'TXN20241215001', '2024-12-15 09:30:00'],
    ['21520001', 'Học phí HK2/2024', 8500000, 'Ví điện tử (MoMo)',      'SUCCESS', 'TXN20240610001', '2024-06-10 14:20:00'],
    ['21520002', 'Học phí HK1/2025', 8800000, 'Chuyển khoản ngân hàng', 'SUCCESS', 'TXN20241220001', '2024-12-20 10:00:00'],
    ['22520001', 'Học phí HK1/2025', 8800000, 'Tiền mặt',               'SUCCESS', 'TXN20241205001', '2024-12-05 11:00:00'],
  ];
  for (const p of payments) insertPayment.run(...p);

  console.log('✅ Seed xong! Tài khoản demo: MSSV=21520001, Pass=21520001');
}

init();
module.exports = db;
