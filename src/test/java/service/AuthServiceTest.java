package service;

import dao.UserDAO;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.PasswordUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String EMAIL = "an@example.com";
    private static final String PASSWORD = "MatKhau@123";

    @Mock
    private UserDAO userDAO;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userDAO);
    }

    private static User userWithStoredPassword(String storedPassword) {
        User user = new User();
        user.setUserID(7);
        user.setEmail(EMAIL);
        user.setFullName("Nguyễn Văn An");
        user.setPassword(storedPassword);
        return user;
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("đăng nhập thành công khi mật khẩu khớp hash")
        void login_succeedsWithCorrectPassword() {
            when(userDAO.findByEmail(EMAIL))
                    .thenReturn(userWithStoredPassword(PasswordUtil.hash(PASSWORD)));

            ServiceResult result = authService.login(EMAIL, PASSWORD);

            assertTrue(result.success());
            assertEquals(EMAIL, result.user().getEmail());
        }

        @Test
        @DisplayName("User trả về không mang theo hash mật khẩu")
        void login_stripsPasswordFromReturnedUser() {
            when(userDAO.findByEmail(EMAIL))
                    .thenReturn(userWithStoredPassword(PasswordUtil.hash(PASSWORD)));

            ServiceResult result = authService.login(EMAIL, PASSWORD);

            assertNull(result.user().getPassword());
        }

        @Test
        @DisplayName("sai mật khẩu bị từ chối")
        void login_failsWithWrongPassword() {
            when(userDAO.findByEmail(EMAIL))
                    .thenReturn(userWithStoredPassword(PasswordUtil.hash(PASSWORD)));

            ServiceResult result = authService.login(EMAIL, "sai-mat-khau");

            assertFalse(result.success());
            assertNull(result.user());
        }

        @Test
        @DisplayName("email không tồn tại và mật khẩu sai cho cùng một thông báo lỗi")
        void login_doesNotLeakAccountExistence() {
            when(userDAO.findByEmail("khongton@example.com")).thenReturn(null);
            when(userDAO.findByEmail(EMAIL))
                    .thenReturn(userWithStoredPassword(PasswordUtil.hash(PASSWORD)));

            String unknownEmailError =
                    authService.login("khongton@example.com", PASSWORD).errorMessage();
            String wrongPasswordError =
                    authService.login(EMAIL, "sai-mat-khau").errorMessage();

            assertEquals(unknownEmailError, wrongPasswordError);
        }

        @Test
        @DisplayName("email được chuẩn hóa (trim + lowercase) trước khi tra cứu")
        void login_normalizesEmail() {
            when(userDAO.findByEmail(EMAIL))
                    .thenReturn(userWithStoredPassword(PasswordUtil.hash(PASSWORD)));

            assertTrue(authService.login("  An@Example.COM ", PASSWORD).success());
        }

        @Test
        @DisplayName("bản ghi plaintext cũ đăng nhập được và được băm lại ngay")
        void login_migratesLegacyPlaintextRow() {
            when(userDAO.findByEmail(EMAIL)).thenReturn(userWithStoredPassword(PASSWORD));

            ServiceResult result = authService.login(EMAIL, PASSWORD);

            assertTrue(result.success());
            ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
            verify(userDAO).updatePasswordById(eq(7), hashCaptor.capture());
            assertTrue(PasswordUtil.isHashed(hashCaptor.getValue()));
            assertTrue(PasswordUtil.verify(PASSWORD, hashCaptor.getValue()));
        }

        @Test
        @DisplayName("hash hiện hành thì không ghi lại DB")
        void login_doesNotRewriteCurrentHash() {
            when(userDAO.findByEmail(EMAIL))
                    .thenReturn(userWithStoredPassword(PasswordUtil.hash(PASSWORD)));

            authService.login(EMAIL, PASSWORD);

            verify(userDAO, never()).updatePasswordById(anyInt(), anyString());
        }

        @Test
        @DisplayName("email hoặc mật khẩu rỗng bị chặn trước khi chạm DB")
        void login_rejectsBlankInputWithoutHittingDao() {
            assertFalse(authService.login("", PASSWORD).success());
            assertFalse(authService.login(EMAIL, "").success());
            assertFalse(authService.login(null, null).success());

            verifyNoInteractions(userDAO);
        }

        @Test
        @DisplayName("bỏ trống ô nhập báo lỗi riêng, không dùng chung thông báo sai mật khẩu")
        void login_blankInputHasItsOwnMessage() {
            when(userDAO.findByEmail(EMAIL))
                    .thenReturn(userWithStoredPassword(PasswordUtil.hash(PASSWORD)));

            String blankError = authService.login("", "").errorMessage();
            String wrongPasswordError = authService.login(EMAIL, "sai-mat-khau").errorMessage();

            assertNotEquals(blankError, wrongPasswordError);
        }
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("mật khẩu được băm trước khi lưu xuống DB")
        void register_hashesPasswordBeforePersisting() {
            when(userDAO.emailExists(EMAIL)).thenReturn(false);
            when(userDAO.register(any(User.class))).thenReturn(42);

            ServiceResult result = authService.register(
                    "Nguyễn Văn An", EMAIL, PASSWORD, PASSWORD, "0900000000");

            assertTrue(result.success());
            ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
            verify(userDAO).register(saved.capture());
            assertNotEquals(PASSWORD, saved.getValue().getPassword());
            assertTrue(PasswordUtil.isHashed(saved.getValue().getPassword()));
            assertTrue(PasswordUtil.verify(PASSWORD, saved.getValue().getPassword()));
        }

        @Test
        @DisplayName("email đã tồn tại bị từ chối và không ghi DB")
        void register_rejectsDuplicateEmail() {
            when(userDAO.emailExists(EMAIL)).thenReturn(true);

            ServiceResult result = authService.register(
                    "Nguyễn Văn An", EMAIL, PASSWORD, PASSWORD, "0900000000");

            assertFalse(result.success());
            verify(userDAO, never()).register(any(User.class));
        }

        @Test
        @DisplayName("mật khẩu xác nhận không khớp bị từ chối")
        void register_rejectsMismatchedConfirmation() {
            ServiceResult result = authService.register(
                    "Nguyễn Văn An", EMAIL, PASSWORD, "khac-mat-khau", "0900000000");

            assertFalse(result.success());
            verifyNoInteractions(userDAO);
        }

        @Test
        @DisplayName("mật khẩu quá ngắn bị từ chối")
        void register_rejectsShortPassword() {
            ServiceResult result = authService.register(
                    "Nguyễn Văn An", EMAIL, "abc", "abc", "0900000000");

            assertFalse(result.success());
            verifyNoInteractions(userDAO);
        }

        @Test
        @DisplayName("email sai định dạng và họ tên rỗng bị từ chối")
        void register_rejectsInvalidProfileFields() {
            assertFalse(authService.register("An", "khong-phai-email",
                    PASSWORD, PASSWORD, "0900000000").success());
            assertFalse(authService.register("  ", EMAIL,
                    PASSWORD, PASSWORD, "0900000000").success());

            verifyNoInteractions(userDAO);
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePassword {

        @Test
        @DisplayName("đổi mật khẩu thành công khi mật khẩu cũ đúng")
        void changePassword_succeedsWithCorrectOldPassword() {
            when(userDAO.findById(7))
                    .thenReturn(userWithStoredPassword(PasswordUtil.hash(PASSWORD)));
            when(userDAO.updatePasswordById(eq(7), anyString())).thenReturn(true);

            ServiceResult result =
                    authService.changePassword(7, PASSWORD, "MatKhauMoi@1", "MatKhauMoi@1");

            assertTrue(result.success());
            ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
            verify(userDAO).updatePasswordById(eq(7), hashCaptor.capture());
            assertTrue(PasswordUtil.verify("MatKhauMoi@1", hashCaptor.getValue()));
        }

        @Test
        @DisplayName("mật khẩu cũ sai thì không ghi DB")
        void changePassword_rejectsWrongOldPassword() {
            when(userDAO.findById(7))
                    .thenReturn(userWithStoredPassword(PasswordUtil.hash(PASSWORD)));

            ServiceResult result =
                    authService.changePassword(7, "sai-mat-khau", "MatKhauMoi@1", "MatKhauMoi@1");

            assertFalse(result.success());
            verify(userDAO, never()).updatePasswordById(anyInt(), anyString());
        }

        @Test
        @DisplayName("bỏ trống mật khẩu cũ bị chặn trước khi chạm DB")
        void changePassword_rejectsBlankOldPassword() {
            ServiceResult result =
                    authService.changePassword(7, "", "MatKhauMoi@1", "MatKhauMoi@1");

            assertFalse(result.success());
            verifyNoInteractions(userDAO);
        }

        @Test
        @DisplayName("mật khẩu mới không khớp xác nhận thì không ghi DB")
        void changePassword_rejectsMismatchedConfirmation() {
            when(userDAO.findById(7))
                    .thenReturn(userWithStoredPassword(PasswordUtil.hash(PASSWORD)));

            ServiceResult result =
                    authService.changePassword(7, PASSWORD, "MatKhauMoi@1", "MatKhauMoi@2");

            assertFalse(result.success());
            verify(userDAO, never()).updatePasswordById(anyInt(), anyString());
        }
    }

    @Nested
    @DisplayName("đặt lại mật khẩu")
    class ResetPassword {

        @Test
        @DisplayName("token hợp lệ thì lưu hash mới và xóa token")
        void resetPassword_savesHashAndConsumesToken() {
            when(userDAO.getEmailByToken("tok")).thenReturn(EMAIL);
            when(userDAO.updatePasswordByEmail(eq(EMAIL), anyString())).thenReturn(true);

            ServiceResult result =
                    authService.resetPassword("tok", "MatKhauMoi@1", "MatKhauMoi@1");

            assertTrue(result.success());
            ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
            verify(userDAO).updatePasswordByEmail(eq(EMAIL), hashCaptor.capture());
            assertTrue(PasswordUtil.verify("MatKhauMoi@1", hashCaptor.getValue()));
            verify(userDAO).deleteResetToken("tok");
        }

        @Test
        @DisplayName("token hết hạn hoặc sai bị từ chối")
        void resetPassword_rejectsInvalidToken() {
            when(userDAO.getEmailByToken("het-han")).thenReturn(null);

            ServiceResult result =
                    authService.resetPassword("het-han", "MatKhauMoi@1", "MatKhauMoi@1");

            assertFalse(result.success());
            verify(userDAO, never()).updatePasswordByEmail(anyString(), anyString());
            verify(userDAO, never()).deleteResetToken(anyString());
        }

        @Test
        @DisplayName("mật khẩu mới không hợp lệ thì token không bị tiêu thụ")
        void resetPassword_keepsTokenWhenPasswordInvalid() {
            when(userDAO.getEmailByToken("tok")).thenReturn(EMAIL);

            ServiceResult result = authService.resetPassword("tok", "abc", "abc");

            assertFalse(result.success());
            verify(userDAO, never()).deleteResetToken(anyString());
        }

        @Test
        @DisplayName("email chưa đăng ký không sinh token")
        void issueResetToken_returnsNullForUnknownEmail() {
            when(userDAO.findByEmail("khongton@example.com")).thenReturn(null);

            assertNull(authService.issueResetToken("khongton@example.com"));
            verify(userDAO, never()).saveResetToken(anyString(), anyString(), any());
        }

        @Test
        @DisplayName("email đã đăng ký sinh token và lưu kèm hạn dùng")
        void issueResetToken_createsTokenForKnownEmail() {
            when(userDAO.findByEmail(EMAIL)).thenReturn(userWithStoredPassword("x"));

            PasswordResetTicket ticket = authService.issueResetToken(EMAIL);

            assertNotNull(ticket);
            assertEquals(EMAIL, ticket.email());
            assertEquals("Nguyễn Văn An", ticket.fullName());
            assertFalse(ticket.token().isEmpty());
            verify(userDAO).saveResetToken(eq(EMAIL), eq(ticket.token()), any());
        }
    }
}
