<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Cart count helper
    java.util.Map<?,?> sessionCart = (java.util.Map<?,?>) session.getAttribute("cart");
    int cartCount = (sessionCart == null) ? 0 : sessionCart.size();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DWatch - Thế Giới Đồng Hồ</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,400;0,500;0,600;0,700;1,400&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<!-- Top Bar -->
<div class="topbar">
    <span>📞 Hotline: 1800 6177</span>
    <span>🚚 Miễn phí vận chuyển đơn từ 500.000₫</span>
    <span>⚡ Đồng hồ chính hãng 100%</span>
</div>

<!-- Header -->
<header class="header">
    <div class="container header-inner">
        <a href="${pageContext.request.contextPath}/home" class="logo">
            <span class="logo-d">D</span><span class="logo-watch">WATCH</span>
        </a>

        <!-- Search Bar -->
        <form class="search-form" action="${pageContext.request.contextPath}/home" method="get">
            <input type="text" name="keyword" class="search-input"
                   placeholder="Tìm kiếm đồng hồ, thương hiệu..."
                   value="${keyword}">
            <button type="submit" class="search-btn">🔍</button>
        </form>

        <!-- User Account -->
        <%
            model.User loggedUser = (model.User) session.getAttribute("loggedUser");
        %>
        <% if (loggedUser != null) { %>
            <a href="${pageContext.request.contextPath}/profile" class="user-btn">
                👤 <%= loggedUser.getFullName().split(" ")[0] %>
            </a>
        <% } else { %>
            <a href="${pageContext.request.contextPath}/login" class="user-btn">Đăng nhập</a>
        <% } %>

        <!-- Cart Icon -->
        <a href="${pageContext.request.contextPath}/cart" class="cart-btn">
            🛒 Giỏ hàng
            <% if (cartCount > 0) { %>
                <span class="cart-badge"><%= cartCount %></span>
            <% } %>
        </a>
    </div>

    <!-- Navigation -->
    <nav class="navbar">
        <div class="container nav-inner">
            <a href="${pageContext.request.contextPath}/home" class="nav-link">Trang Chủ</a>
            <a href="${pageContext.request.contextPath}/home?cat=1" class="nav-link">Đồng Hồ Nam</a>
            <a href="${pageContext.request.contextPath}/home?cat=2" class="nav-link">Đồng Hồ Nữ</a>
            <a href="${pageContext.request.contextPath}/home?cat=3" class="nav-link">Đồng Hồ Đôi</a>
            <a href="${pageContext.request.contextPath}/home?cat=4" class="nav-link">Smartwatch</a>
            <a href="${pageContext.request.contextPath}/admin/login" class="nav-link nav-admin">⚙ Admin</a>
        </div>
    </nav>
</header>
