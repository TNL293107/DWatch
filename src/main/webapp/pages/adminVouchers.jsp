<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Mã Giảm Giá — DWatch Admin</title>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,400;0,500;0,600;0,700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body class="admin-body-panel">

<header class="admin-header">
    <div class="admin-header-inner">
        <span class="admin-logo"><span class="logo-d">D</span><span class="logo-watch">WATCH</span> Admin</span>
        <nav>
            <a href="${pageContext.request.contextPath}/admin/products" class="admin-nav">Sản Phẩm</a>
            <a href="${pageContext.request.contextPath}/admin/orders" class="admin-nav">Đơn Hàng</a>
            <a href="${pageContext.request.contextPath}/admin/vouchers" class="admin-nav active">Mã Giảm Giá</a>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="admin-nav">Thống Kê</a>
            <a href="${pageContext.request.contextPath}/home" class="admin-nav">← Trang Chủ</a>
        </nav>
    </div>
</header>

<div class="admin-content">

    <c:if test="${param.msg == 'saved'}">
        <div class="alert-success">✔ Lưu mã giảm giá thành công!</div>
    </c:if>
    <c:if test="${param.msg == 'deactivated'}">
        <div class="alert-warning">🗑 Đã vô hiệu hóa mã giảm giá.</div>
    </c:if>
    <c:if test="${param.msg == 'error'}">
        <div class="alert-error">✘ Có lỗi xảy ra. Vui lòng thử lại.</div>
    </c:if>

    <div class="admin-panel">
        <h2>${not empty editVoucher ? 'Chỉnh Sửa Mã Giảm Giá' : 'Thêm Mã Giảm Giá Mới'}</h2>

        <form action="${pageContext.request.contextPath}/admin/vouchers" method="post" class="admin-form">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <c:if test="${not empty editVoucher}">
                <input type="hidden" name="voucherID" value="${editVoucher.voucherID}">
            </c:if>

            <div class="admin-form-grid">
                <div class="form-group">
                    <label>Mã giảm giá *</label>
                    <input type="text" name="code" required class="form-input"
                           placeholder="VD: SALE10" value="${editVoucher.code}">
                </div>
                <div class="form-group">
                    <label>Loại giảm giá *</label>
                    <select name="discountType" required class="form-input">
                        <option value="percent" ${editVoucher.discountType == 'percent' ? 'selected' : ''}>Phần trăm (%)</option>
                        <option value="fixed" ${editVoucher.discountType == 'fixed' ? 'selected' : ''}>Số tiền cố định (₫)</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Giá trị giảm *</label>
                    <input type="number" name="discountValue" required min="0" step="0.01"
                           class="form-input" value="${editVoucher.discountValue}">
                </div>
                <div class="form-group">
                    <label>Đơn hàng tối thiểu (₫)</label>
                    <input type="number" name="minOrderAmount" min="0" step="1000"
                           class="form-input" value="${editVoucher.minOrderAmount}">
                </div>
                <div class="form-group">
                    <label>Số lượt sử dụng tối đa</label>
                    <input type="number" name="maxUses" min="1"
                           class="form-input" value="${editVoucher.maxUses}">
                </div>
                <div class="form-group">
                    <label>Ngày hết hạn</label>
                    <input type="date" name="expiryDate" class="form-input"
                           value="<fmt:formatDate value='${editVoucher.expiryDate}' pattern='yyyy-MM-dd'/>">
                </div>
            </div>

            <div class="admin-form-actions">
                <button type="submit" class="btn-order">
                    ${not empty editVoucher ? '💾 Lưu Thay Đổi' : '➕ Thêm Mã'}
                </button>
                <c:if test="${not empty editVoucher}">
                    <a href="${pageContext.request.contextPath}/admin/vouchers" class="btn-outline">Hủy</a>
                </c:if>
            </div>
        </form>
    </div>

    <div class="admin-panel">
        <h2>Danh Sách Mã Giảm Giá (${vouchers.size()} mã)</h2>
        <div class="table-responsive">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Mã</th>
                        <th>Loại</th>
                        <th>Giá trị</th>
                        <th>Đơn tối thiểu</th>
                        <th>Đã dùng</th>
                        <th>Hết hạn</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="v" items="${vouchers}">
                        <tr>
                            <td><strong>${v.code}</strong></td>
                            <td>${v.discountType == 'percent' ? 'Phần trăm' : 'Cố định'}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${v.discountType == 'percent'}">${v.discountValue}%</c:when>
                                    <c:otherwise><fmt:formatNumber value="${v.discountValue}" type="number" groupingUsed="true"/>₫</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${not empty v.minOrderAmount ? v.minOrderAmount : '—'}</td>
                            <td>${v.usedCount}${not empty v.maxUses ? '/'.concat(v.maxUses) : ''}</td>
                            <td>${not empty v.expiryDate ? v.expiryDate : 'Không giới hạn'}</td>
                            <td>${v.active ? 'Đang hoạt động' : 'Đã tắt'}</td>
                            <td class="action-cell">
                                <a href="${pageContext.request.contextPath}/admin/vouchers?action=edit&id=${v.voucherID}"
                                   class="btn-edit">✏ Sửa</a>
                                <c:if test="${v.active}">
                                    <a href="${pageContext.request.contextPath}/admin/vouchers?action=deactivate&id=${v.voucherID}"
                                       class="btn-delete" onclick="return confirm('Vô hiệu hóa mã này?')">🗑 Tắt</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>
