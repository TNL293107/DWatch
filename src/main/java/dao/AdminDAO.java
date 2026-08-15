package dao;

import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AdminDAO — truy cập dữ liệu bảng Admin.
 *
 * <p>Chỉ đọc/ghi dữ liệu; việc xác thực mật khẩu do {@code service.AdminAuthService} đảm nhiệm.
 */
public class AdminDAO {

    /** Lấy hash mật khẩu của admin theo username; null nếu không tồn tại. */
    public String findPasswordHash(String username) {
        String sql = "SELECT Password FROM Admin WHERE Username = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("Password");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Ghi đè hash mật khẩu của admin (dùng khi migrate bản ghi plaintext cũ). */
    public boolean updatePasswordHash(String username, String passwordHash) {
        String sql = "UPDATE Admin SET Password = ? WHERE Username = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
