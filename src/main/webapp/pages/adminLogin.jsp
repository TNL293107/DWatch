<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login — DWatch</title>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,400;0,500;0,600;0,700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="admin-body">

<div class="admin-login-box">
    <div class="admin-logo">
        <span class="logo-d">D</span><span class="logo-watch">WATCH</span>
        <br><small>Admin Panel</small>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert-error">${error}</div>
    <% } %>

    <form action="${pageContext.request.contextPath}/admin/login" method="post">
        <div class="form-group">
            <label>Tên đăng nhập</label>
            <input type="text" name="username" required class="form-input"
                   placeholder="admin" autofocus>
        </div>
        <div class="form-group">
            <label>Mật khẩu</label>
            <input type="password" name="password" required class="form-input"
                   placeholder="••••••••">
        </div>
        <button type="submit" class="btn-order" style="width:100%">Đăng Nhập</button>
    </form>
    <a href="${pageContext.request.contextPath}/home" class="back-link">← Về trang chủ</a>
</div>

</body>
</html>
