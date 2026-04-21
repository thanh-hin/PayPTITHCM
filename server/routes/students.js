const express = require('express');
const router = express.Router();
const db = require('../config/db');
const auth = require('../middleware/auth');

// GET /api/students/:id
router.get('/:id', auth, (req, res) => {
  const student = db.prepare(`
    SELECT s.student_id, s.full_name, s.email, s.phone,
           c.class_name, c.faculty
    FROM Student s
    LEFT JOIN Class c ON s.class_id = c.class_id
    WHERE s.student_id = ?
  `).get(req.params.id);

  if (!student) return res.status(404).json({ success: false, message: 'Không tìm thấy sinh viên' });

  res.json({
    success: true,
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

module.exports = router;
