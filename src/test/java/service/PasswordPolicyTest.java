package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyTest {

    @Test
    @DisplayName("mật khẩu hợp lệ và khớp xác nhận thì không có lỗi")
    void validate_acceptsValidPassword() {
        assertNull(PasswordPolicy.validate("abcdef", "abcdef"));
    }

    @Test
    @DisplayName("không truyền xác nhận thì chỉ kiểm tra độ dài")
    void validate_skipsConfirmationWhenNull() {
        assertNull(PasswordPolicy.validate("abcdef", null));
    }

    @Test
    @DisplayName("mật khẩu rỗng hoặc null bị từ chối")
    void validate_rejectsEmpty() {
        assertNotNull(PasswordPolicy.validate(null, null));
        assertNotNull(PasswordPolicy.validate("", ""));
    }

    @Test
    @DisplayName("mật khẩu ngắn hơn mức tối thiểu bị từ chối")
    void validate_rejectsTooShort() {
        String error = PasswordPolicy.validate("abcde", "abcde");

        assertNotNull(error);
        assertTrue(error.contains(String.valueOf(PasswordPolicy.MIN_LENGTH)));
    }

    @Test
    @DisplayName("mật khẩu vượt giới hạn 72 byte của BCrypt bị từ chối")
    void validate_rejectsTooLong() {
        assertNotNull(PasswordPolicy.validate("a".repeat(73), "a".repeat(73)));
    }

    @Test
    @DisplayName("xác nhận không khớp bị từ chối")
    void validate_rejectsMismatchedConfirmation() {
        assertNotNull(PasswordPolicy.validate("abcdef", "abcdeg"));
    }
}
