require('dotenv').config();

const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3001;

app.use(cors());
app.use(express.json());

app.use('/api/payments', require('./routes/payments'));

app.get('/', (req, res) => {
  res.json({
    success: true,
    message: 'VNPay backend PayPTITHCM đang chạy',
    port: PORT
  });
});

app.listen(PORT, () => {
  console.log(`VNPay backend chạy tại: http://localhost:${PORT}`);
  console.log(`Android emulator gọi qua: http://10.0.2.2:${PORT}`);
});