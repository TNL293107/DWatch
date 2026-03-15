<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container auth-page">
    <div class="auth-box">
        <h2 class="auth-title">Quên Mật Khẩu</h2>
        <p class="auth-desc">Nhập email đăng ký, chúng tôi sẽ gửi link đặt lại mật khẩu (hiệu lực 1 giờ).</p>

        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert-success">${success}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/forgotPassword" method="post">
            <div class="form-group">
                <label>Email *</label>
                <input type="email" name="email" required class="form-input"
                       placeholder="email@example.com" value="${param.email}">
            </div>
            <button type="submit" class="btn-auth">Gửi Link Đặt Lại Mật Khẩu</button>
        </form>

        <p class="auth-switch">
            <a href="${pageContext.request.contextPath}/login">← Quay lại đăng nhập</a>
        </p>
    </div>
</div>

<%@ include file="footer.jsp" %>
