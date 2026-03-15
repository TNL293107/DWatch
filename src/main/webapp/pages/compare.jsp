<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jsp" %>

<div class="container compare-page">
    <h1 class="page-title">⚖ So Sánh Sản Phẩm</h1>

    <c:choose>
        <c:when test="${empty compareProducts}">
            <div class="compare-empty">
                <p>Chưa có sản phẩm nào để so sánh. Bạn có thể chọn tối đa 3 sản phẩm từ trang chi tiết (nút "Thêm vào so sánh").</p>
                <a href="${pageContext.request.contextPath}/home" class="btn-primary">Xem sản phẩm</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="compare-actions">
                <a href="${pageContext.request.contextPath}/home" class="btn-outline">← Thêm sản phẩm khác</a>
            </div>
            <div class="compare-table-wrap">
                <table class="compare-table">
                    <thead>
                        <tr>
                            <th class="compare-label">Tiêu chí</th>
                            <c:forEach var="p" items="${compareProducts}">
                                <th class="compare-product-header">
                                    <a href="${pageContext.request.contextPath}/compare?remove=${p.productID}" class="compare-remove" title="Bỏ khỏi so sánh">✕</a>
                                    <a href="${pageContext.request.contextPath}/product?id=${p.productID}" class="compare-product-link">
                                        <img src="${not empty p.imageURL ? p.imageURL : ''}" alt="${p.productName}" class="compare-thumb"
                                             onerror="this.src='${pageContext.request.contextPath}/images/default.svg'">
                                        <span class="compare-name">${p.productName}</span>
                                    </a>
                                </th>
                            </c:forEach>
                        </tr>
                    </thead>
                    <tbody>
                        <tr><td class="compare-label">Giá</td>
                            <c:forEach var="p" items="${compareProducts}">
                                <td class="compare-value compare-price"><fmt:formatNumber value="${p.price}" type="number" groupingUsed="true"/>₫</td>
                            </c:forEach>
                        </tr>
                        <tr><td class="compare-label">Thương hiệu</td>
                            <c:forEach var="p" items="${compareProducts}">
                                <td class="compare-value">${p.brand}</td>
                            </c:forEach>
                        </tr>
                        <tr><td class="compare-label">Xuất xứ</td>
                            <c:forEach var="p" items="${compareProducts}">
                                <td class="compare-value">${p.origin}</td>
                            </c:forEach>
                        </tr>
                        <tr><td class="compare-label">Đường kính mặt</td>
                            <c:forEach var="p" items="${compareProducts}">
                                <td class="compare-value">${p.caseSize}</td>
                            </c:forEach>
                        </tr>
                        <tr><td class="compare-label">Bộ máy</td>
                            <c:forEach var="p" items="${compareProducts}">
                                <td class="compare-value">${p.movement}</td>
                            </c:forEach>
                        </tr>
                        <tr><td class="compare-label">Kháng nước</td>
                            <c:forEach var="p" items="${compareProducts}">
                                <td class="compare-value">${p.waterResist}</td>
                            </c:forEach>
                        </tr>
                        <tr><td class="compare-label">Danh mục</td>
                            <c:forEach var="p" items="${compareProducts}">
                                <td class="compare-value">${p.categoryName}</td>
                            </c:forEach>
                        </tr>
                        <tr><td class="compare-label">Tình trạng</td>
                            <c:forEach var="p" items="${compareProducts}">
                                <td class="compare-value">
                                    <c:choose>
                                        <c:when test="${p.stock > 0}">Còn hàng (${p.stock})</c:when>
                                        <c:otherwise>Hết hàng</c:otherwise>
                                    </c:choose>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr><td class="compare-label">Thao tác</td>
                            <c:forEach var="p" items="${compareProducts}">
                                <td class="compare-value">
                                    <a href="${pageContext.request.contextPath}/product?id=${p.productID}" class="btn-outline btn-sm">Xem chi tiết</a>
                                    <form action="${pageContext.request.contextPath}/cart" method="post" style="display:inline;margin-left:8px">
                                        <input type="hidden" name="action" value="add">
                                        <input type="hidden" name="productID" value="${p.productID}">
                                        <input type="hidden" name="quantity" value="1">
                                        <button type="submit" class="btn-primary btn-sm" ${p.stock <= 0 ? 'disabled' : ''}>Thêm vào giỏ</button>
                                    </form>
                                </td>
                            </c:forEach>
                        </tr>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="footer.jsp" %>
