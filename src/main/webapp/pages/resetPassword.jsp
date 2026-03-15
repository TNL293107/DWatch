<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container auth-page">
    <div class="auth-box">
        <h2 class="auth-title">Đặt Lại Mật Khẩu</h2>

        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>

        <c:if test="${not empty token}">
            <form action="${pageContext.request.contextPath}/resetPassword" method="post">
                <input type="hidden" name="token" value="${token}">
                <div class="form-group">
                    <label>Mật khẩu mới *</label>
                    <input type="password" name="newPassword" required class="form-input"
                           placeholder="Tối thiểu 6 ký tự" minlength="6">
                </div>
                <div class="form-group">
                    <label>Xác nhận mật khẩu *</label>
                    <input type="password" name="newPassword2" required class="form-input"
                           placeholder="Nhập lại mật khẩu" minlength="6">
                </div>
                <button type="submit" class="btn-auth">Đặt Lại Mật Khẩu</button>
            </form>
        </c:if>

        <p class="auth-switch">
            <a href="${pageContext.request.contextPath}/login">← Quay lại đăng nhập</a>
        </p>
    </div>
</div>

<%@ include file="footer.jsp" %>
