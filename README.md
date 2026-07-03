# DWatch — Cửa Hàng Đồng Hồ E-Commerce

**Repository:** [https://github.com/TNL293107/DWatch](https://github.com/TNL293107/DWatch)

Ứng dụng web bán đồng hồ xây bằng **Java (Servlet/JSP)**, **Maven**, **Microsoft SQL Server**. Hỗ trợ đăng ký/đăng nhập (mật khẩu băm bcrypt), giỏ hàng, mã giảm giá, đặt hàng (COD / VietQR), theo dõi trạng thái đơn hàng, đánh giá sản phẩm, quên mật khẩu, tra cứu đơn hàng, so sánh sản phẩm, quản trị sản phẩm/đơn hàng/mã giảm giá và dashboard thống kê cho admin.

**Setup Guide (Ver 2.0)** — Apache NetBeans 25 + Microsoft SQL Server Management Studio 22

---

## Công nghệ

- **Backend:** Java 17, Servlet 4.0, JSP, JSTL
- **Build:** Maven 3.x
- **Database:** Microsoft SQL Server
- **Deploy:** WAR → Apache Tomcat 9/10
- **Bảo mật:** BCrypt (băm mật khẩu), CSRF token trên mọi form POST
- **Email:** Apache Commons Email (Gmail SMTP) — xác nhận đơn hàng, đặt lại mật khẩu, thông báo đổi trạng thái đơn
- **Test:** JUnit 5, AssertJ, Mockito (`src/test/java`)

---

## Cấu trúc project (Maven)

Mã nguồn tổ chức theo **feature/domain** (không theo layer) dưới package gốc `com.dwatch`:

```
DWatch/
├── pom.xml
├── .env.example              ← copy thành .env, điền DB + (tùy chọn) email/admin
├── database.sql              ← script tạo DB và bảng chính
├── database_add_payment_method.sql
├── database_password_reset.sql
├── database_order_status.sql
├── database_reviews.sql
├── database_vouchers.sql
├── LICENSE
├── README.md
└── src/
    ├── main/
    │   ├── java/com/dwatch/
    │   │   ├── common/     DBUtil, AppConfig, EmailUtil, VietQRUtil, PasswordUtil,
    │   │   │               CsrfUtil, CsrfFilter, CharsetFilter, Pagination
    │   │   ├── product/    Product, Category, ProductDAO, CategoryDAO,
    │   │   │               HomeServlet, ProductDetailServlet, CompareServlet
    │   │   ├── cart/       CartItem, CartServlet
    │   │   ├── order/      Order, OrderDetail, OrderDAO, OrderConfirmServlet,
    │   │   │               OrderHistoryServlet, OrderLookupServlet
    │   │   ├── user/       User, UserDAO, UserServlet,
    │   │   │               ForgotPasswordServlet, ResetPasswordServlet
    │   │   ├── wishlist/   WishlistDAO, WishlistServlet
    │   │   ├── review/     Review, ReviewDAO
    │   │   ├── voucher/    Voucher, VoucherDAO
    │   │   └── admin/      AdminDAO, AdminLoginServlet, AdminBootstrapListener,
    │   │                   ProductSetupServlet, AdminOrderServlet,
    │   │                   AdminVoucherServlet, AdminDashboardServlet,
    │   │                   StatsDAO, TopProduct, RevenuePoint
    │   └── webapp/
    │       ├── WEB-INF/web.xml    ← context-param VietQR, filter mapping (Charset → Csrf)
    │       ├── css/{style,admin}.css
    │       ├── images/
    │       └── pages/*.jsp
    └── test/java/com/dwatch/     ← JUnit 5 unit tests (mirrors main package structure)
```

---

## Chức năng chính

| Chức năng | Mô tả |
|-----------|--------|
| **Trang chủ** | Danh sách sản phẩm, tìm kiếm, lọc theo danh mục, phân trang |
| **Chi tiết sản phẩm** | Ảnh, mô tả, thông số, thêm giỏ, yêu thích, **thêm vào so sánh**, **đánh giá & xếp hạng sao** |
| **Đánh giá sản phẩm** | Người dùng đã đăng nhập gửi đánh giá 1–5 sao + nhận xét; hiển thị điểm trung bình và danh sách đánh giá (phân trang) |
| **Giỏ hàng** | Sửa/xóa số lượng, **áp dụng mã giảm giá**, cập nhật giỏ; checkout bắt buộc đăng nhập (hoặc tạo tài khoản) |
| **Mã giảm giá (Voucher)** | Giảm theo % hoặc số tiền cố định, có điều kiện đơn tối thiểu, giới hạn lượt dùng, ngày hết hạn |
| **Thanh toán** | **COD** (thanh toán khi nhận hàng) hoặc **VietQR** (mã QR đúng số tiền sau giảm giá) |
| **Trạng thái đơn hàng** | Vòng đời chi tiết: Chờ xử lý → Đã xác nhận → Đang giao hàng → Đã giao hàng (hoặc Đã hủy); admin cập nhật, khách nhận email thông báo khi trạng thái thay đổi |
| **Tình trạng thanh toán** | Đã thanh toán / Chưa thanh toán — hiển thị riêng biệt với trạng thái xử lý đơn |
| **Đăng ký / Đăng nhập** | Mật khẩu băm bằng **BCrypt**; tài khoản cũ (mật khẩu thô) tự động nâng cấp khi đăng nhập lần kế tiếp; session user; redirect về giỏ hàng sau khi đăng nhập nếu đang checkout |
| **Bảo vệ CSRF** | Mọi form POST đều mang token CSRF theo phiên, request thiếu/sai token bị từ chối (403) |
| **Quên mật khẩu** | Nhập email → gửi link đặt lại mật khẩu (hiệu lực 1 giờ) qua email |
| **Tra cứu đơn hàng** | **Không cần đăng nhập**: nhập Mã đơn + SĐT hoặc Email → xem trạng thái và chi tiết đơn |
| **So sánh sản phẩm** | Chọn tối đa 3 sản phẩm từ trang chi tiết → bảng so sánh (giá, thương hiệu, xuất xứ, kích thước, bộ máy, kháng nước...) |
| **Lịch sử đơn hàng** | Danh sách đơn (có phân trang) và chi tiết (chỉ user đã đăng nhập) |
| **Yêu thích (Wishlist)** | Thêm/bỏ sản phẩm yêu thích (cần đăng nhập) |
| **Email** | Xác nhận đơn hàng + tình trạng thanh toán; đặt lại mật khẩu; thông báo đổi trạng thái đơn |
| **Admin — Sản phẩm** | Đăng nhập admin (mật khẩu băm bcrypt), CRUD sản phẩm (kèm ảnh) |
| **Admin — Đơn hàng** | Danh sách đơn (phân trang, lọc theo trạng thái), cập nhật trạng thái từng đơn |
| **Admin — Mã giảm giá** | CRUD mã giảm giá (thêm/sửa/vô hiệu hóa) |
| **Admin — Dashboard** | Tổng doanh thu, số đơn theo trạng thái, sản phẩm bán chạy, doanh thu 14 ngày gần nhất |

---

## Trang & URL

| Trang | URL | Mô tả |
|-------|-----|--------|
| Trang chủ | `/home` | Sản phẩm, tìm kiếm, lọc danh mục, phân trang |
| Tìm kiếm | `/home?keyword=casio` | Theo tên/mô tả/thương hiệu |
| Lọc danh mục | `/home?cat=1` | 1=Nam, 2=Nữ, 3=Đôi, 4=Smartwatch |
| Chi tiết sản phẩm | `/product?id=1` | Thông tin, thêm giỏ, yêu thích, so sánh, đánh giá (`#reviews`) |
| Giỏ hàng | `/cart` | Xem/sửa giỏ, áp dụng mã giảm giá; form đặt hàng (cần đăng nhập) |
| Xác nhận đơn | `/orderConfirm?id=1` | Sau khi đặt hàng; hiện VietQR nếu chọn thanh toán QR |
| Đăng nhập | `/login` | Có link "Quên mật khẩu?" |
| Đăng ký | `/register` | Tạo tài khoản |
| Quên mật khẩu | `/forgotPassword` | Gửi link đặt lại mật khẩu |
| Đặt lại mật khẩu | `/resetPassword?token=xxx` | Form đổi mật khẩu (từ link email) |
| Tra cứu đơn | `/orderLookup` | Mã đơn + SĐT/Email, không cần đăng nhập |
| So sánh sản phẩm | `/compare` | Danh sách so sánh (tối đa 3); `?add=id` / `?remove=id` |
| Lịch sử đơn | `/orders` | Danh sách đơn, phân trang (cần đăng nhập) |
| Chi tiết đơn | `/orders?id=1` | Chi tiết đơn (cần đăng nhập) |
| Yêu thích | `/wishlist` | Sản phẩm đã thích (cần đăng nhập) |
| Cá nhân | `/profile` | Sửa thông tin, đổi mật khẩu (cần đăng nhập) |
| Admin đăng nhập | `/admin/login` | Mặc định **admin** / **admin123** (nên đổi qua `.env`, xem phần Bảo mật) |
| Admin sản phẩm | `/admin/products` | Thêm/sửa/xóa sản phẩm |
| Admin đơn hàng | `/admin/orders` | Danh sách + cập nhật trạng thái đơn; `?status=pending` để lọc |
| Admin mã giảm giá | `/admin/vouchers` | Thêm/sửa/vô hiệu hóa mã giảm giá |
| Admin dashboard | `/admin/dashboard` | Doanh thu, đơn theo trạng thái, top sản phẩm |

---

## Cài đặt & Chạy

### 1. Cấu hình database

Mở **SQL Server Management Studio**, kết nối tới SQL Server, chạy lần lượt (đúng thứ tự):

1. **`database.sql`** — tạo database `DWatchDB`, bảng, dữ liệu mẫu.
2. **`database_add_payment_method.sql`** — nếu bảng `Orders` đã tồn tại nhưng chưa có cột `PaymentMethod`, `PaymentStatus`.
3. **`database_password_reset.sql`** — tạo bảng `PasswordResetToken` (cho chức năng quên mật khẩu).
4. **`database_order_status.sql`** — backfill trạng thái `pending` cho đơn cũ + index trên `Orders.Status`.
5. **`database_reviews.sql`** — tạo bảng `Review` (đánh giá sản phẩm).
6. **`database_vouchers.sql`** — tạo bảng `Voucher` + thêm cột `VoucherCode`, `DiscountAmount` vào `Orders`.

Tất cả script đều dùng `IF NOT EXISTS` nên chạy lại nhiều lần vẫn an toàn.

### 2. Cấu hình kết nối DB, admin và (tùy chọn) Email

1. Copy **`.env.example`** thành **`.env`** (cùng thư mục với `pom.xml`).
2. Trong **`.env`** điền:
   - `DB_SERVER`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` (bắt buộc).
   - `ADMIN_BOOTSTRAP_USER`, `ADMIN_BOOTSTRAP_PASSWORD` (tùy chọn) — nếu điền, ứng dụng tự tạo tài khoản admin này (mật khẩu băm bcrypt) khi khởi động nếu chưa tồn tại. Bỏ trống thì vẫn dùng được seed mặc định `admin/admin123` trong `database.sql`.
   - `MAIL_FROM`, `MAIL_APP_PASSWORD`, `MAIL_FROM_NAME` (tùy chọn) — cấu hình Gmail SMTP để gửi email xác nhận đơn, đặt lại mật khẩu, thông báo đổi trạng thái. Không cấu hình thì email chỉ log lỗi, không làm hỏng chức năng khác.

File **`.env`** nằm trong `.gitignore`, không bị đẩy lên Git.

### 3. Cấu hình VietQR (thanh toán QR)

Trong **`src/main/webapp/WEB-INF/web.xml`** có 3 context-param:

- **`vietqr_acq_id`** — Mã ngân hàng/SWIFT BANK CODE (6 số, VD: `970422` MB) hoặc tên ngắn (VD: `Vietinbank`).
- **`vietqr_account_no`** — Số tài khoản nhận tiền (của bạn).
- **`vietqr_account_name`** — Tên chủ tài khoản (nên không dấu).

Thay bằng **số tài khoản thật** của bạn để mã QR quét được và chuyển khoản thành công.

### 4. Bật TCP/IP cho SQL Server

- **SQL Server Configuration Manager** → Protocols → **TCP/IP** = Enabled, port **1433** → Restart SQL Server.

### 5. Build, test và chạy

```bash
cd DWatch    # thư mục chứa pom.xml
mvn clean package
```

- `mvn clean package` sẽ chạy bộ test JUnit 5 (`src/test/java`) trước khi đóng gói.
- Chỉ chạy test: `mvn test`.
- File WAR: **`target/DWatch.war`**.
- Copy vào **`webapps`** của Tomcat, khởi động Tomcat.
- Mở trình duyệt: **`http://localhost:8080/DWatch/`**

**Yêu cầu:** JDK 17, Maven 3.6+, Tomcat 9/10, SQL Server.

---

## Bảo mật

- **Mật khẩu được băm bằng BCrypt** (thư viện `at.favre.lib:bcrypt`). Tài khoản tạo trước khi nâng cấp (mật khẩu thô) vẫn đăng nhập được bình thường và tự động được băm lại ngay khi đăng nhập thành công — không cần reset mật khẩu hàng loạt.
- **CSRF token** theo phiên được gắn vào mọi form POST; request thiếu hoặc sai token bị từ chối với mã 403.
- **Không hardcode secret trong source:** thông tin DB, tài khoản admin bootstrap, và Gmail SMTP đều đọc từ `.env` (xem `.env.example`) qua `AppConfig`.
- **Cần làm ngay sau khi nâng cấp từ bản cũ:**
  - Đổi mật khẩu admin mặc định (`admin/admin123`) — đăng nhập rồi đổi qua UI, hoặc cấu hình `ADMIN_BOOTSTRAP_USER`/`ADMIN_BOOTSTRAP_PASSWORD` với tài khoản khác.
  - Nếu trước đây bạn đã commit Gmail App Password vào `EmailUtil.java`, **hãy thu hồi/tạo lại App Password đó trên tài khoản Google** — giá trị cũ không còn được dùng trong code nhưng vẫn tồn tại trong lịch sử Git.
  - `web.xml` có thể chứa thông tin VietQR (số tài khoản mẫu); nếu dùng tài khoản thật, nên dùng biến môi trường hoặc file cấu hình ngoài, không commit tài khoản thật lên GitHub.
  - `.env` đã có trong `.gitignore` → không bị push (mật khẩu DB, biến môi trường).

---

## Hướng dẫn cách clone và chạy

```bash
git clone https://github.com/TNL293107/DWatch.git
cd DWatch
```

Sau đó:

1. Tạo **`.env`** từ **`.env.example`**, điền `DB_SERVER`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.
2. Chạy lần lượt 6 script SQL theo thứ tự ở mục "Cài đặt & Chạy" (bước 1).
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
| Trạng thái đơn luôn hiện "Chờ xử lý" | Chạy **`database_order_status.sql`** để backfill cột `Status` cho đơn cũ |
| Không thấy phần đánh giá sản phẩm | Chạy **`database_reviews.sql`** để tạo bảng `Review` |
| Áp mã giảm giá báo lỗi DB | Chạy **`database_vouchers.sql`** để tạo bảng `Voucher` và cột liên quan trong `Orders` |
| Submit form báo lỗi 403 | Thiếu hoặc sai `csrfToken` — đảm bảo form có `<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">` |
| Không gửi được email | Kiểm tra `MAIL_FROM`, `MAIL_APP_PASSWORD` trong `.env` (dùng Gmail App Password, không dùng mật khẩu tài khoản thường) |

---

## License

Phát hành theo giấy phép [MIT](LICENSE).

## Liên kết

- **GitHub:** [https://github.com/TNL293107/DWatch](https://github.com/TNL293107/DWatch)
