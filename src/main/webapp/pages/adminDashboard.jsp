<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống Kê — DWatch Admin</title>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,400;0,500;0,600;0,700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body class="admin-body-panel">

<header class="admin-header">
    <div class="admin-header-inner">
        <span class="admin-logo"><span class="logo-d">D</span><span class="logo-watch">WATCH</span> Admin</span>
        <nav>
            <a href="${pageContext.request.contextPath}/admin/products" class="admin-nav">Sản Phẩm</a>
            <a href="${pageContext.request.contextPath}/admin/orders" class="admin-nav">Đơn Hàng</a>
            <a href="${pageContext.request.contextPath}/admin/vouchers" class="admin-nav">Mã Giảm Giá</a>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="admin-nav active">Thống Kê</a>
            <a href="${pageContext.request.contextPath}/home" class="admin-nav">← Trang Chủ</a>
        </nav>
    </div>
</header>

<div class="admin-content">

    <div class="stat-cards">
        <div class="stat-card">
            <div class="stat-label">Tổng doanh thu (không tính đơn đã hủy)</div>
            <div class="stat-value"><fmt:formatNumber value="${totalRevenue}" type="number" groupingUsed="true"/>₫</div>
        </div>
        <c:forEach var="entry" items="${orderCountByStatus}">
            <div class="stat-card">
                <div class="stat-label">Đơn hàng: ${entry.key}</div>
                <div class="stat-value">${entry.value}</div>
            </div>
        </c:forEach>
    </div>

    <div class="admin-panel">
        <h2>Sản Phẩm Bán Chạy Nhất</h2>
        <div class="table-responsive">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Sản phẩm</th>
                        <th>Số lượng đã bán</th>
                        <th>Doanh thu</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty topProducts}">
                            <tr><td colspan="3">Chưa có dữ liệu bán hàng.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="p" items="${topProducts}">
                                <tr>
                                    <td>${p.productName}</td>
                                    <td>${p.totalSold}</td>
                                    <td><fmt:formatNumber value="${p.revenue}" type="number" groupingUsed="true"/>₫</td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

    <div class="admin-panel">
        <h2>Doanh Thu 14 Ngày Gần Nhất</h2>
        <div class="table-responsive">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Ngày</th>
                        <th>Doanh thu</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty revenueByDay}">
                            <tr><td colspan="2">Chưa có đơn hàng nào trong khoảng thời gian này.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="r" items="${revenueByDay}">
                                <tr>
                                    <td><fmt:formatDate value="${r.date}" pattern="dd/MM/yyyy"/></td>
                                    <td><fmt:formatNumber value="${r.revenue}" type="number" groupingUsed="true"/>₫</td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>
