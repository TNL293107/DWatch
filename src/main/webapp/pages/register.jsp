<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container auth-page">
    <div class="auth-box">
        <h2 class="auth-title">Đăng Ký Tài Khoản</h2>

        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <input type="hidden" name="redirect" value="${param.redirect}">
            <div class="form-group">
                <label>Họ và tên *</label>
                <input type="text" name="fullName" required class="form-input"
                       placeholder="Nguyễn Văn A" value="${param.fullName}">
            </div>
            <div class="form-group">
                <label>Email *</label>
                <input type="email" name="email" required class="form-input"
                       placeholder="email@example.com" value="${param.email}">
            </div>
            <div class="form-group">
                <label>Số điện thoại</label>
                <input type="tel" name="phone" class="form-input"
                       placeholder="0901 234 567" value="${param.phone}">
            </div>
            <div class="form-group">
                <label>Mật khẩu *</label>
                <input type="password" name="password" required class="form-input"
                       placeholder="Tối thiểu 6 ký tự" minlength="6">
            </div>
            <div class="form-group">
                <label>Xác nhận mật khẩu *</label>
                <input type="password" name="password2" required class="form-input"
                       placeholder="Nhập lại mật khẩu" minlength="6">
            </div>
            <button type="submit" class="btn-auth">Tạo Tài Khoản</button>
        </form>

        <p class="auth-switch">
            Đã có tài khoản?
            <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
        </p>
    </div>
</div>

<%@ include file="footer.jsp" %>
