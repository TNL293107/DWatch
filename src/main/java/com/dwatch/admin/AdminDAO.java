package com.dwatch.admin;

import com.dwatch.common.DBUtil;
import com.dwatch.common.PasswordUtil;

import java.sql.*;

public class AdminDAO {

    /**
     * Xác thực tài khoản admin. Trả về true nếu username/password khớp.
     * Chấp nhận mật khẩu cũ dạng plaintext (seed mặc định trong database.sql)
     * và tự động băm lại ngay khi xác thực thành công.
     */
    public boolean authenticate(String username, String password) {
        String sql = "SELECT Password FROM Admin WHERE Username = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String storedHash = rs.getString("Password");
                if (!PasswordUtil.verify(password, storedHash)) return false;
                if (PasswordUtil.isLegacyPlaintext(storedHash)) {
                    rehashPassword(cn, username, PasswordUtil.hash(password));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Kiểm tra tài khoản admin có tồn tại không (dùng cho bootstrap). */
    public boolean exists(String username) {
        String sql = "SELECT 1 FROM Admin WHERE Username = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Tạo tài khoản admin mới với mật khẩu đã băm (dùng cho bootstrap). */
    public boolean insert(String username, String plainPassword) {
        String sql = "INSERT INTO Admin (Username, Password) VALUES (?, ?)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(plainPassword));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void rehashPassword(Connection cn, String username, String newHash) throws SQLException {
        String sql = "UPDATE Admin SET Password=? WHERE Username=?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }
}
