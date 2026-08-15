# DWatch — Cửa Hàng Đồng Hồ E-Commerce

**Repository:** [https://github.com/TNL293107/DWatch](https://github.com/TNL293107/DWatch)

Ứng dụng web bán đồng hồ xây bằng **Java (Servlet/JSP)**, **Maven**, **Microsoft SQL Server**. Hỗ trợ đăng ký/đăng nhập, giỏ hàng, đặt hàng (COD / VietQR), quên mật khẩu, tra cứu đơn hàng, so sánh sản phẩm, quản trị sản phẩm.

**Setup Guide (Ver 1.5)** — Apache NetBeans 25 + Microsoft SQL Server Management Studio 22

---

## Công nghệ

- **Backend:** Java 17, Servlet 4.0, JSP, JSTL  
- **Build:** Maven 3.x  
- **Database:** Microsoft SQL Server  
- **Deploy:** WAR → Apache Tomcat 9/10  
- **Email:** Apache Commons Email (Gmail SMTP) — xác nhận đơn hàng, đặt lại mật khẩu  

---

## Cấu trúc project (Maven)

```
DWatch/
├── pom.xml
├── .env.example          ← copy thành .env, điền DB + (tùy chọn) email
├── database.sql          ← script tạo DB và bảng chính
├── database_add_payment_method.sql
├── database_password_reset.sql
├── database_password_hash.sql   ← migrate mật khẩu plaintext sang BCrypt
├── README.md
├── src/test/java/               ← unit test (JUnit 5 + Mockito): chạy bằng `mvn test`
└── src/main/
    ├── java/
    │   ├── dao/
    │   │   ├── AdminDAO.java
    │   │   ├── CategoryDAO.java
    │   │   ├── OrderDAO.java
    │   │   ├── ProductDAO.java
    │   │   ├── UserDAO.java
    │   │   └── WishlistDAO.java
    │   ├── model/
    │   │   ├── CartItem.java
    │   │   ├── Category.java
    │   │   ├── Order.java
    │   │   ├── OrderDetail.java
    │   │   ├── Product.java
    │   │   └── User.java
    │   ├── service/                     ← tầng nghiệp vụ (servlet không còn chứa business logic)
    │   │   ├── AdminAuthService.java
    │   │   ├── AuthService.java         ← đăng nhập/đăng ký/đổi & đặt lại mật khẩu
    │   │   ├── PasswordPolicy.java
    │   │   ├── PasswordResetTicket.java
    │   │   ├── ServiceResult.java
    │   │   └── UserService.java
    │   ├── servlet/
    │   │   ├── AdminLoginServlet.java
    │   │   ├── CartServlet.java
    │   │   ├── CharsetFilter.java
    │   │   ├── CsrfFilter.java          ← kiểm tra CSRF token cho mọi request ghi dữ liệu
    │   │   ├── CompareServlet.java
    │   │   ├── ForgotPasswordServlet.java
    │   │   ├── HomeServlet.java
    │   │   ├── OrderConfirmServlet.java
    │   │   ├── OrderHistoryServlet.java
    │   │   ├── OrderLookupServlet.java
    │   │   ├── ProductDetailServlet.java
    │   │   ├── ProductSetupServlet.java
    │   │   ├── ResetPasswordServlet.java
    │   │   ├── UserServlet.java
    │   │   └── WishlistServlet.java
    │   └── util/
    │       ├── CsrfUtil.java            ← sinh/kiểm tra CSRF token
    │       ├── DBUtil.java
    │       ├── EmailUtil.java
    │       ├── PasswordUtil.java        ← băm & xác thực mật khẩu (BCrypt)
    │       └── VietQRUtil.java          ← tạo mã QR thanh toán (VietQR)
    └── webapp/
        ├── WEB-INF/
        │   └── web.xml                  ← context-param VietQR (số TK, tên NH)
        ├── css/
        │   ├── admin.css
        │   └── style.css
        ├── images/
        ├── pages/
        │   ├── adminLogin.jsp
        │   ├── cart.jsp
        │   ├── compare.jsp
        │   ├── footer.jsp
        │   ├── forgotPassword.jsp
        │   ├── header.jsp
        │   ├── home.jsp
        │   ├── login.jsp
        │   ├── orderConfirm.jsp
        │   ├── orderDetail.jsp
        │   ├── orderHistory.jsp
        │   ├── orderLookup.jsp
        │   ├── orderLookupResult.jsp
        │   ├── productDetail.jsp
        │   ├── productSetup.jsp
        │   ├── profile.jsp
        │   ├── register.jsp
        │   ├── resetPassword.jsp
        │   └── wishlist.jsp
        └── index.jsp
```

---

## Chức năng chính

| Chức năng | Mô tả |
|-----------|--------|
| **Trang chủ** | Danh sách sản phẩm, tìm kiếm, lọc theo danh mục |
| **Chi tiết sản phẩm** | Ảnh, mô tả, thông số, thêm giỏ, yêu thích, **thêm vào so sánh** |
| **Giỏ hàng** | Sửa/xóa số lượng, cập nhật giỏ; **checkout bắt buộc đăng nhập** (hoặc tạo tài khoản) |
| **Thanh toán** | **COD** (thanh toán khi nhận hàng) hoặc **VietQR** (mã QR đúng số tiền, chuẩn VietQR) |
| **Tình trạng đơn** | **Đã thanh toán / Chưa thanh toán** — hiển thị trong email xác nhận và trang đơn hàng |
| **Đăng ký / Đăng nhập** | Session user; redirect về giỏ hàng sau khi đăng nhập nếu đang checkout |
| **Quên mật khẩu** | Nhập email → gửi link đặt lại mật khẩu (hiệu lực 1 giờ) qua email |
| **Tra cứu đơn hàng** | **Không cần đăng nhập**: nhập Mã đơn + SĐT hoặc Email → xem trạng thái và chi tiết đơn |
| **So sánh sản phẩm** | Chọn tối đa 3 sản phẩm từ trang chi tiết → bảng so sánh (giá, thương hiệu, xuất xứ, kích thước, bộ máy, kháng nước...) |
| **Lịch sử đơn hàng** | Xem danh sách đơn và chi tiết (chỉ user đã đăng nhập) |
| **Yêu thích (Wishlist)** | Thêm/bỏ sản phẩm yêu thích (cần đăng nhập) |
| **Email** | Xác nhận đơn hàng + tình trạng thanh toán; email đặt lại mật khẩu |
| **Admin** | Đăng nhập admin, CRUD sản phẩm (kèm ảnh) |

---

## Trang & URL

| Trang | URL | Mô tả |
|-------|-----|--------|
| Trang chủ | `/home` | Sản phẩm, tìm kiếm, lọc danh mục |
| Tìm kiếm | `/home?keyword=casio` | Theo tên/mô tả/thương hiệu |
| Lọc danh mục | `/home?cat=1` | 1=Nam, 2=Nữ, 3=Đôi, 4=Smartwatch |
| Chi tiết sản phẩm | `/product?id=1` | Thông tin, thêm giỏ, yêu thích, so sánh |
| Giỏ hàng | `/cart` | Xem/sửa giỏ; form đặt hàng (cần đăng nhập) |
| Xác nhận đơn | `/orderConfirm?id=1` | Sau khi đặt hàng; hiện VietQR nếu chọn thanh toán QR |
| Đăng nhập | `/login` | Có link "Quên mật khẩu?" |
| Đăng ký | `/register` | Tạo tài khoản |
| Quên mật khẩu | `/forgotPassword` | Gửi link đặt lại mật khẩu |
| Đặt lại mật khẩu | `/resetPassword?token=xxx` | Form đổi mật khẩu (từ link email) |
| Tra cứu đơn | `/orderLookup` | Mã đơn + SĐT/Email, không cần đăng nhập |
| So sánh sản phẩm | `/compare` | Danh sách so sánh (tối đa 3); `?add=id` / `?remove=id` |
| Lịch sử đơn | `/orders` | Danh sách đơn (cần đăng nhập) |
| Chi tiết đơn | `/orders?id=1` | Chi tiết đơn (cần đăng nhập) |
| Yêu thích | `/wishlist` | Sản phẩm đã thích (cần đăng nhập) |
| Cá nhân | `/profile` | Sửa thông tin, đổi mật khẩu (cần đăng nhập) |
| Admin đăng nhập | `/admin/login` | **admin** / **admin123** |
| Admin sản phẩm | `/admin/products` | Thêm/sửa/xóa sản phẩm |

---

## Cài đặt & Chạy

### 1. Cấu hình database

1. Mở **SQL Server Management Studio**, kết nối tới SQL Server.
2. Chạy lần lượt:
   - **`database.sql`** — tạo database `DWatchDB`, bảng, dữ liệu mẫu.
   - **`database_add_payment_method.sql`** — nếu bảng `Orders` đã tồn tại nhưng chưa có cột `PaymentMethod`, `PaymentStatus`.
   - **`database_password_reset.sql`** — tạo bảng `PasswordResetToken` (cho chức năng quên mật khẩu).
   - **`database_password_hash.sql`** — băm mật khẩu admin seed bằng BCrypt và liệt kê các tài khoản còn lưu plaintext (xem [Bảo mật](#bảo-mật)).

### 2. Cấu hình kết nối DB và (tùy chọn) Email

1. Copy **`.env.example`** thành **`.env`** (cùng thư mục với `pom.xml`).
2. Trong **`.env`** điền:
   - `DB_SERVER`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` (bắt buộc).
   - Để gửi email (xác nhận đơn, quên mật khẩu): cấu hình Gmail trong `EmailUtil.java` (FROM_EMAIL, APP_PASSWORD) hoặc dùng biến môi trường nếu bạn đã chỉnh code đọc từ env.

File **`.env`** nằm trong `.gitignore`, không bị đẩy lên Git.

### 3. Cấu hình VietQR (thanh toán QR)

Trong **`src/main/webapp/WEB-INF/web.xml`** có 3 context-param:

- **`vietqr_acq_id`** — Mã ngân hàng/SWIFT BANK CODE (6 số, VD: `970422` MB) hoặc tên ngắn (VD: `Vietinbank`).
- **`vietqr_account_no`** — Số tài khoản nhận tiền (của bạn).
- **`vietqr_account_name`** — Tên chủ tài khoản (nên không dấu).

Thay bằng **số tài khoản thật** của bạn để mã QR quét được và chuyển khoản thành công.

### 4. Bật TCP/IP cho SQL Server

- **SQL Server Configuration Manager** → Protocols → **TCP/IP** = Enabled, port **1433** → Restart SQL Server.

### 5. Build và chạy

```bash
cd DWatch    # thư mục chứa pom.xml
mvn clean package
```

- File WAR: **`target/DWatch.war`**.
- Copy vào **`webapps`** của Tomcat, khởi động Tomcat.
- Mở trình duyệt: **`http://localhost:8080/DWatch/`**

**Yêu cầu:** JDK 17, Maven 3.6+, Tomcat 9/10, SQL Server.

---

## Bảo mật

| Cơ chế | Thực hiện ở đâu |
|--------|-----------------|
| **Băm mật khẩu** | `util/PasswordUtil` — BCrypt cost 12. Không còn mật khẩu plaintext trong luồng đăng nhập/đăng ký/đổi mật khẩu/đặt lại mật khẩu, cho cả `Users` và `Admin`. |
| **Tự migrate dữ liệu cũ** | `service/AuthService`, `service/AdminAuthService` — bản ghi plaintext còn sót vẫn đăng nhập được và được băm lại, ghi đè ngay trong lần đăng nhập đúng đầu tiên. |
| **Chống CSRF** | `servlet/CsrfFilter` + `util/CsrfUtil` — synchronizer token cho mọi request POST/PUT/PATCH/DELETE; form JSP nhúng `<input type="hidden" name="_csrf">`. Cookie session dùng `SameSite=Lax` (`META-INF/context.xml`). |
| **Chống session fixation** | Cấp session id mới (`changeSessionId`) ngay sau khi đăng nhập thành công. |
| **Chống dò tài khoản** | Đăng nhập sai và email không tồn tại trả về cùng một thông báo; trang Quên mật khẩu luôn phản hồi giống nhau. |
| **Chống SQL injection** | Toàn bộ truy vấn dùng `PreparedStatement`. |

Sau khi deploy, **đổi ngay mật khẩu admin mặc định** (`admin` / `admin123`) — giá trị này được ghi công khai trong README nên phải coi như đã lộ.

### Lưu ý bảo mật khi commit/push:

- **`.env`** đã nên có trong **`.gitignore`** → không bị push (mật khẩu DB, biến môi trường).
- **`web.xml`** có thể chứa thông tin VietQR (số tài khoản mẫu); nếu dùng tài khoản thật, nên dùng biến môi trường hoặc file cấu hình ngoài, không commit tài khoản thật lên GitHub.

---

## Chạy test

```bash
mvn test
```

54 unit test (JUnit 5 + Mockito) cho `PasswordUtil`, `CsrfUtil`, `PasswordPolicy`, `AuthService`, `AdminAuthService` — chạy hoàn toàn bằng mock DAO, không cần SQL Server.

---

## Hướng dẫn cách clone và chạy:
```bash
git clone https://github.com/TNL293107/DWatch.git
cd DWatch
```

Sau đó:

1. Tạo **`.env`** từ **`.env.example`**, điền `DB_SERVER`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.
2. Chạy **`database.sql`**, **`database_add_payment_method.sql`**, **`database_password_reset.sql`** trong SQL Server.
3. (Tùy chọn) Sửa **`web.xml`** — VietQR: `vietqr_acq_id`, `vietqr_account_no`, `vietqr_account_name`.
4. Build: **`mvn clean package`** → deploy **`target/DWatch.war`** lên Tomcat.
5. Mở: **`http://localhost:8080/DWatch/`**

---

## Xử lý lỗi thường gặp

| Vấn đề | Gợi ý xử lý |
|--------|--------------|
| `ClassNotFoundException: SQLServerDriver` | Thêm MSSQL JDBC JAR vào dependency (Maven) hoặc `WEB-INF/lib/` |
| `SQLException: Connection refused` | Bật TCP/IP, port 1433; kiểm tra `.env` (DB_SERVER, DB_PORT) |
| `javax.el.PropertyNotFoundException` | Đảm bảo JSTL đã khai báo trong `pom.xml` và có trong WAR |
| Chữ tiếng Việt hiển thị sai | Kiểm tra collation DB (VD: `Vietnamese_CI_AS`), CharsetFilter UTF-8 |
| Quên mật khẩu báo lỗi DB | Chạy **`database_password_reset.sql`** để tạo bảng `PasswordResetToken` |
| Đặt hàng thất bại (lỗi cột) | Chạy **`database_add_payment_method.sql`** để thêm cột vào `Orders` |
| Quét QR báo "tài khoản đã đóng" | Đổi `vietqr_*` trong `web.xml` sang **số tài khoản và ngân hàng thật** của bạn |

---

## Liên kết

- **GitHub:** [https://github.com/TNL293107/DWatch](https://github.com/TNL293107/DWatch)
