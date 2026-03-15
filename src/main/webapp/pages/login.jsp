<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container auth-page">
    <div class="auth-box">
        <h2 class="auth-title">Đăng Nhập</h2>

        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>
        <c:if test="${not empty param.msg and param.msg == 'required'}">
            <div class="alert-error">Vui lòng đăng nhập để tiếp tục.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <input type="hidden" name="redirect" value="${param.redirect}">
            <div class="form-group">
                <label>Email *</label>
                <input type="email" name="email" required class="form-input"
                       placeholder="email@example.com" value="${param.email}">
            </div>
            <div class="form-group">
                <label>Mật khẩu *</label>
                <input type="password" name="password" required class="form-input"
                       placeholder="Nhập mật khẩu">
            </div>
            <button type="submit" class="btn-auth">Đăng Nhập</button>
        </form>
        <p class="auth-link">
            <a href="${pageContext.request.contextPath}/forgotPassword">Quên mật khẩu?</a>
        </p>
        <c:if test="${param.msg == 'reset'}">
            <div class="alert-success">Đặt lại mật khẩu thành công. Vui lòng đăng nhập bằng mật khẩu mới.</div>
        </c:if>

        <p class="auth-switch">
            Chưa có tài khoản?
            <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a>
        </p>
    </div>
</div>

<%@ include file="footer.jsp" %>
