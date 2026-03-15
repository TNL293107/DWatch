<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="header.jsp" %>

<div class="container cart-page">
    <h1 class="page-title">🛒 Giỏ Hàng Của Bạn</h1>

    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty cart}">
            <div class="empty-cart">
                <p>Giỏ hàng của bạn đang trống.</p>
                <a href="${pageContext.request.contextPath}/home" class="btn-primary">Tiếp Tục Mua Sắm</a>
            </div>
        </c:when>
        <c:otherwise>

            <div class="cart-layout">
                <!-- Cart Items Table -->
                <div class="cart-items-col">
                    <form action="${pageContext.request.contextPath}/cart" method="post" id="cartForm">
                        <input type="hidden" name="action" value="update">
                        <table class="cart-table">
                            <thead>
                                <tr>
                                    <th>Sản phẩm</th>
                                    <th>Đơn giá</th>
                                    <th>Số lượng</th>
                                    <th>Thành tiền</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:set var="total" value="0"/>
                                <c:forEach var="entry" items="${cart}">
                                    <c:set var="item" value="${entry.value}"/>
                                    <c:set var="total" value="${total + item.subtotal}"/>
                                    <tr>
                                        <td class="cart-product-cell">
                                            <input type="hidden" name="productID" value="${item.product.productID}">
                                            <img src="${not empty item.product.imageURL ? item.product.imageURL : ''}"
                                                 alt="${item.product.productName}" class="cart-thumb"
                                                 onerror="this.src='${pageContext.request.contextPath}/images/default.svg'">
                                            <div>
                                                <a href="${pageContext.request.contextPath}/product?id=${item.product.productID}"
                                                   class="cart-item-name">${item.product.productName}</a>
                                                <br><small>${item.product.brand}</small>
                                            </div>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${item.product.price}" type="number" groupingUsed="true"/>₫
                                        </td>
                                        <td>
                                            <input type="number" name="qty" value="${item.quantity}"
                                                   min="1" max="99" class="qty-box"
                                                   onchange="document.getElementById('cartForm').submit()">
                                        </td>
                                        <td class="subtotal-cell">
                                            <fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true"/>₫
                                        </td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/cart" method="post"
                                                  style="display:inline">
                                                <input type="hidden" name="action"    value="remove">
                                                <input type="hidden" name="productID" value="${item.product.productID}">
                                                <button type="submit" class="btn-remove" title="Xóa">✕</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <div class="cart-actions">
                            <a href="${pageContext.request.contextPath}/home" class="btn-outline">← Tiếp Tục Mua</a>
                            <button type="submit" class="btn-secondary">🔄 Cập Nhật Giỏ</button>
                        </div>
                    </form>
                </div>

                <!-- Order Summary + Delivery Form -->
                <div class="cart-summary-col">
                    <div class="order-summary">
                        <h3>Tóm Tắt Đơn Hàng</h3>
                        <div class="summary-row">
                            <span>Tạm tính:</span>
                            <strong>
                                <fmt:formatNumber value="${total}" type="number" groupingUsed="true"/>₫
                            </strong>
                        </div>
                        <div class="summary-row">
                            <span>Phí vận chuyển:</span>
                            <strong>Miễn phí</strong>
                        </div>
                        <div class="summary-row total-row">
                            <span>Tổng cộng:</span>
                            <strong class="grand-total">
                                <fmt:formatNumber value="${total}" type="number" groupingUsed="true"/>₫
                            </strong>
                        </div>
                    </div>

                    <!-- Delivery Information Form -->
                    <div class="delivery-form">
                        <h3>Thông Tin Giao Hàng</h3>
                        <form action="${pageContext.request.contextPath}/cart" method="post" id="checkoutForm">
                            <input type="hidden" name="action" value="checkout">

                            <div class="form-group">
                                <label>Họ và tên *</label>
                                <input type="text" name="fullName" required
                                       placeholder="Nguyễn Văn A" class="form-input">
                            </div>
                            <div class="form-group">
                                <label>Số điện thoại *</label>
                                <input type="tel" name="phone" required
                                       placeholder="0901 234 567" class="form-input">
                            </div>
                            <div class="form-group">
                                <label>Email</label>
                                <input type="email" name="email"
                                       placeholder="email@example.com" class="form-input">
                            </div>
                            <div class="form-group">
                                <label>Địa chỉ giao hàng *</label>
                                <textarea name="address" required rows="2"
                                          placeholder="Số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành"
                                          class="form-input"></textarea>
                            </div>
                            <div class="form-group">
                                <label>Ghi chú</label>
                                <textarea name="note" rows="2"
                                          placeholder="Ghi chú thêm (nếu có)..."
                                          class="form-input"></textarea>
                            </div>
                            <button type="submit" class="btn-order">
                                ✔ Đặt Hàng Ngay
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="footer.jsp" %>
