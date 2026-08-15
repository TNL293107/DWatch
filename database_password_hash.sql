-- Migration: chuyển mật khẩu từ plaintext sang BCrypt
--
-- Ứng dụng đã tự migrate: mỗi lần một tài khoản đăng nhập đúng, nếu giá trị đang lưu
-- chưa phải hash BCrypt thì service layer băm lại và ghi đè ngay (xem service/AuthService
-- và service/AdminAuthService). Script này chỉ để:
--   (1) băm sẵn tài khoản admin seed, để bản dump DB không còn mật khẩu dạng chữ thường;
--   (2) kiểm tra xem còn bản ghi plaintext nào chưa được migrate.
--
-- Cột Password của cả Users và Admin đang là nvarchar(255) — đủ chứa hash BCrypt (60 ký tự),
-- không cần đổi schema.

USE [DWatchDB]
GO

-- ---------------------------------------------------------------------------
-- (1) Băm mật khẩu admin seed.
--     Hash dưới đây tương ứng mật khẩu 'admin123' (BCrypt cost 12).
--     ĐỔI MẬT KHẨU ADMIN NGAY SAU KHI DEPLOY — đây chỉ là giá trị demo, đã công khai
--     trong README nên phải coi như đã lộ.
-- ---------------------------------------------------------------------------
UPDATE [dbo].[Admin]
SET [Password] = '$2a$12$g4kpi8/nQ2qGFjAXdbv19.wtupZ3eehOJRn1VAhdrFfUyLjAxzAgO'
WHERE [Username] = N'admin'
  AND [Password] NOT LIKE '$2%';
GO

-- ---------------------------------------------------------------------------
-- (2) Kiểm tra: liệt kê các tài khoản còn lưu plaintext.
--     Các dòng này sẽ tự biến mất sau khi chủ tài khoản đăng nhập lần kế tiếp.
-- ---------------------------------------------------------------------------
SELECT 'Users' AS TableName, [UserID] AS Id, [Email] AS Account
FROM [dbo].[Users]
WHERE [Password] NOT LIKE '$2%' OR LEN([Password]) <> 60
UNION ALL
SELECT 'Admin', [AdminID], [Username]
FROM [dbo].[Admin]
WHERE [Password] NOT LIKE '$2%' OR LEN([Password]) <> 60;
GO

-- ---------------------------------------------------------------------------
-- (3) TÙY CHỌN — với các tài khoản không bao giờ đăng nhập lại, mật khẩu plaintext
--     sẽ nằm mãi trong DB. Câu lệnh dưới đây vô hiệu hóa chúng bằng cách ghi một giá trị
--     không khớp được với bất kỳ mật khẩu nào; người dùng phải dùng chức năng
--     "Quên mật khẩu" để đặt lại. Bỏ comment khi muốn thực hiện.
-- ---------------------------------------------------------------------------
-- UPDATE [dbo].[Users]
-- SET [Password] = 'disabled-' + CONVERT(nvarchar(36), NEWID())
-- WHERE [Password] NOT LIKE '$2%';
-- GO
