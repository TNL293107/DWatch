<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jsp" %>

<div class="container confirm-page">
    <div class="confirm-box">
        <div class="confirm-icon">📦</div>
        <h1>Kết Quả Tra Cứu — Đơn Hàng #${order.orderID}</h1>

        <div class="confirm-info">
            <h3>Thông Tin Giao Hàng</h3>
            <table class="info-table">
                <tr><td>Người nhận</td><td><strong>${order.fullName}</strong></td></tr>
                <tr><td>Điện thoại</td><td>${order.phone}</td></tr>
                <tr><td>Email</td><td>${order.email}</td></tr>
                <tr><td>Địa chỉ</td><td>${order.address}</td></tr>
                <tr><td>Ngày đặt</td><td><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td></tr>
                <tr><td>Trạng thái</td><td><span class="order-status">${order.status != null ? order.status : 'Đang xử lý'}</span></td></tr>
                <c:if test="${not empty order.paymentStatus}">
                    <tr><td>Tình trạng thanh toán</td><td><strong>${order.paymentStatus}</strong></td></tr>
                </c:if>
            </table>
        </div>

        <div class="confirm-items">
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

        <div class="confirm-actions">
            <a href="${pageContext.request.contextPath}/orderLookup" class="btn-outline">🔍 Tra cứu đơn khác</a>
            <a href="${pageContext.request.contextPath}/home" class="btn-primary">Về trang chủ</a>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
