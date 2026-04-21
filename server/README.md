# PAY PTITHCM - Backend Server

## Yêu cầu
- **Node.js** >= 16.x (tải tại https://nodejs.org/)
- npm (đi kèm với Node.js)

## Cài đặt & Chạy

```bash
cd server
npm install
node index.js
```

Server sẽ chạy tại: `http://localhost:3000`

Android Emulator kết nối qua: `http://10.0.2.2:3000`

## OTP (Mock Email)
OTP sẽ được **log ra console terminal** của server thay vì gửi email thật.
Nhìn vào terminal để lấy OTP khi test.

```
📧 [MOCK EMAIL] Gửi OTP đến: an@student.ptithcm.edu.vn
   OTP: 123456  (hết hạn sau 5 phút)
```

## Tài khoản demo
| MSSV     | Mật khẩu | Email                              |
|----------|----------|------------------------------------|
| 21520001 | 21520001 | an@student.ptithcm.edu.vn          |
| 21520002 | 21520002 | binh@student.ptithcm.edu.vn        |
| 22520001 | 22520001 | cuong@student.ptithcm.edu.vn       |
| 23520001 | 23520001 | dung@student.ptithcm.edu.vn        |

## Endpoints
| Method | URL | Mô tả |
|--------|-----|-------|
| POST | /api/auth/send-otp | Gửi OTP |
| POST | /api/auth/login | Đăng nhập |
| GET | /api/students/:id | Thông tin sinh viên |
| GET | /api/fees/:studentId | Danh sách học phí |
| GET | /api/payments/:studentId | Lịch sử thanh toán |
| POST | /api/payments | Thanh toán |
| GET | /api/contact | Thông tin liên hệ |

## Offline Mode
Nếu server không chạy, app Android vẫn hoạt động offline với SQLite local.
- Nhập OTP = `000000` để bypass OTP khi offline
- Mật khẩu được hash SHA-256 trong SQLite
