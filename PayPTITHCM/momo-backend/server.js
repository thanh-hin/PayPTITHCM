const express = require("express")
const cors = require("cors")

const app = express()

app.use(cors())
app.use(express.json())

const orders = {}

app.post("/momo/create-payment", (req, res) => {
    const studentId = req.body.studentId || "21520001"
    const amount = Number(req.body.amount || 0)
    const orderInfo = req.body.orderInfo || "Thanh toan hoc phi PayPTITHCM"

    if (amount <= 0) {
        return res.status(400).json({
            success: false,
            message: "Amount khong hop le"
        })
    }

    const orderId = "PAYPTIT_" + Date.now()
    const requestId = "REQ_" + Date.now()
    const transId = "MOMO_SANDBOX_" + Date.now()

    orders[orderId] = {
        orderId,
        requestId,
        transId,
        studentId,
        amount,
        orderInfo,
        status: "PENDING",
        resultCode: null,
        message: "Dang cho thanh toan",
        createdAt: new Date().toISOString()
    }

    const payUrl = "http://localhost:3000/momo/checkout?orderId=" + orderId

    res.json({
        success: true,
        partnerCode: "MOMO_SANDBOX",
        orderId,
        requestId,
        amount,
        orderInfo,
        payUrl,
        deeplink: payUrl,
        qrCodeUrl: payUrl,
        resultCode: 0,
        message: "Tao giao dich sandbox thanh cong"
    })
})

app.get("/momo/checkout", (req, res) => {
    const orderId = req.query.orderId
    const order = orders[orderId]

    if (!order) {
        return res.send("<h2>Khong tim thay giao dich</h2>")
    }

    res.send(`
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>MoMo Sandbox PayPTITHCM</title>
            <style>
                body {
                    margin: 0;
                    font-family: Arial, sans-serif;
                    background: #a50064;
                    color: #222;
                }
                .box {
                    max-width: 420px;
                    margin: 60px auto;
                    background: white;
                    border-radius: 18px;
                    padding: 24px;
                    box-shadow: 0 8px 22px rgba(0,0,0,0.2);
                }
                .logo {
                    text-align: center;
                    color: #a50064;
                    font-size: 36px;
                    font-weight: bold;
                    margin-bottom: 4px;
                }
                .sub {
                    text-align: center;
                    color: #777;
                    margin-bottom: 24px;
                }
                .row {
                    margin: 12px 0;
                }
                .label {
                    color: #777;
                    font-size: 14px;
                }
                .value {
                    font-size: 18px;
                    font-weight: bold;
                    margin-top: 4px;
                }
                .amount {
                    color: #a50064;
                    font-size: 28px;
                    font-weight: bold;
                }
                button {
                    width: 100%;
                    padding: 14px;
                    margin-top: 10px;
                    border: none;
                    border-radius: 8px;
                    font-size: 16px;
                    cursor: pointer;
                }
                .success {
                    background: #a50064;
                    color: white;
                }
                .failed {
                    background: #eeeeee;
                    color: #222;
                }
                .cancel {
                    background: white;
                    color: #a50064;
                    border: 1px solid #a50064;
                }
            </style>
        </head>
        <body>
            <div class="box">
                <div class="logo">MoMo</div>
                <div class="sub">Sandbox giả lập</div>

                <div class="row">
                    <div class="label">Người nhận</div>
                    <div class="value">PayPTITHCM</div>
                </div>

                <div class="row">
                    <div class="label">Mã đơn hàng</div>
                    <div class="value">${order.orderId}</div>
                </div>

                <div class="row">
                    <div class="label">Nội dung</div>
                    <div class="value">${order.orderInfo}</div>
                </div>

                <div class="row">
                    <div class="label">Số tiền</div>
                    <div class="amount">${order.amount.toLocaleString("vi-VN")} VNĐ</div>
                </div>

                <form method="POST" action="/momo/sandbox-result">
                    <input type="hidden" name="orderId" value="${order.orderId}">
                    <input type="hidden" name="status" value="SUCCESS">
                    <button class="success" type="submit">Xác nhận thanh toán thành công</button>
                </form>

                <form method="POST" action="/momo/sandbox-result">
                    <input type="hidden" name="orderId" value="${order.orderId}">
                    <input type="hidden" name="status" value="FAILED">
                    <button class="failed" type="submit">Giả lập thanh toán thất bại</button>
                </form>

                <form method="POST" action="/momo/sandbox-result">
                    <input type="hidden" name="orderId" value="${order.orderId}">
                    <input type="hidden" name="status" value="CANCELED">
                    <button class="cancel" type="submit">Hủy thanh toán</button>
                </form>
            </div>
        </body>
        </html>
    `)
})

app.post("/momo/sandbox-result", express.urlencoded({ extended: true }), (req, res) => {
    const orderId = req.body.orderId
    const status = req.body.status
    const order = orders[orderId]

    if (!order) {
        return res.send("<h2>Khong tim thay giao dich</h2>")
    }

    if (status === "SUCCESS") {
        order.status = "PAID"
        order.resultCode = 0
        order.message = "Thanh toan thanh cong"
    } else if (status === "FAILED") {
        order.status = "FAILED"
        order.resultCode = 1006
        order.message = "Thanh toan that bai"
    } else {
        order.status = "CANCELED"
        order.resultCode = 1001
        order.message = "Nguoi dung huy thanh toan"
    }

    order.paidAt = new Date().toISOString()

    res.send(`
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>Payment Result</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background: #f5f5f5;
                    padding: 40px;
                    text-align: center;
                }
                .box {
                    background: white;
                    max-width: 420px;
                    margin: auto;
                    padding: 24px;
                    border-radius: 16px;
                }
                h2 {
                    color: #a50064;
                }
            </style>
        </head>
        <body>
            <div class="box">
                <h2>${order.message}</h2>
                <p>Mã đơn hàng: <b>${order.orderId}</b></p>
                <p>Mã giao dịch: <b>${order.transId}</b></p>
                <p>Bạn có thể quay lại ứng dụng PayPTITHCM.</p>
            </div>
        </body>
        </html>
    `)
})

app.get("/momo/status/:orderId", (req, res) => {
    const orderId = req.params.orderId
    const order = orders[orderId]

    if (!order) {
        return res.status(404).json({
            success: false,
            message: "Khong tim thay don hang"
        })
    }

    res.json({
        success: true,
        order
    })
})

app.listen(3000, () => {
    console.log("MoMo sandbox backend running at http://localhost:3000")
})