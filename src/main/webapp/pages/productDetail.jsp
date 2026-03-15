<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="header.jsp" %>

<div class="container detail-page">

    <!-- Breadcrumb -->
    <nav class="breadcrumb">
        <a href="${pageContext.request.contextPath}/home">Trang chủ</a> ›
        <a href="${pageContext.request.contextPath}/home?cat=${product.categoryID}">${product.categoryName}</a> ›
        <span>${product.productName}</span>
    </nav>

    <div class="detail-layout">

        <!-- Product Image -->
        <div class="detail-image-col">
            <div class="detail-img-wrap">
                <img src="${not empty product.imageURL ? product.imageURL : ''}"
                     alt="${product.productName}"
                     class="card-img"
                     onerror="this.src='${pageContext.request.contextPath}/images/default.svg'">
            </div>
        </div>

        <!-- Product Info -->
        <div class="detail-info-col">
            <span class="detail-brand">${product.brand}</span>
            <h1 class="detail-name">${product.productName}</h1>

            <div class="detail-price">
                <fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/>₫
            </div>

            <p class="detail-desc">${product.description}</p>

            <!-- Specs Table -->
            <table class="spec-table">
                <tr><td>Thương hiệu</td><td><strong>${product.brand}</strong></td></tr>
                <tr><td>Xuất xứ</td>    <td><strong>${product.origin}</strong></td></tr>
                <tr><td>Đường kính</td> <td><strong>${product.caseSize}</strong></td></tr>
                <tr><td>Bộ máy</td>     <td><strong>${product.movement}</strong></td></tr>
                <tr><td>Kháng nước</td> <td><strong>${product.waterResist}</strong></td></tr>
                <tr><td>Danh mục</td>   <td><strong>${product.categoryName}</strong></td></tr>
                <tr><td>Tình trạng</td>
                    <td>
                        <c:choose>
                            <c:when test="${product.stock > 0}">
                                <span class="badge-instock">✔ Còn hàng (${product.stock} cái)</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge-outstock">✘ Hết hàng</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>

            <!-- Add to Cart Form -->
            <form action="${pageContext.request.contextPath}/cart" method="post" class="atc-form">
                <input type="hidden" name="action"    value="add">
                <input type="hidden" name="productID" value="${product.productID}">
                <div class="qty-row">
                    <label>Số lượng:</label>
                    <div class="qty-ctrl">
                        <button type="button" onclick="changeQty(-1)">−</button>
                        <input type="number" name="quantity" id="qtyInput"
                               value="1" min="1" max="${product.stock}" class="qty-input">
                        <button type="button" onclick="changeQty(1)">+</button>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${product.stock > 0}">
                        <button type="submit" class="btn-atc-large">🛒 Thêm Vào Giỏ Hàng</button>
                    </c:when>
                    <c:otherwise>
                        <button type="button" class="btn-atc-large disabled" disabled>Hết Hàng</button>
                    </c:otherwise>
                </c:choose>
            </form>

            <!-- Wishlist Button -->
            <%
                model.User wlUser = (model.User) session.getAttribute("loggedUser");
                boolean isWishlisted = false;
                if (wlUser != null) {
                    dao.WishlistDAO wlDAO = new dao.WishlistDAO();
                    isWishlisted = wlDAO.isWishlisted(wlUser.getUserID(),
                        ((model.Product) request.getAttribute("product")).getProductID());
                }
            %>
            <form action="${pageContext.request.contextPath}/wishlist" method="post" style="margin-bottom:16px">
                <input type="hidden" name="productID" value="${product.productID}">
                <input type="hidden" name="action" value="<%= isWishlisted ? "remove" : "add" %>">
                <button type="submit" class="btn-wishlist <%= isWishlisted ? "wishlisted" : "" %>">
                    <%= isWishlisted ? "♥ Đã yêu thích" : "♡ Thêm yêu thích" %>
                </button>
            </form>

            <!-- Trust Badges -->
            <div class="trust-row">
                <span>✔ Chính hãng 100%</span>
                <span>🔄 Đổi trả 30 ngày</span>
                <span>🛡 Bảo hành 12 tháng</span>
            </div>
        </div>
    </div>
</div>

<script>
function changeQty(delta) {
    const input = document.getElementById('qtyInput');
    const max   = parseInt(input.max) || 99;
    let val = parseInt(input.value) + delta;
    if (val < 1)   val = 1;
    if (val > max) val = max;
    input.value = val;
}
</script>

<!-- Sản phẩm liên quan -->
<c:if test="${not empty relatedProducts}">
<section class="container related-section">
    <h2 class="section-title">Sản Phẩm Liên Quan</h2>
    <div class="product-grid">
        <c:forEach var="p" items="${relatedProducts}">
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
</section>
</c:if>

<%@ include file="footer.jsp" %>
