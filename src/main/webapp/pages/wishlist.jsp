<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jsp" %>

<div class="container profile-page">
    <h1 class="page-title">❤ Sản Phẩm Yêu Thích</h1>
    <div class="profile-layout">
        <div class="profile-sidebar">
            <div class="profile-avatar">
                <div class="avatar-circle">${loggedUser.fullName.substring(0,1).toUpperCase()}</div>
                <div class="avatar-name">${loggedUser.fullName}</div>
            </div>
            <nav class="profile-nav">
                <a href="${pageContext.request.contextPath}/profile" class="profile-nav-link">⚙ Thông tin cá nhân</a>
                <a href="${pageContext.request.contextPath}/orders" class="profile-nav-link">📦 Lịch sử đơn hàng</a>
                <a href="${pageContext.request.contextPath}/wishlist" class="profile-nav-link active">❤ Yêu thích</a>
                <a href="${pageContext.request.contextPath}/logout" class="profile-nav-link logout">🚪 Đăng xuất</a>
            </nav>
        </div>
        <div class="profile-content">
            <c:choose>
                <c:when test="${empty wishlist}">
                    <div class="empty-state">
                        <p>💔 Chưa có sản phẩm yêu thích nào.</p>
                        <a href="${pageContext.request.contextPath}/home" class="btn-primary">Khám phá ngay</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="product-grid">
                        <c:forEach var="p" items="${wishlist}">
                            <div class="product-card">
                                <a href="${pageContext.request.contextPath}/product?id=${p.productID}" class="card-img-link">
                                    <img src="${not empty p.imageURL ? p.imageURL : ''}"
                                         alt="${p.productName}" class="card-img"
                                         onerror="this.src='${pageContext.request.contextPath}/images/default.svg'">
                                    <span class="card-brand">${p.brand}</span>
                                </a>
                                <div class="card-body">
                                    <h3 class="card-name">
                                        <a href="${pageContext.request.contextPath}/product?id=${p.productID}">${p.productName}</a>
                                    </h3>
                                    <div class="card-meta"><span>${p.caseSize}</span><span>${p.movement}</span></div>
                                    <div class="card-footer">
                                        <span class="card-price">
                                            <fmt:formatNumber value="${p.price}" type="number" groupingUsed="true"/>₫
                                        </span>
                                        <form action="${pageContext.request.contextPath}/wishlist" method="post" style="display:inline">
                                            <input type="hidden" name="productID" value="${p.productID}">
                                            <input type="hidden" name="action" value="remove">
                                            <button type="submit" class="btn-remove" title="Xóa khỏi yêu thích">♡</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<%@ include file="footer.jsp" %>
