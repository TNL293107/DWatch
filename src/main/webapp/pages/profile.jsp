<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container profile-page">
    <h1 class="page-title">👤 Tài Khoản Của Tôi</h1>

    <div class="profile-layout">
        <!-- Sidebar -->
        <div class="profile-sidebar">
            <div class="profile-avatar">
                <div class="avatar-circle">${loggedUser.fullName.substring(0,1).toUpperCase()}</div>
                <div class="avatar-name">${loggedUser.fullName}</div>
                <div class="avatar-email">${loggedUser.email}</div>
            </div>
            <nav class="profile-nav">
                <a href="${pageContext.request.contextPath}/profile" class="profile-nav-link active">⚙ Thông tin cá nhân</a>
                <a href="${pageContext.request.contextPath}/orders" class="profile-nav-link">📦 Lịch sử đơn hàng</a>
                <a href="${pageContext.request.contextPath}/wishlist" class="profile-nav-link">❤ Yêu thích</a>
                <a href="${pageContext.request.contextPath}/logout" class="profile-nav-link logout">🚪 Đăng xuất</a>
            </nav>
        </div>

        <!-- Main Content -->
        <div class="profile-content">
            <c:if test="${not empty success}"><div class="alert-success">${success}</div></c:if>
            <c:if test="${not empty error}"><div class="alert-error">${error}</div></c:if>

            <!-- Cập nhật thông tin -->
            <div class="profile-panel">
                <h3>Thông Tin Cá Nhân</h3>
                <form action="${pageContext.request.contextPath}/profile" method="post">
                    <input type="hidden" name="action" value="updateProfile">
                    <div class="admin-form-grid">
                        <div class="form-group">
                            <label>Họ và tên</label>
                            <input type="text" name="fullName" class="form-input" value="${loggedUser.fullName}" required>
                        </div>
                        <div class="form-group">
                            <label>Email</label>
                            <input type="text" class="form-input" value="${loggedUser.email}" disabled>
                        </div>
                        <div class="form-group">
                            <label>Số điện thoại</label>
                            <input type="tel" name="phone" class="form-input" value="${loggedUser.phone}">
                        </div>
                        <div class="form-group form-group-full">
                            <label>Địa chỉ mặc định</label>
                            <input type="text" name="address" class="form-input" value="${loggedUser.address}"
                                   placeholder="Địa chỉ giao hàng mặc định">
                        </div>
                    </div>
                    <button type="submit" class="btn-primary">💾 Lưu thay đổi</button>
                </form>
            </div>

            <!-- Đổi mật khẩu -->
            <div class="profile-panel" style="margin-top:24px">
                <h3>Đổi Mật Khẩu</h3>
                <form action="${pageContext.request.contextPath}/profile" method="post">
                    <input type="hidden" name="action" value="changePassword">
                    <div class="admin-form-grid">
                        <div class="form-group">
                            <label>Mật khẩu hiện tại</label>
                            <input type="password" name="oldPassword" class="form-input" required>
                        </div>
                        <div class="form-group">
                            <label>Mật khẩu mới</label>
                            <input type="password" name="newPassword" class="form-input" required minlength="6">
                        </div>
                        <div class="form-group">
                            <label>Xác nhận mật khẩu mới</label>
                            <input type="password" name="newPassword2" class="form-input" required minlength="6">
                        </div>
                    </div>
                    <button type="submit" class="btn-outline">🔒 Đổi mật khẩu</button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
