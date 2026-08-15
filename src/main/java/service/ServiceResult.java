package service;

import model.User;

/**
 * Kết quả của một thao tác nghiệp vụ: thành công (kèm User nếu có), hoặc thất bại kèm
 * thông báo hiển thị được cho người dùng — đã lọc, không lộ chi tiết nội bộ.
 */
public record ServiceResult(boolean success, User user, String errorMessage) {

    public static ServiceResult ok(User user) {
        return new ServiceResult(true, user, null);
    }

    public static ServiceResult ok() {
        return new ServiceResult(true, null, null);
    }

    public static ServiceResult error(String message) {
        return new ServiceResult(false, null, message);
    }
}
