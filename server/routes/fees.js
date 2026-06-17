const express = require('express');
const router = express.Router();
const db = require('../config/db');
const auth = require('../middleware/auth');

// GET /api/fees/:studentId
router.get('/:studentId', auth, (req, res) => {
  const fees = db.prepare(`
    SELECT * FROM StudentFee
    WHERE student_id = ?
    ORDER BY
      CASE status WHEN 'OVERDUE' THEN 1 WHEN 'UNPAID' THEN 2 ELSE 3 END,
      deadline ASC
  `).all(req.params.studentId);

  res.json({
    success: true,
    fees: fees.map(f => ({
      id: f.student_fee_id,
      name: f.fee_name,
      amount: Math.round(f.amount),
      deadline: f.deadline,
      status: f.status,
      paidDate: f.paid_date || null
    }))
  });
});

module.exports = router;
