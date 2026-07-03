package com.dwatch.review;

import com.dwatch.common.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public boolean addReview(Review r) {
        if (r.getRating() < 1 || r.getRating() > 5) return false;
        String sql = "INSERT INTO Review (ProductID, UserID, AuthorName, Rating, Comment) VALUES (?,?,?,?,?)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, r.getProductID());
            ps.setObject(2, r.getUserID());
            ps.setString(3, r.getAuthorName());
            ps.setInt(4, r.getRating());
            ps.setString(5, r.getComment());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Đánh giá của một sản phẩm, phân trang (mới nhất trước), chỉ lấy review đã duyệt */
    public List<Review> getReviewsByProduct(int productID, int page, int pageSize) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM Review WHERE ProductID = ? AND IsApproved = 1 "
                   + "ORDER BY CreatedDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        int offset = (Math.max(1, page) - 1) * pageSize;
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int countReviewsByProduct(int productID) {
        String sql = "SELECT COUNT(*) FROM Review WHERE ProductID = ? AND IsApproved = 1";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Điểm đánh giá trung bình (0 nếu chưa có review nào) */
    public double getAverageRating(int productID) {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) FROM Review WHERE ProductID = ? AND IsApproved = 1";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Review map(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setReviewID(rs.getInt("ReviewID"));
        r.setProductID(rs.getInt("ProductID"));
        Object userID = rs.getObject("UserID");
        r.setUserID(userID != null ? rs.getInt("UserID") : null);
        r.setAuthorName(rs.getString("AuthorName"));
        r.setRating(rs.getInt("Rating"));
        r.setComment(rs.getString("Comment"));
        r.setCreatedDate(rs.getTimestamp("CreatedDate"));
        return r;
    }
}
