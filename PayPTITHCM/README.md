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
| Navigation | ViewPager2 + BottomNavigationView (No-swipe) |
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

---

## 👤 Tài khoản Test

| MSSV | Mật khẩu | Tên | Trạng thái |
|---|---|---|---|
| `21520001` | `21520001` | Nguyễn Văn An | Có 3 khoản UNPAID + 1 OVERDUE |
| `21520002` | `21520002` | Trần Thị Bình | Có 1 khoản UNPAID + 1 OVERDUE |

> **Lưu ý OTP**: Sau khi nhập MSSV + mật khẩu, mã OTP hiển thị ngay trên màn hình. Chép lại và nhập vào ô OTP.

---

## 📋 Luồng Demo cho Giảng Viên

### Demo 1 – Đăng nhập và xem tổng quan
1. Nhập MSSV: `21520001`, Mật khẩu: `21520001`
2. Nhập OTP hiển thị trên màn hình → Đăng nhập
3. **Home screen** hiển thị tổng quan. Thanh menu dưới chỉ hiện 3 tab chính: **Học phí, Trang chủ, Cá nhân**.

### Demo 2 – Xem và thanh toán học phí
1. Nhấn tab **Học phí** (mục đầu tiên trên menu hoặc nhấn "Khoản phí" trên Home)
2. Chọn khoản phí và thực hiện thanh toán.
3. *Lưu ý: Chuyển tab diễn ra tức thì, không có hiệu ứng kéo lướt và không thể vuốt tay giữa các trang.*

### Demo 3 – Xem lịch sử giao dịch & Hỗ trợ
1. Từ trang **Trang chủ**, nhấn vào biểu tượng **Lịch sử** trong Grid tiện ích.
2. Ứng dụng sẽ chuyển sang màn hình Lịch sử (đây là tab ẩn khỏi thanh điều hướng chính để tối ưu không gian).
3. Làm tương tự với mục **Hỗ trợ**.

---

## 🏗 Kiến trúc Hệ thống

```
┌─────────────────────────────────────────────────────┐
│                   UI Layer (Activities + Fragments)  │
│                                                     │
│  LoginActivity ──→ MainActivity (ViewPager2)        │
│                        ├── FeeListFragment (Tab 0)  │
│                        ├── HomeFragment (Tab 1)     │
│                        ├── ProfileFragment (Tab 2)  │
│                        ├── HistoryFragment (Hidden) │
│                        └── SupportFragment (Hidden) │
└─────────────────────────────────────────────────────┘
```

---

## ✅ Checklist Nghiệm Thu

### Luồng chính
- [x] Đăng nhập OTP hiển thị tại chỗ
- [x] Menu Bottom rút gọn (3 mục: Học phí, Home, Cá nhân)
- [x] Grid tiện ích tại Home đầy đủ 4 chức năng (Khoản phí, Thông báo, Lịch sử, Hỗ trợ)
- [x] **Vô hiệu hóa Swipe**: Không thể dùng tay vuốt giữa các Fragment
- [x] **No-Smooth-Scroll**: Chuyển trang lập tức, không có hiệu ứng kéo lướt
- [x] Click Grid item tại Home chuyển đúng Fragment tương ứng
- [x] Lưu DB atomic (transaction cho payment)

### UI/UX
- [x] Fix lỗi click Grid trang Home (loại bỏ clickable/focusable ở item con)
- [x] Material Design 3, gam màu PTIT đỏ
- [x] Số tiền định dạng "%,d đ"
- [x] Trạng thái: Đã đóng / Chưa đóng / ⚠ Quá hạn

---

*Đồ án môn học – Lập trình Android – PTITHCM*
