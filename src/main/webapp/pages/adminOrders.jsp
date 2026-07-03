<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Đơn Hàng — DWatch Admin</title>
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
            <a href="${pageContext.request.contextPath}/admin/orders" class="admin-nav active">Đơn Hàng</a>
            <a href="${pageContext.request.contextPath}/admin/vouchers" class="admin-nav">Mã Giảm Giá</a>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="admin-nav">Thống Kê</a>
            <a href="${pageContext.request.contextPath}/home" class="admin-nav">← Trang Chủ</a>
        </nav>
    </div>
</header>

<div class="admin-content">

    <c:if test="${param.msg == 'updated'}">
        <div class="alert-success">✔ Cập nhật trạng thái thành công!</div>
    </c:if>
    <c:if test="${param.msg == 'error'}">
        <div class="alert-error">✘ Có lỗi xảy ra. Vui lòng thử lại.</div>
    </c:if>

    <div class="admin-panel">
        <h2>Danh Sách Đơn Hàng</h2>

        <div class="admin-filter-bar">
            <a href="${pageContext.request.contextPath}/admin/orders"
               class="btn-outline btn-sm ${empty statusFilter ? 'active' : ''}">Tất cả</a>
            <c:forEach var="s" items="${statuses}">
                <a href="${pageContext.request.contextPath}/admin/orders?status=${s}"
                   class="btn-outline btn-sm ${statusFilter == s ? 'active' : ''}">${s}</a>
            </c:forEach>
        </div>

        <div class="table-responsive">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Mã đơn</th>
                        <th>Khách hàng</th>
                        <th>Ngày đặt</th>
                        <th>Tổng tiền</th>
                        <th>Thanh toán</th>
                        <th>Trạng thái</th>
                        <th>Cập nhật</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td>#${o.orderID}</td>
                            <td>${o.fullName}<br><small>${o.email}</small></td>
                            <td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td><fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true"/>₫</td>
                            <td>${o.paymentStatus}</td>
                            <td><span class="order-status status-${o.status}">${o.statusLabel}</span></td>
                            <td>
                                <form action="${pageContext.request.contextPath}/admin/orders" method="post" class="inline-status-form">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="orderID" value="${o.orderID}">
                                    <select name="newStatus" class="form-input">
                                        <c:forEach var="s" items="${statuses}">
                                            <option value="${s}" ${o.status == s ? 'selected' : ''}>${s}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="submit" class="btn-edit">Lưu</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/admin/orders?status=${statusFilter}&page=${currentPage - 1}" class="page-btn">‹</a>
                </c:if>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="${pageContext.request.contextPath}/admin/orders?status=${statusFilter}&page=${i}"
                       class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                </c:forEach>
                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/admin/orders?status=${statusFilter}&page=${currentPage + 1}" class="page-btn">›</a>
                </c:if>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>
