# 📱 PAY PTITHCM – Ứng dụng Đóng Học Phí Sinh Viên

> **Đồ án môn học** – Ứng dụng quản lý và thanh toán học phí dành cho sinh viên Học viện Công nghệ Bưu chính Viễn thông TP.HCM (PTITHCM)

---

## 🛠 Tech Stack

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java (Android Native) |
| UI Framework | Material Design 3 + ConstraintLayout |
| Database | SQLite (local, offline-first) |
| Session | SharedPreferences |
| Navigation | Fragment + BottomNavigationView |
| Build | Gradle (Kotlin DSL) |
| Min SDK | API 27 (Android 8.1) |
| Target SDK | API 36 (Android 15) |

---

## 🚀 Hướng dẫn Cài đặt & Chạy

### Yêu cầu
- Android Studio **Hedgehog** (2023.1.1) trở lên
- JDK 11
- Android SDK API 27–36

### Bước 1 – Clone project
```bash
git clone <repo-url>
cd PayPTITHCM/PayPTITHCM
```

### Bước 2 – Mở trong Android Studio
- File → Open → chọn thư mục `PayPTITHCM/PayPTITHCM`
- Chờ Gradle sync hoàn tất

### Bước 3 – Chạy ứng dụng
- Chọn thiết bị (Emulator hoặc Device thực API ≥ 27)
- Nhấn **Run** (Shift+F10)

> ⚠️ **Lần đầu chạy** sẽ tự động khởi tạo database SQLite với dữ liệu mẫu.
> Nếu muốn reset dữ liệu, gỡ app và cài lại (hoặc xóa data app trên thiết bị).

---

## 👤 Tài khoản Test

| MSSV | Mật khẩu | Tên | Trạng thái |
|---|---|---|---|
| `21520001` | `21520001` | Nguyễn Văn An | Có 3 khoản UNPAID + 1 OVERDUE |
| `21520002` | `21520002` | Trần Thị Bình | Có 1 khoản UNPAID + 1 OVERDUE |
| `22520001` | `22520001` | Lê Văn Cường | Có 1 khoản UNPAID |
| `23520001` | `23520001` | Phạm Thị Dung | Tân sinh viên, 2 OVERDUE + 1 UNPAID |

> **Lưu ý OTP**: Sau khi nhập MSSV + mật khẩu, mã OTP hiển thị ngay trên màn hình (bên phải ô nhập OTP). Chép lại và nhập vào ô OTP.

---

## 📋 Luồng Demo cho Giảng Viên

### Demo 1 – Đăng nhập và xem tổng quan
1. Nhập MSSV: `21520001`, Mật khẩu: `21520001`
2. Nhập OTP hiển thị trên màn hình → Đăng nhập
3. **Home screen** hiển thị:
   - Tên sinh viên
   - Tổng số tiền & số khoản chưa đóng
4. Nhấn tab **Cá nhân** → Xem thông tin sinh viên đầy đủ

### Demo 2 – Xem và thanh toán học phí
1. Nhấn tab **Học phí** (hoặc nhấn "Học phí" trên Home)
2. Danh sách khoản phí hiển thị với màu sắc:
   - 🔴 **Chưa đóng** – tick chọn để thanh toán
   - 🟠 **⚠ Quá hạn** – tick chọn để thanh toán
   - 🟢 **Đã đóng** – không thể chọn (mờ)
3. Tick chọn **Bảo hiểm y tế** và **Phí thẻ sinh viên** (2 khoản nhỏ để demo nhanh)
4. Tổng tiền tự cập nhật ở thanh phía trên
5. Nhấn **"Thanh toán X khoản (Y đ)"**
6. Màn hình thanh toán: Xem chi tiết, chọn phương thức → **Xác nhận**
7. Dialog xác nhận → Nhấn **Xác nhận**
8. Hiển thị **✓ Thanh toán thành công!** với mã giao dịch
9. Quay lại → Danh sách tự làm mới (khoản vừa đóng chuyển sang "Đã đóng")

### Demo 3 – Xem lịch sử giao dịch
1. Nhấn tab **Lịch sử**
2. Xem danh sách giao dịch đã thực hiện (sắp xếp mới nhất trước)
3. **Nhấn vào 1 giao dịch** → Xem chi tiết (ngày, khoản phí, số tiền, phương thức, mã GD)

### Demo 4 – Đăng xuất
1. Tab **Cá nhân** → Nhấn **Đăng xuất**
2. Xác nhận → Về màn hình đăng nhập
3. Đăng nhập bằng tài khoản khác (VD: `21520002`) để thấy dữ liệu khác nhau

---

## 🏗 Kiến trúc Hệ thống

```
┌─────────────────────────────────────────────────────┐
│                   UI Layer (Activities + Fragments)  │
│                                                     │
│  LoginActivity ──→ MainActivity                     │
│                        ├── HomeFragment             │
│                        ├── FeeListFragment ──→ PaymentActivity
│                        ├── HistoryFragment           │
│                        └── ProfileFragment           │
├─────────────────────────────────────────────────────┤
│                   Data Layer                         │
│                                                     │
│  DatabaseHelper (SQLite Singleton)                  │
│  ├── authenticateStudent()                          │
│  ├── getStudentFees()                               │
│  ├── insertPayment() [atomic transaction]           │
│  ├── getPaymentHistory()                            │
│  ├── countUnpaidFees()                              │
│  └── getTotalUnpaid()                               │
│                                                     │
│  SharedPrefs (Session)                              │
│  ├── saveUser() / getUser()                         │
│  ├── isLoggedIn()                                   │
│  └── clearUser()                                    │
├─────────────────────────────────────────────────────┤
│                   Database Schema (SQLite)           │
│                                                     │
│  Class ──← Student ──← StudentFee                  │
│                    └──← Payment                     │
└─────────────────────────────────────────────────────┘
```

### Database Tables
| Bảng | Mô tả |
|---|---|
| `Class` | Thông tin lớp học (tên lớp, khoa, khóa) |
| `Student` | Thông tin sinh viên (MSSV, họ tên, email, mật khẩu) |
| `StudentFee` | Khoản phí theo sinh viên (tên, số tiền, hạn, trạng thái) |
| `Payment` | Lịch sử thanh toán (mã GD, phương thức, ngày, trạng thái) |

---

## ✅ Checklist Nghiệm Thu

### Luồng chính
- [x] Đăng nhập với MSSV + mật khẩu + OTP
- [x] Hiển thị thông báo lỗi rõ ràng khi sai thông tin
- [x] Home: Hiển thị tên sinh viên, tổng khoản nợ, số tiền
- [x] Danh sách học phí với màu sắc theo trạng thái (UNPAID/OVERDUE/PAID)
- [x] Multi-select khoản phí, tổng tiền tự cập nhật
- [x] Màn hình thanh toán: xem chi tiết, chọn phương thức
- [x] Confirm dialog trước khi thanh toán
- [x] Lưu DB atomic (transaction an toàn)
- [x] Cập nhật trạng thái sau thanh toán
- [x] Chống double-submit (disable button khi đang xử lý)
- [x] Lịch sử giao dịch (click xem chi tiết)
- [x] Trang cá nhân với thông tin đầy đủ
- [x] Đăng xuất với confirm dialog
- [x] Empty state cho lịch sử trống

### UI/UX
- [x] Material Design 3, gam màu PTIT đỏ
- [x] Số tiền định dạng "%,d đ" (1.234.567 đ)
- [x] Ngày tháng định dạng dd/MM/yyyy HH:mm
- [x] Trạng thái rõ ràng: Đã đóng / Chưa đóng / ⚠ Quá hạn
- [x] Avatar chữ cái đầu tên trong màn cá nhân
- [x] Loading state hợp lý

### Kỹ thuật
- [x] ViewHolder pattern trong adapters
- [x] ActivityResultLauncher (không dùng deprecated API)
- [x] setOnItemSelectedListener (không dùng deprecated)
- [x] SQLite transaction cho payment
- [x] Singleton DatabaseHelper
- [x] Guard null check trong fragments
- [x] Version bump DB để trigger migration

---

## ⚠️ Điểm còn giới hạn (scope đồ án)

| Giới hạn | Lý do |
|---|---|
| Không có backend API | App local-first, dùng SQLite |
| OTP hiển thị trên màn hình | Không có SMS gateway thực |
| Mật khẩu lưu dưới dạng plaintext | Ngoài scope (đề tài không yêu cầu security) |
| Không có tích hợp cổng thanh toán thực | Không có VNPay/MoMo API key |
| Dữ liệu chỉ trên thiết bị | Không có cloud sync |

---

## 🔧 Nếu có thêm thời gian

1. **Backend API** – Node.js/Spring Boot + PostgreSQL để sync dữ liệu đa thiết bị
2. **Mã hóa mật khẩu** – BCrypt/SHA-256
3. **Push notification** – Nhắc nhở khoản phí sắp đến hạn
4. **Xuất hóa đơn PDF** – Lưu biên lai thanh toán
5. **Tích hợp VNPay** – Thanh toán thực tế
6. **Quản lý Admin** – Giao diện cho kế toán thêm/sửa học phí

---

## 📁 Cấu trúc Project

```
app/src/main/
├── java/com/ptithcm/payptithcm/
│   ├── activities/
│   │   ├── LoginActivity.java       # Đăng nhập (MSSV + Mật khẩu + OTP)
│   │   ├── MainActivity.java        # Container chính với Bottom Navigation
│   │   └── PaymentActivity.java     # Xác nhận & thực hiện thanh toán
│   ├── adapters/
│   │   ├── FeeAdapter.java          # Adapter danh sách học phí (ViewHolder)
│   │   ├── HistoryAdapter.java      # Adapter lịch sử giao dịch (ViewHolder)
│   │   └── HomeAdapter.java         # Adapter grid menu trang chủ
│   ├── models/
│   │   ├── Student.java
│   │   ├── FeeItem.java
│   │   ├── HistoryItem.java
│   │   └── HomeItem.java
│   ├── utils/
│   │   ├── DatabaseHelper.java      # SQLite DAO, Singleton, atomic transactions
│   │   └── SharedPrefs.java         # Session management
│   ├── HomeFragment.java
│   ├── FeeListFragment.java
│   ├── HistoryFragment.java
│   └── ProfileFragment.java
└── res/
    ├── layout/                      # 10 XML layouts
    ├── drawable/                    # Icons + backgrounds
    ├── values/
    │   ├── colors.xml               # PTIT brand colors + status colors
    │   ├── strings.xml
    │   └── themes.xml               # Material3 DayNight
    └── menu/
        └── bottom_nav_menu.xml
```

---

*Đồ án môn học – Lập trình Android – PTITHCM*
