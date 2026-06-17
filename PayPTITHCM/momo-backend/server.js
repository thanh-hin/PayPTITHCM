const express = require("express")
const axios = require("axios")
const crypto = require("crypto")
const cors = require("cors")
require("dotenv").config()

const app = express()

app.use(cors())
app.use(express.json())

const orders = {}

app.post("/momo/create-payment", async (req, res) => {
  try {
    const amount = String(req.body.amount || 10000)
    const orderInfo = req.body.orderInfo || "Thanh toan PayPTITHCM"
    const userEmail = req.body.email || ""
    const orderId = "PAYPTIT_" + Date.now()
    const requestId = orderId
    const extraData = Buffer.from(JSON.stringify({ userEmail })).toString("base64")
    const requestType = "captureWallet"

    const partnerCode = process.env.MOMO_PARTNER_CODE
    const accessKey = process.env.MOMO_ACCESS_KEY
    const secretKey = process.env.MOMO_SECRET_KEY
    const redirectUrl = process.env.REDIRECT_URL
    const ipnUrl = process.env.IPN_URL

    const rawSignature =
      "accessKey=" + accessKey +
      "&amount=" + amount +
      "&extraData=" + extraData +
      "&ipnUrl=" + ipnUrl +
      "&orderId=" + orderId +
      "&orderInfo=" + orderInfo +
      "&partnerCode=" + partnerCode +
      "&redirectUrl=" + redirectUrl +
      "&requestId=" + requestId +
      "&requestType=" + requestType

    const signature = crypto
      .createHmac("sha256", secretKey)
      .update(rawSignature)
      .digest("hex")

    const requestBody = {
      partnerCode,
      partnerName: "PayPTITHCM",
      storeId: "PayPTITHCMStore",
      requestId,
      amount,
      orderId,
      orderInfo,
      redirectUrl,
      ipnUrl,
      lang: "vi",
      requestType,
      autoCapture: true,
      extraData,
      signature
    }

    const momoResponse = await axios.post(process.env.MOMO_ENDPOINT, requestBody, {
      headers: {
        "Content-Type": "application/json"
      },
      timeout: 30000
    })

    orders[orderId] = {
      orderId,
      amount,
      userEmail,
      status: "PENDING",
      momoResponse: momoResponse.data
    }

    res.json({
      success: true,
      orderId,
      payUrl: momoResponse.data.payUrl,
      deeplink: momoResponse.data.deeplink,
      qrCodeUrl: momoResponse.data.qrCodeUrl,
      resultCode: momoResponse.data.resultCode,
      message: momoResponse.data.message
    })
  } catch (error) {
    res.status(500).json({
      success: false,
      message: "Create MoMo payment failed",
      error: error.response?.data || error.message
    })
  }
})

app.post("/momo/ipn", (req, res) => {
  const data = req.body
  const orderId = data.orderId

  if (orders[orderId]) {
    orders[orderId].status = data.resultCode === 0 ? "PAID" : "FAILED"
    orders[orderId].ipn = data
  }

  res.status(204).send()
})

app.get("/momo/return", (req, res) => {
  res.send(`
    <h2>MoMo Payment Return</h2>
    <p>OrderId: ${req.query.orderId || ""}</p>
    <p>ResultCode: ${req.query.resultCode || ""}</p>
    <p>Message: ${req.query.message || ""}</p>
  `)
})

app.get("/momo/order/:orderId", (req, res) => {
  const order = orders[req.params.orderId]

  if (!order) {
    return res.status(404).json({
      success: false,
      message: "Order not found"
    })
  }

  res.json({
    success: true,
    order
  })
})

app.listen(process.env.PORT, () => {
  console.log("MoMo backend running on port " + process.env.PORT)
})