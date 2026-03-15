package dao;

import model.Product;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    /** Thêm sản phẩm vào wishlist. Trả về true nếu thành công */
    public boolean add(int userID, int productID) {
        String sql = "IF NOT EXISTS (SELECT 1 FROM Wishlist WHERE UserID=? AND ProductID=?) " +
                     "INSERT INTO Wishlist (UserID, ProductID) VALUES (?,?)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, userID); ps.setInt(2, productID);
            ps.setInt(3, userID); ps.setInt(4, productID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Xóa sản phẩm khỏi wishlist */
    public boolean remove(int userID, int productID) {
        String sql = "DELETE FROM Wishlist WHERE UserID=? AND ProductID=?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, userID); ps.setInt(2, productID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Lấy danh sách sản phẩm trong wishlist */
    public List<Product> getByUser(int userID) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.* FROM Product p JOIN Wishlist w ON p.ProductID = w.ProductID " +
                     "WHERE w.UserID = ? ORDER BY w.AddedDate DESC";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setProductID(rs.getInt("ProductID"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setPrice(rs.getDouble("Price"));
                    p.setImageURL(rs.getString("ImageURL"));
                    p.setBrand(rs.getString("Brand"));
                    p.setCaseSize(rs.getString("CaseSize"));
                    p.setMovement(rs.getString("Movement"));
                    p.setStock(rs.getInt("Stock"));
                    list.add(p);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Kiểm tra sản phẩm có trong wishlist không */
    public boolean isWishlisted(int userID, int productID) {
        String sql = "SELECT 1 FROM Wishlist WHERE UserID=? AND ProductID=?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, userID); ps.setInt(2, productID);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
