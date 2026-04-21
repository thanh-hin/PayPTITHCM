const express = require('express');
const router = express.Router();
const db = require('../config/db');
const auth = require('../middleware/auth');

// GET /api/payments/:studentId
router.get('/:studentId', auth, (req, res) => {
  const payments = db.prepare(`
    SELECT * FROM Payment
    WHERE student_id = ?
    ORDER BY payment_date DESC
  `).all(req.params.studentId);

  res.json({
    success: true,
    payments: payments.map(p => ({
      paymentId: p.payment_id,
      date: p.payment_date,
      feeName: p.fee_name,
      amount: Math.round(p.amount),
      method: p.method || '',
      status: p.status,
      transactionId: p.transaction_id || ''
    }))
  });
});

// POST /api/payments
// Body: { studentId, feeIds: [1,2,3], method }
router.post('/', auth, (req, res) => {
  const { studentId, feeIds, method } = req.body;
  if (!studentId || !feeIds || !feeIds.length || !method) {
    return res.status(400).json({ success: false, message: 'Thiếu thông tin thanh toán' });
  }

  const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
  const txnId = 'TXN' + Date.now();
  const cleanMethod = method.replace(/[^a-zA-Z0-9\s/(),.-]/g, '').trim();

  // Lấy danh sách fees cần thanh toán
  const placeholders = feeIds.map(() => '?').join(',');
  const fees = db.prepare(`
    SELECT * FROM StudentFee
    WHERE student_fee_id IN (${placeholders})
    AND student_id = ?
    AND status != 'PAID'
  `).all(...feeIds, studentId);

  if (!fees.length) {
    return res.status(400).json({ success: false, message: 'Không tìm thấy khoản phí hợp lệ' });
  }

  // Atomic transaction
  const doPayment = db.transaction(() => {
    const insertPayment = db.prepare(`
      INSERT INTO Payment (student_id, fee_name, amount, method, status, transaction_id, payment_date)
      VALUES (?, ?, ?, ?, 'SUCCESS', ?, ?)
    `);
    const updateFee = db.prepare(`
      UPDATE StudentFee SET status = 'PAID', paid_date = ? WHERE student_fee_id = ?
    `);

    for (const fee of fees) {
      insertPayment.run(studentId, fee.fee_name, fee.amount, cleanMethod, txnId + '_' + fee.student_fee_id, now);
      updateFee.run(now.substring(0, 10), fee.student_fee_id);
    }
    return txnId;
  });

  try {
    const resultTxn = doPayment();
    res.json({ success: true, transactionId: resultTxn, message: 'Thanh toán thành công' });
  } catch (err) {
    console.error('Payment error:', err);
    res.status(500).json({ success: false, message: 'Lỗi xử lý thanh toán' });
  }
});

module.exports = router;
