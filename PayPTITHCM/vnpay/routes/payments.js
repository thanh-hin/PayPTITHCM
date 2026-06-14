const express = require('express');
const router = express.Router();

const { VNPay, ignoreLogger } = require('vnpay');

const paymentStore = new Map();

const vnpay = new VNPay({
  tmnCode: process.env.VNP_TMNCODE,
  secureSecret: process.env.VNP_SECRET,
  vnpayHost: process.env.VNP_HOST || 'https://sandbox.vnpayment.vn',
  testMode: true,
  hashAlgorithm: 'SHA512',
  enableLog: true,
  loggerFn: ignoreLogger,
});

/**
 * Android gọi API này để tạo link thanh toán VNPay.
 * Body:
 * {
 *   "studentId": "21520001",
 *   "feeIds": [1, 2],
 *   "amount": 100000
 * }
 */
router.post('/create-vnpay', (req, res) => {
  try {
    const { studentId, feeIds, amount } = req.body;

    if (!studentId || !amount) {
      return res.status(400).json({
        success: false,
        message: 'Thiếu studentId hoặc amount'
      });
    }

    const txnRef = 'VNPAY_' + Date.now();

    paymentStore.set(txnRef, {
      studentId,
      feeIds: feeIds || [],
      amount: Math.round(Number(amount)),
      status: 'PENDING',
      createdAt: new Date().toISOString()
    });

    const paymentUrl = vnpay.buildPaymentUrl({
      vnp_Amount: Math.round(Number(amount)),
      vnp_IpAddr: req.ip || '127.0.0.1',
      vnp_ReturnUrl: process.env.APP_RETURN_URL,
      vnp_TxnRef: txnRef,
      vnp_OrderInfo: `Thanh toan hoc phi PayPTITHCM - ${studentId}`,
    });

    return res.json({
      success: true,
      paymentUrl,
      transactionId: txnRef,
      amount: Math.round(Number(amount))
    });
  } catch (err) {
    console.error('Create VNPay error:', err);

    return res.status(500).json({
      success: false,
      message: 'Lỗi tạo URL thanh toán VNPay',
      error: err.message
    });
  }
});

/**
 * VNPay redirect về đây sau khi người dùng thanh toán.
 */
router.get('/vnpay-return', (req, res) => {
  try {
    const verify = vnpay.verifyReturnUrl(req.query);

    const txnRef = req.query.vnp_TxnRef;
    const responseCode = req.query.vnp_ResponseCode;
    const transactionStatus = req.query.vnp_TransactionStatus;

    const isPaid =
      verify.isSuccess &&
      responseCode === '00' &&
      transactionStatus === '00';

    const payment = paymentStore.get(txnRef);

    if (payment) {
      payment.status = isPaid ? 'SUCCESS' : 'FAILED';
      payment.responseCode = responseCode;
      payment.updatedAt = new Date().toISOString();

      paymentStore.set(txnRef, payment);
    }

    if (isPaid) {
      return res.send(`
        <h2>Thanh toán VNPay thành công</h2>
        <p>Mã giao dịch: ${txnRef}</p>
        <p>Bạn quay lại app PayPTITHCM để cập nhật trạng thái học phí.</p>
      `);
    }

    return res.send(`
      <h2>Thanh toán VNPay thất bại</h2>
      <p>Mã phản hồi: ${responseCode || 'N/A'}</p>
      <p>Bạn có thể quay lại app PayPTITHCM để thử lại.</p>
    `);
  } catch (err) {
    console.error('VNPay return error:', err);

    return res.send(`
      <h2>Lỗi xác thực VNPay</h2>
      <p>${err.message}</p>
    `);
  }
});

/**
 * Android gọi API này sau khi quay lại app để kiểm tra trạng thái.
 */
router.get('/vnpay-status/:txnRef', (req, res) => {
  const txnRef = req.params.txnRef;
  const payment = paymentStore.get(txnRef);

  if (!payment) {
    return res.status(404).json({
      success: false,
      message: 'Không tìm thấy giao dịch'
    });
  }

  return res.json({
    success: true,
    transactionId: txnRef,
    status: payment.status,
    amount: payment.amount,
    studentId: payment.studentId,
    feeIds: payment.feeIds
  });
});

module.exports = router;