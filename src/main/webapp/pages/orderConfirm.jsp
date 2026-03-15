<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="header.jsp" %>

<div class="container confirm-page">

    <div class="confirm-box">
        <div class="confirm-icon">✅</div>
        <h1>Đặt Hàng Thành Công!</h1>
        <p>Cảm ơn bạn đã mua hàng tại DWatch.<br>
           Mã đơn hàng của bạn: <strong>#${order.orderID}</strong></p>

        <div class="confirm-info">
            <h3>Thông Tin Giao Hàng</h3>
            <table class="info-table">
                <tr><td>Người nhận:</td><td><strong>${order.fullName}</strong></td></tr>
                <tr><td>Điện thoại:</td><td>${order.phone}</td></tr>
                <tr><td>Email:</td>     <td>${order.email}</td></tr>
                <tr><td>Địa chỉ:</td>  <td>${order.address}</td></tr>
                <c:if test="${not empty order.note}">
                    <tr><td>Ghi chú:</td><td>${order.note}</td></tr>
                </c:if>
            </table>
        </div>

        <div class="confirm-items">
            <h3>Chi Tiết Đơn Hàng</h3>
            <table class="confirm-table">
                <thead>
                    <tr><th>Sản phẩm</th><th>Đơn giá</th><th>SL</th><th>Thành tiền</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="d" items="${details}">
                        <tr>
                            <td>${d.productName}</td>
                            <td><fmt:formatNumber value="${d.unitPrice}" type="number" groupingUsed="true"/>₫</td>
                            <td>${d.quantity}</td>
                            <td><fmt:formatNumber value="${d.subtotal}" type="number" groupingUsed="true"/>₫</td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3"><strong>Tổng cộng</strong></td>
                        <td><strong class="grand-total">
                            <fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/>₫
                        </strong></td>
                    </tr>
                </tfoot>
            </table>
        </div>

        <div class="confirm-actions">
            <a href="${pageContext.request.contextPath}/home" class="btn-primary">Tiếp Tục Mua Sắm</a>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
