package util;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * PasswordUtil — băm và xác thực mật khẩu bằng BCrypt.
 *
 * <p>Toàn bộ mật khẩu mới được lưu dưới dạng BCrypt (định dạng {@code $2a$<cost>$<salt+hash>},
 * 60 ký tự — vừa với cột nvarchar(255) hiện có).
 *
 * <p>Dữ liệu cũ trong DB đang lưu plaintext. {@link #verify(String, String)} vẫn chấp nhận
 * các bản ghi đó (so sánh constant-time) để người dùng cũ đăng nhập được, còn
 * {@link #needsUpgrade(String)} báo cho service layer biết phải băm lại và ghi đè ngay
 * sau lần đăng nhập thành công đầu tiên.
 */
public final class PasswordUtil {

    /** Chi phí BCrypt (2^12 vòng) — cân bằng giữa an toàn và thời gian đăng nhập. */
    private static final int COST = 12;

    /** BCrypt chỉ xử lý tối đa 72 byte; dài hơn sẽ bị cắt nên phải chặn từ đầu. */
    public static final int MAX_PASSWORD_BYTES = 72;

    private PasswordUtil() {
    }

    /**
     * Băm mật khẩu thô.
     *
     * @throws IllegalArgumentException nếu mật khẩu rỗng hoặc vượt quá 72 byte
     */
    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }
        if (exceedsMaxLength(rawPassword)) {
            throw new IllegalArgumentException(
                    "Mật khẩu vượt quá " + MAX_PASSWORD_BYTES + " byte.");
        }
        return BCrypt.withDefaults().hashToString(COST, rawPassword.toCharArray());
    }

    /**
     * Kiểm tra mật khẩu thô có khớp với giá trị đang lưu trong DB không.
     * Giá trị lưu có thể là hash BCrypt hoặc plaintext (dữ liệu cũ chưa migrate).
     */
    public static boolean verify(String rawPassword, String storedValue) {
        if (rawPassword == null || storedValue == null || storedValue.isEmpty()) {
            return false;
        }
        if (!isHashed(storedValue)) {
            return constantTimeEquals(rawPassword, storedValue);
        }
        if (exceedsMaxLength(rawPassword)) {
            return false;
        }
        return BCrypt.verifyer()
                .verify(rawPassword.toCharArray(), storedValue)
                .verified;
    }

    /** Giá trị lưu trong DB đã là hash BCrypt hay chưa. */
    public static boolean isHashed(String storedValue) {
        if (storedValue == null || storedValue.length() != 60) {
            return false;
        }
        return storedValue.startsWith("$2a$")
                || storedValue.startsWith("$2b$")
                || storedValue.startsWith("$2y$");
    }

    /**
     * Giá trị lưu trong DB có cần băm lại không — đúng khi bản ghi còn là plaintext
     * hoặc được băm với cost thấp hơn cấu hình hiện tại.
     */
    public static boolean needsUpgrade(String storedValue) {
        if (!isHashed(storedValue)) {
            return true;
        }
        return parseCost(storedValue) < COST;
    }

    private static boolean exceedsMaxLength(String rawPassword) {
        return rawPassword.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES;
    }

    /** Đọc cost từ chuỗi hash {@code $2a$12$...}; trả về -1 nếu không parse được. */
    private static int parseCost(String storedValue) {
        try {
            return Integer.parseInt(storedValue.substring(4, 6));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return -1;
        }
    }

    /** So sánh không phụ thuộc thời gian, tránh timing attack trên bản ghi plaintext. */
    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }
}
