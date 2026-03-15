# DWatch E-Commerce — Setup Guide
## Apache NetBeans 25 + Microsoft SQL Server Management Studio 22

---

## Project Structure

```
DWatchProject/
├── src/java/
│   ├── dao/
│   │   ├── CategoryDAO.java
│   │   ├── OrderDAO.java
│   │   └── ProductDAO.java
│   ├── model/
│   │   ├── CartItem.java
│   │   ├── Category.java
│   │   ├── Order.java
│   │   ├── OrderDetail.java
│   │   └── Product.java
│   ├── servlet/
│   │   ├── AdminLoginServlet.java
│   │   ├── CartServlet.java
│   │   ├── CharsetFilter.java
│   │   ├── HomeServlet.java
│   │   ├── OrderConfirmServlet.java
│   │   ├── ProductDetailServlet.java
│   │   └── ProductSetupServlet.java
│   └── util/
│       └── DBUtil.java
└── web/
    ├── WEB-INF/
    │   └── web.xml
    ├── css/
    │   ├── admin.css
    │   └── style.css
    ├── images/          ← put product images here
    ├── js/
    │   └── script.js
    ├── pages/
    │   ├── adminLogin.jsp
    │   ├── cart.jsp
    │   ├── footer.jsp
    │   ├── header.jsp
    │   ├── home.jsp
    │   ├── orderConfirm.jsp
    │   ├── productDetail.jsp
    │   └── productSetup.jsp
    └── index.jsp
```

---

## Step 1 — Set Up the Database

1. Open **SQL Server Management Studio 22**
2. Connect to your local SQL Server instance
3. Open `database.sql` from this project
4. Execute the entire script (**F5** or Run)
5. This creates: `DWatchDB` database with all tables and 10 sample watches

---

## Step 2 — Create Project in NetBeans 25

1. **File → New Project → Java with Ant → Java Web → Web Application**
2. Name: `DWatchProject`
3. Server: **Apache Tomcat** (add if not already added)
4. Java EE Version: **Java EE 8** (Web Profile)
5. Click **Finish**

---

## Step 3 — Copy Source Files

Copy files into the NetBeans project:
- All `.java` files → `src/java/` (maintain package folder structure)
- All `.jsp`, `.css`, `.js` files → `web/` (maintain folder structure)
- Replace `web/WEB-INF/web.xml` with the provided one

---

## Step 4 — Add Required Libraries (JARs)

Right-click project → **Properties → Libraries → Add JAR/Folder**

Add these JARs to the project classpath:

### A. MSSQL JDBC Driver
Download from: https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server
- File: `mssql-jdbc-12.x.x.jre11.jar`
- Add to: Project Libraries

### B. JSTL (JSP Standard Tag Library)
Download: `javax.servlet.jsp.jstl-1.2.x.jar` and `javax.servlet.jsp.jstl-api-1.2.x.jar`
OR add via Maven dependencies, or download from Apache Taglibs.
- Add to: Project Libraries (and also copy to `web/WEB-INF/lib/`)

### In NetBeans, also ensure:
- Right-click **Libraries → Add Library** → add **"Java EE 8 API Library"** if available

---

## Step 5 — Configure Database Connection

Credentials are read from a **`.env`** file (or environment variables), so you never put your password in code.

1. Copy the example file: **`.env.example`** → **`.env`** (same folder, next to `pom.xml`).
2. Open **`.env`** and set your SQL Server values:
   - `DB_SERVER` — host (e.g. `localhost`)
   - `DB_PORT` — port (default `1433`)
   - `DB_NAME` — database name (e.g. `DWatchDB`)
   - `DB_USER` — SQL login (e.g. `sa`)
   - `DB_PASSWORD` — your password

File **`.env`** is ignored by Git and will not be pushed to GitHub.

> **Chạy bằng Tomcat (WAR):** Ứng dụng đọc `.env` theo thư mục hiện tại. Khi deploy WAR, hãy đặt **biến môi trường** `DB_SERVER`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` cho process Tomcat (hoặc đặt `.env` ở thư mục gốc Tomcat rồi khởi động Tomcat từ đó).  
> **Alternative:** Dùng biến môi trường thay cho `.env`.  
> **Windows Authentication:** `DBUtil` đang dùng SQL login. Dùng integrated security thì cần sửa URL trong code và `sqljdbc_auth.dll`.

---

## Step 6 — Enable TCP/IP in SQL Server

1. Open **SQL Server Configuration Manager**
2. Expand **SQL Server Network Configuration → Protocols for SQLEXPRESS** (or MSSQLSERVER)
3. Enable **TCP/IP**
4. Right-click TCP/IP → Properties → **IP Addresses** tab
5. Scroll to **IPAll** → set **TCP Port = 1433**
6. Restart **SQL Server service**

---

## Step 7 — Add Product Images

Place JPEG/PNG images in: `web/images/`

Default filenames expected:
- `p1.jpg` through `p10.jpg` (for the 10 seeded products)
- `default.jpg` (fallback for missing images)

You can use any watch images downloaded from Google Images or use placeholder images.

---

## Step 8 — Run the Project

1. Right-click project → **Run** (or Shift+F6)
2. NetBeans will deploy to Tomcat and open the browser
3. Default URL: `http://localhost:8080/DWatchProject/`

---

## Pages & URLs

| Page | URL | Description |
|------|-----|-------------|
| Home / Product List | `/home` | Shows all products sorted by price ↑ |
| Search | `/home?keyword=casio` | Search by name/description |
| Category Filter | `/home?cat=1` | Filter by category |
| Product Detail | `/product?id=1` | Product info + Add to Cart |
| Shopping Cart | `/cart` | View, edit, delete cart items |
| Order Confirmation | `/orderConfirm?id=1` | After successful checkout |
| Admin Login | `/admin/login` | Login: **admin** / **admin123** |
| Admin Products | `/admin/products` | Add / edit / delete products |

---

## Assignment Requirements Mapping

| Requirement | Implementation |
|-------------|---------------|
| **1. Setup products** | `/admin/products` — full CRUD with image upload |
| **2. Home page** — list sorted by price, search | `HomeServlet` → `home.jsp` |
| **3. Product details** — picture, info, Add to Cart | `ProductDetailServlet` → `productDetail.jsp` |
| **4. Shopping cart** — add/remove/change qty, delivery form, save order | `CartServlet` → `cart.jsp` |

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `ClassNotFoundException: SQLServerDriver` | Add MSSQL JDBC JAR to project libs AND `WEB-INF/lib/` |
| `SQLException: Connection refused` | Enable TCP/IP in SQL Config Manager, check port 1433 |
| `javax.el.PropertyNotFoundException` | Ensure JSTL JARs are in `WEB-INF/lib/` |
| Vietnamese text shows `?????` | Confirm DB collation is `Vietnamese_CI_AS` or `SQL_Latin1_General_CP1_CI_AS`; check CharsetFilter |
| Images not showing | Put `.jpg` files in `web/images/`, rebuild project |

---

## Push code lên GitHub (cho chủ repo)

### 1. Tạo repository trên GitHub

- Vào [github.com](https://github.com) → **New repository**
- Đặt tên (ví dụ: `DWatch`), không cần tích "Initialize with README" nếu đã có code local
- Ghi lại URL repo (ví dụ: `https://github.com/<username>/DWatch.git`)

### 2. Khởi tạo Git và push

Trong thư mục gốc của project (chứa `pom.xml`), chạy:

```bash
cd "c:\Users\long2\Downloads\DWatch\DWatch"   # hoặc đường dẫn tới thư mục DWatch của bạn
git init
git add .
git commit -m "Initial commit: DWatch e-commerce project"
git branch -M main
git remote add origin https://github.com/<username>/<tên-repo>.git
git push -u origin main
```

Thay `<username>` và `<tên-repo>` bằng tên GitHub và tên repo của bạn.

### 3. Lưu ý bảo mật

- Mật khẩu DB đọc từ file **`.env`** (hoặc biến môi trường). File `.env` đã được đưa vào `.gitignore` nên sẽ **không** bị push lên GitHub. Người clone chỉ cần tạo `.env` từ `.env.example` và điền thông tin của họ.

---

## Cách tải và chạy (cho người clone từ GitHub)

1. **Clone:**  
   `git clone https://github.com/<username>/<tên-repo>.git` → `cd <tên-repo>`
2. **Cấu hình DB:** Copy `.env.example` thành `.env`, mở `.env` và điền `DB_SERVER`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` (xem **Step 5** ở trên).
3. **Chạy script database:** Mở `database.sql` trong SQL Server Management Studio và thực thi (xem **Step 1**).
4. **Build:** Trong thư mục project (có `pom.xml`):  
   `mvn clean package`  
   → File `target/DWatch.war` sẽ được tạo.
5. **Chạy:** Copy `DWatch.war` vào thư mục `webapps` của Tomcat, khởi động Tomcat, mở trình duyệt: `http://localhost:8080/DWatch/`

**Yêu cầu:** JDK 17, Maven 3.6+, Tomcat 9/10, SQL Server.
