<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jsp" %>

<div class="container profile-page">
    <h1 class="page-title">📦 Lịch Sử Đơn Hàng</h1>
    <div class="profile-layout">
        <div class="profile-sidebar">
            <div class="profile-avatar">
                <div class="avatar-circle">${loggedUser.fullName.substring(0,1).toUpperCase()}</div>
                <div class="avatar-name">${loggedUser.fullName}</div>
                <div class="avatar-email">${loggedUser.email}</div>
            </div>
            <nav class="profile-nav">
                <a href="${pageContext.request.contextPath}/profile" class="profile-nav-link">⚙ Thông tin cá nhân</a>
                <a href="${pageContext.request.contextPath}/orders" class="profile-nav-link active">📦 Lịch sử đơn hàng</a>
                <a href="${pageContext.request.contextPath}/wishlist" class="profile-nav-link">❤ Yêu thích</a>
                <a href="${pageContext.request.contextPath}/logout" class="profile-nav-link logout">🚪 Đăng xuất</a>
            </nav>
        </div>
        <div class="profile-content">
            <c:choose>
                <c:when test="${empty orders}">
                    <div class="empty-state">
                        <p>😕 Bạn chưa có đơn hàng nào.</p>
                        <a href="${pageContext.request.contextPath}/home" class="btn-primary">Mua sắm ngay</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Mã đơn</th>
                                <th>Ngày đặt</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái</th>
                                <th>Chi tiết</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="o" items="${orders}">
                                <tr>
                                    <td>#${o.orderID}</td>
                                    <td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td style="color:var(--gold);font-weight:700">
                                        <fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true"/>₫
                                    </td>
                                    <td>
                                        <span class="order-status status-${o.status != null ? o.status.toLowerCase().replace(' ','') : 'pending'}">
                                            ${o.status != null ? o.status : 'Đang xử lý'}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/orders?id=${o.orderID}"
                                           class="btn-edit">Xem</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<%@ include file="footer.jsp" %>
