package service;

import util.PasswordUtil;

import java.nio.charset.StandardCharsets;

/**
 * Quy tắc đặt mật khẩu dùng chung cho đăng ký, đổi mật khẩu và đặt lại mật khẩu.
 */
public final class PasswordPolicy {

    public static final int MIN_LENGTH = 6;

    private PasswordPolicy() {
    }

    /**
     * Kiểm tra mật khẩu mới và phần xác nhận.
     *
     * @return thông báo lỗi hiển thị cho người dùng, hoặc {@code null} nếu hợp lệ
     */
    public static String validate(String password, String confirmation) {
        if (password == null || password.isEmpty()) {
            return "Vui lòng nhập mật khẩu.";
        }
        if (password.length() < MIN_LENGTH) {
            return "Mật khẩu tối thiểu " + MIN_LENGTH + " ký tự.";
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > PasswordUtil.MAX_PASSWORD_BYTES) {
            return "Mật khẩu quá dài (tối đa " + PasswordUtil.MAX_PASSWORD_BYTES + " byte).";
        }
        if (confirmation != null && !password.equals(confirmation)) {
            return "Mật khẩu xác nhận không khớp.";
        }
        return null;
    }
}
