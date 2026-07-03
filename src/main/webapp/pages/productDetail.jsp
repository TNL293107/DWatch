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

            <div class="detail-rating">
                <c:choose>
                    <c:when test="${reviewCount > 0}">
                        ⭐ <fmt:formatNumber value="${averageRating}" maxFractionDigits="1"/>/5
                        <span class="reviews-count">(${reviewCount} đánh giá)</span>
                    </c:when>
                    <c:otherwise>
                        <span class="reviews-count">Chưa có đánh giá</span>
                    </c:otherwise>
                </c:choose>
            </div>

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
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
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
                com.dwatch.user.User wlUser = (com.dwatch.user.User) session.getAttribute("loggedUser");
                boolean isWishlisted = false;
                if (wlUser != null) {
                    com.dwatch.wishlist.WishlistDAO wlDAO = new com.dwatch.wishlist.WishlistDAO();
                    isWishlisted = wlDAO.isWishlisted(wlUser.getUserID(),
                        ((com.dwatch.product.Product) request.getAttribute("product")).getProductID());
                }
            %>
            <form action="${pageContext.request.contextPath}/wishlist" method="post" style="margin-bottom:16px">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="productID" value="${product.productID}">
                <input type="hidden" name="action" value="<%= isWishlisted ? "remove" : "add" %>">
                <button type="submit" class="btn-wishlist <%= isWishlisted ? "wishlisted" : "" %>">
                    <%= isWishlisted ? "♥ Đã yêu thích" : "♡ Thêm yêu thích" %>
                </button>
            </form>
            <p style="margin-bottom:16px">
                <a href="${pageContext.request.contextPath}/compare?add=${product.productID}" class="btn-compare">⚖ Thêm vào so sánh</a>
            </p>

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
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
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

<!-- Đánh giá sản phẩm -->
<section class="container reviews-section" id="reviews">
    <h2 class="section-title">Đánh Giá Sản Phẩm</h2>

    <div class="reviews-summary">
        <c:choose>
            <c:when test="${reviewCount > 0}">
                <span class="reviews-avg">⭐ <fmt:formatNumber value="${averageRating}" maxFractionDigits="1"/>/5</span>
                <span class="reviews-count">(${reviewCount} đánh giá)</span>
            </c:when>
            <c:otherwise>
                <span class="reviews-count">Chưa có đánh giá nào cho sản phẩm này.</span>
            </c:otherwise>
        </c:choose>
    </div>

    <c:choose>
        <c:when test="${not empty sessionScope.loggedUser}">
            <form action="${pageContext.request.contextPath}/product" method="post" class="review-form">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="productID" value="${product.productID}">
                <div class="form-group">
                    <label>Đánh giá của bạn *</label>
                    <select name="rating" required class="form-input" style="max-width:200px">
                        <option value="5">5 - Tuyệt vời</option>
                        <option value="4">4 - Tốt</option>
                        <option value="3">3 - Bình thường</option>
                        <option value="2">2 - Không hài lòng</option>
                        <option value="1">1 - Tệ</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Nhận xét</label>
                    <textarea name="comment" rows="3" class="form-input" placeholder="Chia sẻ trải nghiệm của bạn..."></textarea>
                </div>
                <button type="submit" class="btn-primary">Gửi Đánh Giá</button>
            </form>
        </c:when>
        <c:otherwise>
            <p class="review-login-prompt">
                <a href="${pageContext.request.contextPath}/login?redirect=${pageContext.request.contextPath}/product?id=${product.productID}">Đăng nhập</a>
                để viết đánh giá.
            </p>
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${empty reviews}">
            <p class="empty-state-inline">Chưa có đánh giá nào. Hãy là người đầu tiên!</p>
        </c:when>
        <c:otherwise>
            <div class="review-list">
                <c:forEach var="r" items="${reviews}">
                    <div class="review-item">
                        <div class="review-header">
                            <strong><c:out value="${not empty r.authorName ? r.authorName : 'Ẩn danh'}"/></strong>
                            <span class="review-stars">
                                <c:forEach begin="1" end="5" var="i">${i <= r.rating ? '★' : '☆'}</c:forEach>
                            </span>
                            <span class="review-date"><fmt:formatDate value="${r.createdDate}" pattern="dd/MM/yyyy"/></span>
                        </div>
                        <p class="review-comment"><c:out value="${r.comment}"/></p>
                    </div>
                </c:forEach>
            </div>

            <c:if test="${reviewTotalPages > 1}">
                <div class="pagination">
                    <c:if test="${reviewCurrentPage > 1}">
                        <a href="${pageContext.request.contextPath}/product?id=${product.productID}&reviewPage=${reviewCurrentPage - 1}#reviews" class="page-btn">‹</a>
                    </c:if>
                    <c:forEach begin="1" end="${reviewTotalPages}" var="i">
                        <a href="${pageContext.request.contextPath}/product?id=${product.productID}&reviewPage=${i}#reviews"
                           class="page-btn ${i == reviewCurrentPage ? 'active' : ''}">${i}</a>
                    </c:forEach>
                    <c:if test="${reviewCurrentPage < reviewTotalPages}">
                        <a href="${pageContext.request.contextPath}/product?id=${product.productID}&reviewPage=${reviewCurrentPage + 1}#reviews" class="page-btn">›</a>
                    </c:if>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="footer.jsp" %>
