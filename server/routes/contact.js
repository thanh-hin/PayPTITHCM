const express = require('express');
const router = express.Router();

// GET /api/contact
router.get('/', (req, res) => {
  res.json({
    success: true,
    contact: {
      phone: '0901234567',
      phoneDisplay: '090 123 4567',
      email: 'hotro@ptithcm.edu.vn',
      address: '122 Hoàng Diệu 2, Thủ Đức, TP. Hồ Chí Minh',
      hours: 'Thứ 2 - Thứ 6: 07:30 - 17:00',
      fax: '(028) 3897 0601',
      website: 'https://www.ptithcm.edu.vn'
    }
  });
});

module.exports = router;
