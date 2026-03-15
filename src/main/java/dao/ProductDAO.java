package dao;

import model.Product;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ProductDAO {

    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductID(rs.getInt("ProductID"));
        p.setProductName(rs.getString("ProductName"));
        p.setDescription(rs.getString("Description"));
        p.setPrice(rs.getDouble("Price"));
        p.setStock(rs.getInt("Stock"));
        p.setImageURL(rs.getString("ImageURL"));
        p.setBrand(rs.getString("Brand"));
        p.setOrigin(rs.getString("Origin"));
        p.setWaterResist(rs.getString("WaterResist"));
        p.setCaseSize(rs.getString("CaseSize"));
        p.setMovement(rs.getString("Movement"));
        p.setCategoryID(rs.getInt("CategoryID"));
        try { p.setCategoryName(rs.getString("CategoryName")); }
        catch (SQLException ignored) {}
        return p;
    }

    /** Lấy tất cả sản phẩm, sắp xếp mặc định theo giá tăng dần */
    public List<Product> getAllProducts() {
        return getFilteredProducts(null, null, 0, 0, null, null, 1, 12);
    }

    /** Tìm kiếm theo từ khóa */
    public List<Product> searchProducts(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.CategoryName FROM Product p "
                   + "LEFT JOIN Category c ON p.CategoryID = c.CategoryID "
                   + "WHERE p.IsActive = 1 "
                   + "  AND (p.ProductName LIKE ? OR p.Description LIKE ? OR p.Brand LIKE ?) "
                   + "ORDER BY p.Price ASC";
        String kw = "%" + keyword + "%";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /** Lấy sản phẩm theo danh mục */
    public List<Product> getProductsByCategory(int categoryID) {
        return getFilteredProducts(categoryID, null, 0, 0, null, null, 1, 12);
    }

    /**
     * Lọc nâng cao + sắp xếp + phân trang
     * @param categoryID  0 = tất cả
     * @param brand       null = tất cả
     * @param minPrice    0 = không giới hạn
     * @param maxPrice    0 = không giới hạn
     * @param movement    null = tất cả
     * @param waterResist null = tất cả
     * @param page        trang hiện tại (bắt đầu từ 1)
     * @param pageSize    số sản phẩm mỗi trang
     */
    public List<Product> getFilteredProducts(Integer categoryID, String brand,
                                              double minPrice, double maxPrice,
                                              String movement, String waterResist,
                                              int page, int pageSize) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.CategoryName FROM Product p " +
            "LEFT JOIN Category c ON p.CategoryID = c.CategoryID " +
            "WHERE p.IsActive = 1 "
        );
        List<Object> params = new ArrayList<>();

        if (categoryID != null && categoryID > 0) {
            sql.append("AND p.CategoryID = ? ");
            params.add(categoryID);
        }
        if (brand != null && !brand.isEmpty()) {
            sql.append("AND p.Brand = ? ");
            params.add(brand);
        }
        if (minPrice > 0) {
            sql.append("AND p.Price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice > 0) {
            sql.append("AND p.Price <= ? ");
            params.add(maxPrice);
        }
        if (movement != null && !movement.isEmpty()) {
            sql.append("AND p.Movement LIKE ? ");
            params.add("%" + movement + "%");
        }
        if (waterResist != null && !waterResist.isEmpty()) {
            sql.append("AND p.WaterResist = ? ");
            params.add(waterResist);
        }

        // Phân trang
        int offset = (page - 1) * pageSize;
        sql.append("ORDER BY p.Price ASC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(pageSize);

        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lọc nâng cao + sắp xếp tùy chọn + phân trang
     * @param sortBy  "price_asc" | "price_desc" | "newest" | "bestseller"
     */
    public List<Product> getFilteredProductsSorted(Integer categoryID, String brand,
                                                    double minPrice, double maxPrice,
                                                    String movement, String waterResist,
                                                    String sortBy, int page, int pageSize) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.CategoryName FROM Product p " +
            "LEFT JOIN Category c ON p.CategoryID = c.CategoryID "
        );

        // Join thêm bảng OrderDetail nếu sort theo bestseller
        if ("bestseller".equals(sortBy)) {
            sql = new StringBuilder(
                "SELECT p.*, c.CategoryName, COALESCE(SUM(od.Quantity),0) AS totalSold " +
                "FROM Product p " +
                "LEFT JOIN Category c ON p.CategoryID = c.CategoryID " +
                "LEFT JOIN OrderDetail od ON p.ProductID = od.ProductID "
            );
        }

        sql.append("WHERE p.IsActive = 1 ");
        List<Object> params = new ArrayList<>();

        if (categoryID != null && categoryID > 0) {
            sql.append("AND p.CategoryID = ? "); params.add(categoryID);
        }
        if (brand != null && !brand.isEmpty()) {
            sql.append("AND p.Brand = ? "); params.add(brand);
        }
        if (minPrice > 0) {
            sql.append("AND p.Price >= ? "); params.add(minPrice);
        }
        if (maxPrice > 0) {
            sql.append("AND p.Price <= ? "); params.add(maxPrice);
        }
        if (movement != null && !movement.isEmpty()) {
            sql.append("AND p.Movement LIKE ? "); params.add("%" + movement + "%");
        }
        if (waterResist != null && !waterResist.isEmpty()) {
            sql.append("AND p.WaterResist = ? "); params.add(waterResist);
        }

        if ("bestseller".equals(sortBy)) {
            sql.append("GROUP BY p.ProductID, p.ProductName, p.Description, p.Price, p.Stock, " +
                       "p.ImageURL, p.Brand, p.Origin, p.WaterResist, p.CaseSize, p.Movement, " +
                       "p.CategoryID, p.IsActive, p.CreatedDate, c.CategoryName ");
            sql.append("ORDER BY totalSold DESC ");
        } else if ("price_desc".equals(sortBy)) {
            sql.append("ORDER BY p.Price DESC ");
        } else if ("newest".equals(sortBy)) {
            sql.append("ORDER BY p.CreatedDate DESC ");
        } else {
            sql.append("ORDER BY p.Price ASC ");
        }

        int offset = (page - 1) * pageSize;
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset); params.add(pageSize);

        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /** Đếm tổng số sản phẩm (dùng cho phân trang) */
    public int countFilteredProducts(Integer categoryID, String brand,
                                     double minPrice, double maxPrice,
                                     String movement, String waterResist) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM Product p WHERE p.IsActive = 1 "
        );
        List<Object> params = new ArrayList<>();

        if (categoryID != null && categoryID > 0) {
            sql.append("AND p.CategoryID = ? "); params.add(categoryID);
        }
        if (brand != null && !brand.isEmpty()) {
            sql.append("AND p.Brand = ? "); params.add(brand);
        }
        if (minPrice > 0) {
            sql.append("AND p.Price >= ? "); params.add(minPrice);
        }
        if (maxPrice > 0) {
            sql.append("AND p.Price <= ? "); params.add(maxPrice);
        }
        if (movement != null && !movement.isEmpty()) {
            sql.append("AND p.Movement LIKE ? "); params.add("%" + movement + "%");
        }
        if (waterResist != null && !waterResist.isEmpty()) {
            sql.append("AND p.WaterResist = ? "); params.add(waterResist);
        }

        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Lấy danh sách brand riêng biệt (cho filter) */
    public List<String> getAllBrands() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT Brand FROM Product WHERE IsActive = 1 AND Brand IS NOT NULL ORDER BY Brand";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Lấy danh sách Movement riêng biệt (cho filter) */
    public List<String> getAllMovements() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT Movement FROM Product WHERE IsActive = 1 AND Movement IS NOT NULL ORDER BY Movement";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Lấy danh sách WaterResist riêng biệt (cho filter) */
    public List<String> getAllWaterResists() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT WaterResist FROM Product WHERE IsActive = 1 AND WaterResist IS NOT NULL ORDER BY WaterResist";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Sản phẩm liên quan (cùng danh mục, trừ sản phẩm hiện tại) */
    public List<Product> getRelatedProducts(int categoryID, int excludeProductID, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT TOP (?) p.*, c.CategoryName FROM Product p "
                   + "LEFT JOIN Category c ON p.CategoryID = c.CategoryID "
                   + "WHERE p.IsActive = 1 AND p.CategoryID = ? AND p.ProductID != ? "
                   + "ORDER BY NEWID()";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, categoryID);
            ps.setInt(3, excludeProductID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Lấy sản phẩm theo ID */
    public Product getProductByID(int id) {
        String sql = "SELECT p.*, c.CategoryName FROM Product p "
                   + "LEFT JOIN Category c ON p.CategoryID = c.CategoryID "
                   + "WHERE p.ProductID = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Lấy nhiều sản phẩm theo danh sách ID (cho trang so sánh) */
    public List<Product> getProductsByIDs(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.CategoryName FROM Product p "
                   + "LEFT JOIN Category c ON p.CategoryID = c.CategoryID "
                   + "WHERE p.ProductID = ? AND (p.IsActive = 1 OR p.IsActive IS NULL)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (Integer id : ids) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) list.add(map(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insertProduct(Product p) {
        String sql = "INSERT INTO Product (ProductName, Description, Price, Stock, "
                   + "ImageURL, Brand, Origin, WaterResist, CaseSize, Movement, CategoryID) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName()); ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());       ps.setInt(4, p.getStock());
            ps.setString(5, p.getImageURL());    ps.setString(6, p.getBrand());
            ps.setString(7, p.getOrigin());      ps.setString(8, p.getWaterResist());
            ps.setString(9, p.getCaseSize());    ps.setString(10, p.getMovement());
            ps.setInt(11, p.getCategoryID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateProduct(Product p) {
        String sql = "UPDATE Product SET ProductName=?, Description=?, Price=?, Stock=?, "
                   + "ImageURL=?, Brand=?, Origin=?, WaterResist=?, CaseSize=?, "
                   + "Movement=?, CategoryID=? WHERE ProductID=?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName()); ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());       ps.setInt(4, p.getStock());
            ps.setString(5, p.getImageURL());    ps.setString(6, p.getBrand());
            ps.setString(7, p.getOrigin());      ps.setString(8, p.getWaterResist());
            ps.setString(9, p.getCaseSize());    ps.setString(10, p.getMovement());
            ps.setInt(11, p.getCategoryID());    ps.setInt(12, p.getProductID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteProduct(int id) {
        String sql = "UPDATE Product SET IsActive = 0 WHERE ProductID = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
