package service;

/**
 * Thông tin cần thiết để gửi email đặt lại mật khẩu.
 */
public record PasswordResetTicket(String email, String fullName, String token) {
}
