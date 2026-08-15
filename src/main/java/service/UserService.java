package service;

import dao.UserDAO;
import model.User;

/**
 * UserService — nghiệp vụ trên hồ sơ người dùng (không thuộc luồng xác thực).
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this(new UserDAO());
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Cập nhật thông tin cá nhân. Trả về User đã cập nhật để servlet ghi lại vào session.
     */
    public ServiceResult updateProfile(User loggedUser, String fullName,
                                       String phone, String address) {
        if (loggedUser == null) {
            return ServiceResult.error("Không tìm thấy tài khoản.");
        }
        if (fullName == null || fullName.isBlank()) {
            return ServiceResult.error("Vui lòng nhập họ tên.");
        }
        loggedUser.setFullName(fullName.trim());
        loggedUser.setPhone(phone);
        loggedUser.setAddress(address);

        if (!userDAO.updateProfile(loggedUser)) {
            return ServiceResult.error("Cập nhật thất bại.");
        }
        return ServiceResult.ok(loggedUser);
    }
}
