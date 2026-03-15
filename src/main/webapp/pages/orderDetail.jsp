<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jsp" %>

<div class="container profile-page">
    <h1 class="page-title">Chi Tiết Đơn Hàng #${order.orderID}</h1>
    <div class="profile-layout">
        <div class="profile-sidebar">
            <div class="profile-avatar">
                <div class="avatar-circle">${loggedUser.fullName.substring(0,1).toUpperCase()}</div>
                <div class="avatar-name">${loggedUser.fullName}</div>
            </div>
            <nav class="profile-nav">
                <a href="${pageContext.request.contextPath}/profile" class="profile-nav-link">⚙ Thông tin cá nhân</a>
                <a href="${pageContext.request.contextPath}/orders" class="profile-nav-link active">📦 Lịch sử đơn hàng</a>
                <a href="${pageContext.request.contextPath}/wishlist" class="profile-nav-link">❤ Yêu thích</a>
                <a href="${pageContext.request.contextPath}/logout" class="profile-nav-link logout">🚪 Đăng xuất</a>
            </nav>
        </div>
        <div class="profile-content">
            <div class="profile-panel">
                <h3>Thông Tin Giao Hàng</h3>
                <table class="info-table">
                    <tr><td>Người nhận</td><td><strong>${order.fullName}</strong></td></tr>
                    <tr><td>Điện thoại</td><td>${order.phone}</td></tr>
                    <tr><td>Email</td><td>${order.email}</td></tr>
                    <tr><td>Địa chỉ</td><td>${order.address}</td></tr>
                    <tr><td>Ghi chú</td><td>${order.note}</td></tr>
                    <tr><td>Ngày đặt</td><td><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td></tr>
                    <tr><td>Trạng thái</td>
                        <td><span class="order-status">${order.status != null ? order.status : 'Đang xử lý'}</span></td>
                    </tr>
                </table>
            </div>
            <div class="profile-panel" style="margin-top:20px">
                <h3>Sản Phẩm Đã Đặt</h3>
                <table class="confirm-table">
                    <thead><tr><th>Sản phẩm</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th></tr></thead>
                    <tbody>
                        <c:forEach var="d" items="${details}">
                            <tr>
                                <td>${d.productName}</td>
                                <td><fmt:formatNumber value="${d.unitPrice}" type="number" groupingUsed="true"/>₫</td>
                                <td>${d.quantity}</td>
                                <td style="color:var(--gold);font-weight:700">
                                    <fmt:formatNumber value="${d.unitPrice * d.quantity}" type="number" groupingUsed="true"/>₫
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="3" style="text-align:right;font-weight:700">Tổng cộng:</td>
                            <td style="color:var(--gold);font-weight:700;font-size:18px">
                                <fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/>₫
                            </td>
                        </tr>
                    </tfoot>
                </table>
            </div>
            <div style="margin-top:20px">
                <a href="${pageContext.request.contextPath}/orders" class="btn-outline">← Quay lại danh sách</a>
            </div>
        </div>
    </div>
</div>
<%@ include file="footer.jsp" %>
