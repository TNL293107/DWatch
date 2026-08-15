package service;

import dao.AdminDAO;
import util.PasswordUtil;

/**
 * AdminAuthService — xác thực tài khoản quản trị.
 *
 * <p>Cùng cơ chế với {@link AuthService}: mật khẩu lưu dưới dạng BCrypt, bản ghi plaintext
 * cũ (ví dụ seed {@code admin/admin123} trong database.sql) được băm lại ngay sau lần
 * đăng nhập đúng đầu tiên.
 */
public class AdminAuthService {

    private final AdminDAO adminDAO;

    public AdminAuthService() {
        this(new AdminDAO());
    }

    public AdminAuthService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    /** true nếu username/password đúng. */
    public boolean authenticate(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isEmpty()) {
            return false;
        }
        String storedValue = adminDAO.findPasswordHash(username);
        if (storedValue == null || !PasswordUtil.verify(rawPassword, storedValue)) {
            return false;
        }
        if (PasswordUtil.needsUpgrade(storedValue)) {
            adminDAO.updatePasswordHash(username, PasswordUtil.hash(rawPassword));
        }
        return true;
    }
}
