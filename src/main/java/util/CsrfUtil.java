package util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * CsrfUtil — sinh và kiểm tra CSRF token theo mẫu synchronizer token.
 *
 * <p>Mỗi session có đúng một token, được nhúng vào form dưới dạng input ẩn
 * {@code _csrf} (xem {@code pages/csrfField.jsp}) và đối chiếu lại ở
 * {@link servlet.CsrfFilter} với mọi request làm thay đổi dữ liệu.
 */
public final class CsrfUtil {

    public static final String SESSION_ATTRIBUTE = "csrfToken";
    public static final String PARAMETER_NAME = "_csrf";
    public static final String HEADER_NAME = "X-CSRF-Token";

    private static final int TOKEN_BYTES = 32;
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() {
    }

    /** Lấy token của session, sinh mới nếu chưa có. */
    public static String getOrCreateToken(HttpSession session) {
        Object existing = session.getAttribute(SESSION_ATTRIBUTE);
        if (existing instanceof String && !((String) existing).isEmpty()) {
            return (String) existing;
        }
        String token = generateToken();
        session.setAttribute(SESSION_ATTRIBUTE, token);
        return token;
    }

    /** Sinh token mới và ghi đè token cũ — gọi sau khi đăng nhập/đăng xuất. */
    public static String rotateToken(HttpSession session) {
        String token = generateToken();
        session.setAttribute(SESSION_ATTRIBUTE, token);
        return token;
    }

    /**
     * Request có mang token khớp với session hay không.
     * Trả về false khi session chưa tồn tại hoặc token vắng mặt.
     */
    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(SESSION_ATTRIBUTE);
        if (!(expected instanceof String)) {
            return false;
        }
        return matches((String) expected, extractToken(request));
    }

    /** So sánh token constant-time; null/rỗng luôn không hợp lệ. */
    public static boolean matches(String expected, String actual) {
        if (expected == null || actual == null || expected.isEmpty() || actual.isEmpty()) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    private static String extractToken(HttpServletRequest request) {
        String token = request.getParameter(PARAMETER_NAME);
        if (token == null || token.isEmpty()) {
            token = request.getHeader(HEADER_NAME);
        }
        return token;
    }

    private static String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
