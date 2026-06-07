const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const db = require('../config/db');

const JWT_SECRET = 'payptithcm_secret_2025';
const JWT_EXPIRES = '7d';

// OTP store: { email: { otp, expiresAt } }
const otpStore = new Map();

function generateOTP() {
  return String(Math.floor(100000 + Math.random() * 900000));
}

// POST /api/auth/send-otp
// Body: { identifier } — có thể là MSSV hoặc email
router.post('/send-otp', (req, res) => {
  const { identifier } = req.body;
  if (!identifier) return res.status(400).json({ success: false, message: 'Thiếu thông tin' });

  // Tìm student theo MSSV hoặc email
  let student;
  if (identifier.includes('@')) {
    student = db.prepare('SELECT * FROM Student WHERE email = ?').get(identifier);
  } else {
    student = db.prepare('SELECT * FROM Student WHERE student_id = ?').get(identifier);
  }

  if (!student) {
    return res.status(404).json({ success: false, message: 'Không tìm thấy tài khoản' });
  }

  const otp = generateOTP();
  const expiresAt = Date.now() + 5 * 60 * 1000; // 5 phút
  otpStore.set(student.email, { otp, expiresAt });

  // Mock email: log ra console thay vì gửi thật
  console.log(`\n📧 [MOCK EMAIL] Gửi OTP đến: ${student.email}`);
  console.log(`   OTP: ${otp}  (hết hạn sau 5 phút)\n`);

  res.json({
    success: true,
    message: `Đã gửi OTP đến email ${maskEmail(student.email)}`,
    // Chỉ trả email đã mask để hiện toast
    maskedEmail: maskEmail(student.email)
  });
});

// POST /api/auth/verify-otp
// Body: { identifier, otp }
router.post('/verify-otp', (req, res) => {
  const { identifier, otp } = req.body;
  if (!identifier || !otp) return res.status(400).json({ success: false, message: 'Thiếu thông tin' });

  let student;
  if (identifier.includes('@')) {
    student = db.prepare('SELECT * FROM Student WHERE email = ?').get(identifier);
  } else {
    student = db.prepare('SELECT * FROM Student WHERE student_id = ?').get(identifier);
  }

  if (!student) return res.status(404).json({ success: false, message: 'Không tìm thấy tài khoản' });

  const stored = otpStore.get(student.email);
  if (!stored) return res.status(400).json({ success: false, message: 'Chưa gửi OTP hoặc OTP đã hết hạn' });
  if (Date.now() > stored.expiresAt) {
    otpStore.delete(student.email);
    return res.status(400).json({ success: false, message: 'OTP đã hết hạn, vui lòng gửi lại' });
  }
  if (stored.otp !== otp.trim()) {
    return res.status(400).json({ success: false, message: 'OTP không đúng' });
  }

  otpStore.delete(student.email);
  res.json({ success: true, message: 'OTP hợp lệ' });
});

// POST /api/auth/login
// Body: { identifier, password, otp }
router.post('/login', (req, res) => {
  const { identifier, password, otp } = req.body;
  if (!identifier || !password || !otp) {
    return res.status(400).json({ success: false, message: 'Thiếu thông tin đăng nhập' });
  }

  // Tìm student
  let student;
  if (identifier.includes('@')) {
    student = db.prepare(`
      SELECT s.*, c.class_name, c.faculty
      FROM Student s LEFT JOIN Class c ON s.class_id = c.class_id
      WHERE s.email = ?
    `).get(identifier);
  } else {
    student = db.prepare(`
      SELECT s.*, c.class_name, c.faculty
      FROM Student s LEFT JOIN Class c ON s.class_id = c.class_id
      WHERE s.student_id = ?
    `).get(identifier);
  }

  if (!student) {
    return res.status(401).json({ success: false, message: 'MSSV/Email hoặc mật khẩu không đúng' });
  }

  // Kiểm tra mật khẩu (bcrypt)
  const passwordMatch = bcrypt.compareSync(password, student.password);
  if (!passwordMatch) {
    return res.status(401).json({ success: false, message: 'MSSV/Email hoặc mật khẩu không đúng' });
  }

  // Kiểm tra OTP
  const stored = otpStore.get(student.email);
  if (!stored) {
    return res.status(400).json({ success: false, message: 'OTP chưa được gửi hoặc đã hết hạn' });
  }
  if (Date.now() > stored.expiresAt) {
    otpStore.delete(student.email);
    return res.status(400).json({ success: false, message: 'OTP đã hết hạn' });
  }
  if (stored.otp !== otp.trim()) {
    return res.status(400).json({ success: false, message: 'OTP không đúng' });
  }
  otpStore.delete(student.email);

  // Tạo JWT token
  const token = jwt.sign(
    { studentId: student.student_id, email: student.email },
    JWT_SECRET,
    { expiresIn: JWT_EXPIRES }
  );

  res.json({
    success: true,
    message: 'Đăng nhập thành công',
    token,
    student: {
      studentId: student.student_id,
      fullName: student.full_name,
      email: student.email,
      phone: student.phone || '',
      className: student.class_name || '',
      faculty: student.faculty || ''
    }
  });
});

function maskEmail(email) {
  if (!email || !email.includes('@')) return email;
  const [local, domain] = email.split('@');
  const masked = local.length <= 3
    ? local[0] + '***'
    : local.substring(0, 2) + '***' + local.slice(-1);
  return masked + '@' + domain;
}

module.exports = router;
