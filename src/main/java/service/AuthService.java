package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import java.util.Date;
import java.util.UUID;

/**
 * AuthService — nghiệp vụ xác thực người dùng: đăng nhập, đăng ký, đổi mật khẩu,
 * đặt lại mật khẩu qua email.
 *
 * <p>Đây là nơi duy nhất mật khẩu thô được xử lý: servlet chỉ nhận tham số từ request
 * rồi gọi service, DAO chỉ nhận hash. Mọi bản ghi plaintext còn sót trong DB sẽ được
 * băm lại và ghi đè ngay khi người dùng đăng nhập thành công lần kế tiếp.
 */
public class AuthService {

    private static final String INVALID_CREDENTIALS = "Email hoặc mật khẩu không đúng.";
    private static final int RESET_TOKEN_VALID_MINUTES = 60;

    private final UserDAO userDAO;

    public AuthService() {
        this(new UserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public int getResetTokenValidMinutes() {
        return RESET_TOKEN_VALID_MINUTES;
    }

    /** Đăng nhập bằng email + mật khẩu. Thông báo lỗi cố ý mơ hồ để không lộ email nào tồn tại. */
    public ServiceResult login(String email, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            // Bỏ trống ô nhập không liên quan tới việc tài khoản có tồn tại hay không,
            // nên thông báo cụ thể ở đây không làm lộ thêm thông tin gì.
            return ServiceResult.error("Vui lòng nhập email và mật khẩu.");
        }
        User user = userDAO.findByEmail(normalizedEmail);
        if (user == null || !PasswordUtil.verify(rawPassword, user.getPassword())) {
            return ServiceResult.error(INVALID_CREDENTIALS);
        }
        upgradeHashIfNeeded(user, rawPassword);
        return ServiceResult.ok(withoutPassword(user));
    }

    /** Đăng ký tài khoản mới. Mật khẩu được băm trước khi ghi xuống DB. */
    public ServiceResult register(String fullName, String email, String rawPassword,
                               String confirmation, String phone) {
        if (fullName == null || fullName.isBlank()) {
            return ServiceResult.error("Vui lòng nhập họ tên.");
        }
        String normalizedEmail = normalizeEmail(email);
        if (!isValidEmail(normalizedEmail)) {
            return ServiceResult.error("Email không hợp lệ.");
        }
        String passwordError = PasswordPolicy.validate(rawPassword, confirmation);
        if (passwordError != null) {
            return ServiceResult.error(passwordError);
        }
        if (userDAO.emailExists(normalizedEmail)) {
            return ServiceResult.error("Email này đã được đăng ký.");
        }

        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPassword(PasswordUtil.hash(rawPassword));
        user.setPhone(phone);

        int id = userDAO.register(user);
        if (id <= 0) {
            return ServiceResult.error("Đăng ký thất bại. Vui lòng thử lại.");
        }
        user.setUserID(id);
        return ServiceResult.ok(withoutPassword(user));
    }

    /** Đổi mật khẩu khi đã đăng nhập; hash cũ được đọc lại từ DB thay vì tin vào session. */
    public ServiceResult changePassword(int userID, String oldPassword,
                                     String newPassword, String confirmation) {
        if (oldPassword == null || oldPassword.isEmpty()) {
            return ServiceResult.error("Vui lòng nhập mật khẩu cũ.");
        }
        User current = userDAO.findById(userID);
        if (current == null) {
            return ServiceResult.error("Không tìm thấy tài khoản.");
        }
        if (!PasswordUtil.verify(oldPassword, current.getPassword())) {
            return ServiceResult.error("Mật khẩu cũ không đúng.");
        }
        String passwordError = PasswordPolicy.validate(newPassword, confirmation);
        if (passwordError != null) {
            return ServiceResult.error(passwordError);
        }
        if (!userDAO.updatePasswordById(userID, PasswordUtil.hash(newPassword))) {
            return ServiceResult.error("Đổi mật khẩu thất bại. Vui lòng thử lại.");
        }
        return ServiceResult.ok();
    }

    /**
     * Tạo token đặt lại mật khẩu cho email.
     *
     * @return thông tin để gửi email, hoặc {@code null} nếu email chưa đăng ký — người gọi
     *         phải hiển thị cùng một thông báo cho cả hai trường hợp để không lộ email nào
     *         có trong hệ thống
     */
    public PasswordResetTicket issueResetToken(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty()) {
            return null;
        }
        User user = userDAO.findByEmail(normalizedEmail);
        if (user == null) {
            return null;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        Date expiry = new Date(
                System.currentTimeMillis() + RESET_TOKEN_VALID_MINUTES * 60 * 1000L);
        userDAO.saveResetToken(normalizedEmail, token, expiry);
        return new PasswordResetTicket(normalizedEmail, user.getFullName(), token);
    }

    /** Email gắn với token còn hiệu lực; null nếu token sai hoặc đã hết hạn. */
    public String emailForValidToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return userDAO.getEmailByToken(token);
    }

    /** Đặt lại mật khẩu bằng token; token bị xóa ngay sau khi dùng thành công. */
    public ServiceResult resetPassword(String token, String newPassword, String confirmation) {
        String email = emailForValidToken(token);
        if (email == null) {
            return ServiceResult.error(
                    "Link đã hết hạn hoặc không hợp lệ. Vui lòng yêu cầu gửi lại link.");
        }
        String passwordError = PasswordPolicy.validate(newPassword, confirmation);
        if (passwordError != null) {
            return ServiceResult.error(passwordError);
        }
        if (!userDAO.updatePasswordByEmail(email, PasswordUtil.hash(newPassword))) {
            return ServiceResult.error("Đặt lại mật khẩu thất bại. Vui lòng thử lại.");
        }
        userDAO.deleteResetToken(token);
        return ServiceResult.ok();
    }

    /** Băm lại và ghi đè bản ghi còn plaintext (hoặc hash cost cũ) sau khi đăng nhập đúng. */
    private void upgradeHashIfNeeded(User user, String rawPassword) {
        if (!PasswordUtil.needsUpgrade(user.getPassword())) {
            return;
        }
        userDAO.updatePasswordById(user.getUserID(), PasswordUtil.hash(rawPassword));
    }

    /**
     * Bản sao không chứa mật khẩu — để hash không đi theo User vào session hay ra tầng view.
     * Trả về bản sao thay vì sửa tại chỗ để không ảnh hưởng đối tượng người gọi đang giữ.
     */
    private User withoutPassword(User user) {
        User safeCopy = new User();
        safeCopy.setUserID(user.getUserID());
        safeCopy.setFullName(user.getFullName());
        safeCopy.setEmail(user.getEmail());
        safeCopy.setPhone(user.getPhone());
        safeCopy.setAddress(user.getAddress());
        safeCopy.setCreatedDate(user.getCreatedDate());
        return safeCopy;
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
