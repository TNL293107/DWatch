package dao;

import model.User;
import util.DBUtil;

import java.sql.*;

public class UserDAO {

    /** Đăng ký tài khoản mới. Trả về UserID hoặc -1 nếu lỗi */
    public int register(User user) {
        String sql = "INSERT INTO Users (FullName, Email, Password, Phone, Address) VALUES (?,?,?,?,?)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    /** Đăng nhập. Trả về User nếu đúng, null nếu sai */
    public User login(String email, String password) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Kiểm tra email đã tồn tại chưa */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM Users WHERE Email = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Cập nhật thông tin cá nhân */
    public boolean updateProfile(User user) {
        String sql = "UPDATE Users SET FullName=?, Phone=?, Address=? WHERE UserID=?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getAddress());
            ps.setInt(4, user.getUserID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Đổi mật khẩu */
    public boolean changePassword(int userID, String oldPass, String newPass) {
        String sql = "UPDATE Users SET Password=? WHERE UserID=? AND Password=?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newPass);
            ps.setInt(2, userID);
            ps.setString(3, oldPass);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Tìm user theo email (dùng cho quên mật khẩu) */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Đặt lại mật khẩu theo email (sau khi xác thực token) */
    public boolean updatePasswordByEmail(String email, String newPassword) {
        String sql = "UPDATE Users SET Password = ? WHERE Email = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Lưu token đặt lại mật khẩu (bảng PasswordResetToken phải tồn tại) */
    public void saveResetToken(String email, String token, java.util.Date expiry) {
        String sql = "INSERT INTO PasswordResetToken (Token, Email, Expiry) VALUES (?, ?, ?)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setString(2, email);
            ps.setTimestamp(3, new Timestamp(expiry.getTime()));
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /** Lấy email từ token (token còn hiệu lực). Trả về null nếu hết hạn hoặc không tồn tại. */
    public String getEmailByToken(String token) {
        String sql = "SELECT Email FROM PasswordResetToken WHERE Token = ? AND Expiry > GETDATE()";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("Email");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Xóa token sau khi đổi mật khẩu xong */
    public void deleteResetToken(String token) {
        String sql = "DELETE FROM PasswordResetToken WHERE Token = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserID(rs.getInt("UserID"));
        u.setFullName(rs.getString("FullName"));
        u.setEmail(rs.getString("Email"));
        u.setPassword(rs.getString("Password"));
        u.setPhone(rs.getString("Phone"));
        u.setAddress(rs.getString("Address"));
        u.setCreatedDate(rs.getTimestamp("CreatedDate"));
        return u;
    }
}
