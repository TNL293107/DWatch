<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Sản Phẩm — DWatch Admin</title>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,400;0,500;0,600;0,700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body class="admin-body-panel">

<!-- Admin Header -->
<header class="admin-header">
    <div class="admin-header-inner">
        <span class="admin-logo"><span class="logo-d">D</span><span class="logo-watch">WATCH</span> Admin</span>
        <nav>
            <a href="${pageContext.request.contextPath}/admin/products" class="admin-nav active">Sản Phẩm</a>
            <a href="${pageContext.request.contextPath}/home" class="admin-nav">← Trang Chủ</a>
        </nav>
    </div>
</header>

<div class="admin-content">

    <c:if test="${param.msg == 'saved'}">
        <div class="alert-success">✔ Lưu sản phẩm thành công!</div>
    </c:if>
    <c:if test="${param.msg == 'deleted'}">
        <div class="alert-warning">🗑 Đã xóa sản phẩm.</div>
    </c:if>
    <c:if test="${param.msg == 'error'}">
        <div class="alert-error">✘ Có lỗi xảy ra. Vui lòng thử lại.</div>
    </c:if>

    <!-- Add / Edit Product Form -->
    <div class="admin-panel">
        <h2>${not empty editProduct ? 'Chỉnh Sửa Sản Phẩm' : 'Thêm Sản Phẩm Mới'}</h2>

        <form action="${pageContext.request.contextPath}/admin/products"
              method="post" class="admin-form">

            <c:if test="${not empty editProduct}">
                <input type="hidden" name="productID"    value="${editProduct.productID}">
                <input type="hidden" name="existingImage" value="${editProduct.imageURL}">
            </c:if>

            <div class="admin-form-grid">
                <div class="form-group">
                    <label>Tên sản phẩm *</label>
                    <input type="text" name="productName" required class="form-input"
                           value="${editProduct.productName}">
                </div>
                <div class="form-group">
                    <label>Thương hiệu</label>
                    <input type="text" name="brand" class="form-input"
                           value="${editProduct.brand}">
                </div>
                <div class="form-group">
                    <label>Giá (VNĐ) *</label>
                    <input type="number" name="price" required min="0" step="1000"
                           class="form-input" value="${editProduct.price}">
                </div>
                <div class="form-group">
                    <label>Số lượng tồn kho *</label>
                    <input type="number" name="stock" required min="0"
                           class="form-input" value="${editProduct.stock}">
                </div>
                <div class="form-group">
                    <label>Danh mục *</label>
                    <select name="categoryID" required class="form-input">
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.categoryID}"
                                ${editProduct.categoryID == cat.categoryID ? 'selected' : ''}>
                                ${cat.categoryName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label>Xuất xứ</label>
                    <input type="text" name="origin" class="form-input"
                           value="${editProduct.origin}">
                </div>
                <div class="form-group">
                    <label>Kích thước mặt</label>
                    <input type="text" name="caseSize" class="form-input"
                           placeholder="e.g. 42mm" value="${editProduct.caseSize}">
                </div>
                <div class="form-group">
                    <label>Bộ máy</label>
                    <input type="text" name="movement" class="form-input"
                           placeholder="e.g. Automatic" value="${editProduct.movement}">
                </div>
                <div class="form-group">
                    <label>Kháng nước</label>
                    <input type="text" name="waterResist" class="form-input"
                           placeholder="e.g. 100m" value="${editProduct.waterResist}">
                </div>
                <div class="form-group">
                    <label>URL Hình ảnh</label>
                    <input type="text" name="imageURL" id="imageURLInput" class="form-input"
                        placeholder="https://example.com/watch.jpg"
                        value="${editProduct.imageURL}"
                        oninput="previewImage(this.value)">
                    <small style="color:#888">Dán link ảnh từ internet vào đây</small>
                    <div style="margin-top:8px">
                        <img id="imgPreview"
                             src="${not empty editProduct.imageURL ? editProduct.imageURL : ''}"
                             alt="Preview"
                             style="max-width:120px;max-height:120px;border-radius:8px;border:1px solid #ddd;display:${not empty editProduct.imageURL ? 'block' : 'none'}"
                             onerror="this.style.display='none'">
                    </div>
                </div>
                <div class="form-group form-group-full">
                    <label>Mô tả sản phẩm</label>
                    <textarea name="description" rows="3" class="form-input">${editProduct.description}</textarea>
                </div>
            </div>

            <div class="admin-form-actions">
                <button type="submit" class="btn-order">
                    ${not empty editProduct ? '💾 Lưu Thay Đổi' : '➕ Thêm Sản Phẩm'}
                </button>
                <c:if test="${not empty editProduct}">
                    <a href="${pageContext.request.contextPath}/admin/products" class="btn-outline">Hủy</a>
                </c:if>
            </div>
        </form>
    </div>

    <!-- Product List Table -->
    <div class="admin-panel">
        <h2>Danh Sách Sản Phẩm (${products.size()} sản phẩm)</h2>
        <div class="table-responsive">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Hình</th>
                        <th>Tên sản phẩm</th>
                        <th>Thương hiệu</th>
                        <th>Danh mục</th>
                        <th>Giá</th>
                        <th>Tồn kho</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${products}">
                        <tr>
                            <td>${p.productID}</td>
                            <td>
                                <img src="${not empty p.imageURL ? p.imageURL : ''}"
                                     alt="${p.productName}" class="admin-thumb"
                                     onerror="this.src='${pageContext.request.contextPath}/images/default.svg'">
                            </td>
                            <td>${p.productName}</td>
                            <td>${p.brand}</td>
                            <td>${p.categoryName}</td>
                            <td><fmt:formatNumber value="${p.price}" type="number" groupingUsed="true"/>₫</td>
                            <td>${p.stock}</td>
                            <td class="action-cell">
                                <a href="${pageContext.request.contextPath}/admin/products?action=edit&id=${p.productID}"
                                   class="btn-edit">✏ Sửa</a>
                                <a href="${pageContext.request.contextPath}/admin/products?action=delete&id=${p.productID}"
                                   class="btn-delete"
                                   onclick="return confirm('Xác nhận xóa sản phẩm này?')">🗑 Xóa</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
function previewImage(url) {
    const img = document.getElementById('imgPreview');
    if (url && url.trim() !== '') {
        img.src = url.trim();
        img.style.display = 'block';
        img.onerror = function() { this.style.display = 'none'; };
    } else {
        img.style.display = 'none';
    }
}
</script>
</body>
</html>
