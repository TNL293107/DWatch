package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsrfUtilTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    /** Session giả lưu attribute trong Map để test được vòng đời sinh/xoay token. */
    private Map<String, Object> sessionAttributes;

    @BeforeEach
    void setUp() {
        sessionAttributes = new HashMap<>();
        lenient().when(session.getAttribute(anyString()))
                .thenAnswer(inv -> sessionAttributes.get(inv.getArgument(0, String.class)));
        lenient().doAnswer(inv -> sessionAttributes.put(
                        inv.getArgument(0, String.class), inv.getArgument(1)))
                .when(session).setAttribute(anyString(), any());
    }

    @Test
    @DisplayName("getOrCreateToken sinh token mới và lưu vào session")
    void getOrCreateToken_createsAndStoresToken() {
        String token = CsrfUtil.getOrCreateToken(session);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(token, sessionAttributes.get(CsrfUtil.SESSION_ATTRIBUTE));
    }

    @Test
    @DisplayName("getOrCreateToken trả lại đúng token cũ nếu session đã có")
    void getOrCreateToken_isIdempotent() {
        String first = CsrfUtil.getOrCreateToken(session);

        assertEquals(first, CsrfUtil.getOrCreateToken(session));
    }

    @Test
    @DisplayName("hai session khác nhau nhận hai token khác nhau")
    void getOrCreateToken_isUniquePerSession() {
        String first = CsrfUtil.getOrCreateToken(session);
        sessionAttributes.clear();

        assertNotEquals(first, CsrfUtil.getOrCreateToken(session));
    }

    @Test
    @DisplayName("rotateToken thay token cũ bằng token mới")
    void rotateToken_replacesExistingToken() {
        String before = CsrfUtil.getOrCreateToken(session);

        String after = CsrfUtil.rotateToken(session);

        assertNotEquals(before, after);
        assertEquals(after, sessionAttributes.get(CsrfUtil.SESSION_ATTRIBUTE));
    }

    @Test
    @DisplayName("isValid chấp nhận token đúng gửi qua parameter")
    void isValid_acceptsMatchingParameter() {
        String token = CsrfUtil.getOrCreateToken(session);
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter(CsrfUtil.PARAMETER_NAME)).thenReturn(token);

        assertTrue(CsrfUtil.isValid(request));
    }

    @Test
    @DisplayName("isValid chấp nhận token đúng gửi qua header (dùng cho AJAX)")
    void isValid_acceptsMatchingHeader() {
        String token = CsrfUtil.getOrCreateToken(session);
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter(CsrfUtil.PARAMETER_NAME)).thenReturn(null);
        when(request.getHeader(CsrfUtil.HEADER_NAME)).thenReturn(token);

        assertTrue(CsrfUtil.isValid(request));
    }

    @Test
    @DisplayName("isValid từ chối token sai")
    void isValid_rejectsWrongToken() {
        CsrfUtil.getOrCreateToken(session);
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter(CsrfUtil.PARAMETER_NAME)).thenReturn("token-gia-mao");

        assertFalse(CsrfUtil.isValid(request));
    }

    @Test
    @DisplayName("isValid từ chối request không mang token")
    void isValid_rejectsMissingToken() {
        CsrfUtil.getOrCreateToken(session);
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter(CsrfUtil.PARAMETER_NAME)).thenReturn(null);
        when(request.getHeader(CsrfUtil.HEADER_NAME)).thenReturn(null);

        assertFalse(CsrfUtil.isValid(request));
    }

    @Test
    @DisplayName("isValid từ chối khi chưa có session")
    void isValid_rejectsWhenNoSession() {
        when(request.getSession(false)).thenReturn(null);

        assertFalse(CsrfUtil.isValid(request));
    }

    @Test
    @DisplayName("isValid từ chối khi session chưa có token")
    void isValid_rejectsWhenSessionHasNoToken() {
        when(request.getSession(false)).thenReturn(session);

        assertFalse(CsrfUtil.isValid(request));
    }

    @Test
    @DisplayName("matches từ chối null, rỗng và chuỗi lệch")
    void matches_rejectsInvalidPairs() {
        assertFalse(CsrfUtil.matches(null, "abc"));
        assertFalse(CsrfUtil.matches("abc", null));
        assertFalse(CsrfUtil.matches("", ""));
        assertFalse(CsrfUtil.matches("abc", "abcd"));
        assertTrue(CsrfUtil.matches("abc", "abc"));
    }
}
