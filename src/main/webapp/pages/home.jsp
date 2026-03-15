<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="header.jsp" %>

<!-- Hero Banner -->
<section class="hero">
    <div class="hero-content">
        <p class="hero-sub">BỘ SƯU TẬP MỚI 2025</p>
        <h1 class="hero-title">Đồng Hồ Chính Hãng<br><span>Phong Cách Đẳng Cấp</span></h1>
        <a href="#products" class="hero-btn">Khám Phá Ngay</a>
    </div>
</section>

<!-- Category Pills -->
<div class="container">
    <div class="cat-pills">
        <a href="${pageContext.request.contextPath}/home"
           class="cat-pill ${empty param.cat && empty keyword ? 'active' : ''}">Tất Cả</a>
        <c:forEach var="cat" items="${categories}">
            <a href="${pageContext.request.contextPath}/home?cat=${cat.categoryID}"
               class="cat-pill ${selectedCat == cat.categoryID ? 'active' : ''}">
                ${cat.categoryName}
            </a>
        </c:forEach>
    </div>
</div>

<!-- Search Result Info -->
<c:if test="${not empty keyword}">
    <div class="container search-info">
        🔍 Kết quả cho: <strong>"${keyword}"</strong>
        — <strong>${totalProducts}</strong> sản phẩm
        <a href="${pageContext.request.contextPath}/home" class="clear-search">✕ Xóa</a>
    </div>
</c:if>

<!-- Products Section -->
<section id="products" class="container products-section">

    <!-- Sort Bar -->
    <div class="sort-bar">
        <span class="sort-label">
            <c:choose>
                <c:when test="${not empty keyword}">Tìm thấy <strong>${totalProducts}</strong> sản phẩm</c:when>
                <c:otherwise>Hiển thị <strong>${products.size()}</strong> / <strong>${totalProducts}</strong> sản phẩm</c:otherwise>
            </c:choose>
        </span>
        <c:set var="sortBase" value="?${not empty param.cat ? 'cat='.concat(param.cat).concat('&') : ''}"/>
        <div class="sort-options">
            <span>Sắp xếp:</span>
            <a href="${pageContext.request.contextPath}/home${sortBase}sort=price_asc"
               class="sort-btn ${sortBy == 'price_asc' || empty sortBy ? 'active' : ''}">Giá tăng</a>
            <a href="${pageContext.request.contextPath}/home${sortBase}sort=price_desc"
               class="sort-btn ${sortBy == 'price_desc' ? 'active' : ''}">Giá giảm</a>
            <a href="${pageContext.request.contextPath}/home${sortBase}sort=newest"
               class="sort-btn ${sortBy == 'newest' ? 'active' : ''}">Mới nhất</a>
            <a href="${pageContext.request.contextPath}/home${sortBase}sort=bestseller"
               class="sort-btn ${sortBy == 'bestseller' ? 'active' : ''}">Bán chạy</a>
        </div>
    </div>

    <!-- Product Grid -->
    <c:choose>
        <c:when test="${empty products}">
            <div class="empty-state">
                <p>😕 Không tìm thấy sản phẩm phù hợp.</p>
                <a href="${pageContext.request.contextPath}/home" class="btn-primary">Xem tất cả</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="product-grid">
                <c:forEach var="p" items="${products}">
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
                            <div class="card-meta">
                                <span>${p.caseSize}</span>
                                <span>${p.movement}</span>
                            </div>
                            <div class="card-footer">
                                <span class="card-price">
                                    <fmt:formatNumber value="${p.price}" type="number" groupingUsed="true"/>₫
                                </span>
                                <form action="${pageContext.request.contextPath}/cart" method="post" style="display:inline">
                                    <input type="hidden" name="action"    value="add">
                                    <input type="hidden" name="productID" value="${p.productID}">
                                    <input type="hidden" name="quantity"  value="1">
                                    <button type="submit" class="btn-add-cart">+ Giỏ hàng</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Phân trang -->
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:set var="pageBase" value="${sortBase}sort=${sortBy}&"/>
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/home${pageBase}page=${currentPage - 1}" class="page-btn">‹</a>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="${pageContext.request.contextPath}/home${pageBase}page=${i}"
                           class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/home${pageBase}page=${currentPage + 1}" class="page-btn">›</a>
                    </c:if>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</section>

<!-- Brand Logos Bar -->
<section class="brands-bar">
    <div class="container">
        <div class="brands-list">
            <a href="${pageContext.request.contextPath}/home?keyword=Casio"   class="brand-item">CASIO</a>
            <a href="${pageContext.request.contextPath}/home?keyword=Seiko"   class="brand-item">SEIKO</a>
            <a href="${pageContext.request.contextPath}/home?keyword=Citizen" class="brand-item">CITIZEN</a>
            <a href="${pageContext.request.contextPath}/home?keyword=Orient"  class="brand-item">ORIENT</a>
            <a href="${pageContext.request.contextPath}/home?keyword=Samsung" class="brand-item">SAMSUNG</a>
            <a href="${pageContext.request.contextPath}/home?keyword=Garmin"  class="brand-item">GARMIN</a>
        </div>
    </div>
</section>

<%@ include file="footer.jsp" %>
