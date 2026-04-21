const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

// Routes
app.use('/api/auth', require('./routes/auth'));
app.use('/api/students', require('./routes/students'));
app.use('/api/fees', require('./routes/fees'));
app.use('/api/payments', require('./routes/payments'));
app.use('/api/contact', require('./routes/contact'));

app.get('/', (req, res) => {
  res.json({ message: 'PAY PTITHCM Server đang chạy!', version: '1.0.0' });
});

app.listen(PORT, () => {
  console.log(`\n🚀 PAY PTITHCM Server chạy tại: http://localhost:${PORT}`);
  console.log(`📱 Android emulator kết nối qua: http://10.0.2.2:${PORT}\n`);
});
