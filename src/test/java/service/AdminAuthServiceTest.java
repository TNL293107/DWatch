package service;

import dao.AdminDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.PasswordUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    @Mock
    private AdminDAO adminDAO;

    private AdminAuthService adminAuthService;

    @BeforeEach
    void setUp() {
        adminAuthService = new AdminAuthService(adminDAO);
    }

    @Test
    @DisplayName("đăng nhập thành công với hash đúng")
    void authenticate_succeedsWithCorrectPassword() {
        when(adminDAO.findPasswordHash(USERNAME)).thenReturn(PasswordUtil.hash(PASSWORD));

        assertTrue(adminAuthService.authenticate(USERNAME, PASSWORD));
    }

    @Test
    @DisplayName("sai mật khẩu bị từ chối")
    void authenticate_failsWithWrongPassword() {
        when(adminDAO.findPasswordHash(USERNAME)).thenReturn(PasswordUtil.hash(PASSWORD));

        assertFalse(adminAuthService.authenticate(USERNAME, "sai-mat-khau"));
    }

    @Test
    @DisplayName("username không tồn tại bị từ chối")
    void authenticate_failsForUnknownUsername() {
        when(adminDAO.findPasswordHash("khongton")).thenReturn(null);

        assertFalse(adminAuthService.authenticate("khongton", PASSWORD));
    }

    @Test
    @DisplayName("seed plaintext admin/admin123 đăng nhập được và được băm lại ngay")
    void authenticate_migratesLegacySeedRow() {
        when(adminDAO.findPasswordHash(USERNAME)).thenReturn(PASSWORD);

        assertTrue(adminAuthService.authenticate(USERNAME, PASSWORD));

        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
        verify(adminDAO).updatePasswordHash(eq(USERNAME), hashCaptor.capture());
        assertTrue(PasswordUtil.verify(PASSWORD, hashCaptor.getValue()));
    }

    @Test
    @DisplayName("hash hiện hành thì không ghi lại DB")
    void authenticate_doesNotRewriteCurrentHash() {
        when(adminDAO.findPasswordHash(USERNAME)).thenReturn(PasswordUtil.hash(PASSWORD));

        adminAuthService.authenticate(USERNAME, PASSWORD);

        verify(adminDAO, never()).updatePasswordHash(anyString(), anyString());
    }

    @Test
    @DisplayName("đầu vào rỗng bị chặn trước khi chạm DB")
    void authenticate_rejectsBlankInput() {
        assertFalse(adminAuthService.authenticate("", PASSWORD));
        assertFalse(adminAuthService.authenticate(USERNAME, ""));
        assertFalse(adminAuthService.authenticate(null, null));

        verifyNoInteractions(adminDAO);
    }
}
