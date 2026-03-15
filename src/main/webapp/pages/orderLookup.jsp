<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container auth-page">
    <div class="auth-box lookup-box">
        <h2 class="auth-title">🔍 Tra Cứu Đơn Hàng</h2>
        <p class="auth-desc">Nhập mã đơn hàng và số điện thoại <strong>hoặc</strong> email dùng khi đặt hàng để xem trạng thái.</p>

        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/orderLookup" method="post">
            <div class="form-group">
                <label>Mã đơn hàng *</label>
                <input type="text" name="orderId" required class="form-input"
                       placeholder="VD: 4" value="${param.orderId}">
            </div>
            <div class="form-group">
                <label>Số điện thoại</label>
                <input type="tel" name="phone" class="form-input"
                       placeholder="0901 234 567" value="${param.phone}">
            </div>
            <div class="form-group">
                <label>Hoặc Email</label>
                <input type="email" name="email" class="form-input"
                       placeholder="email@example.com" value="${param.email}">
            </div>
            <p class="form-hint">Nhập ít nhất một trong hai: số điện thoại hoặc email.</p>
            <button type="submit" class="btn-auth">Tra Cứu</button>
        </form>

        <p class="auth-switch">
            <a href="${pageContext.request.contextPath}/home">← Về trang chủ</a>
        </p>
    </div>
</div>

<%@ include file="footer.jsp" %>
