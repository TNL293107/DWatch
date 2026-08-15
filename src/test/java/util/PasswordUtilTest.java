package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    private static final String PLAINTEXT_LEGACY = "admin123";

    @Test
    @DisplayName("hash sinh chuỗi BCrypt, không phải mật khẩu gốc")
    void hash_returnsBcryptString() {
        String hash = PasswordUtil.hash("MatKhau@123");

        assertNotEquals("MatKhau@123", hash);
        assertTrue(hash.startsWith("$2a$12$"), "phải dùng BCrypt cost 12, nhận: " + hash);
        assertEquals(60, hash.length());
    }

    @Test
    @DisplayName("hash hai lần cùng mật khẩu cho ra hai chuỗi khác nhau (salt ngẫu nhiên)")
    void hash_isSaltedPerCall() {
        assertNotEquals(PasswordUtil.hash("MatKhau@123"), PasswordUtil.hash("MatKhau@123"));
    }

    @Test
    @DisplayName("verify chấp nhận đúng mật khẩu và từ chối mật khẩu sai")
    void verify_matchesOnlyCorrectPassword() {
        String hash = PasswordUtil.hash("MatKhau@123");

        assertTrue(PasswordUtil.verify("MatKhau@123", hash));
        assertFalse(PasswordUtil.verify("MatKhau@124", hash));
        assertFalse(PasswordUtil.verify("", hash));
    }

    @Test
    @DisplayName("verify hoạt động với mật khẩu tiếng Việt có dấu")
    void verify_supportsUnicodePassword() {
        String hash = PasswordUtil.hash("mậtKhẩuTiếngViệt");

        assertTrue(PasswordUtil.verify("mậtKhẩuTiếngViệt", hash));
        assertFalse(PasswordUtil.verify("matKhauTiengViet", hash));
    }

    @Test
    @DisplayName("verify vẫn cho đăng nhập với bản ghi plaintext cũ chưa migrate")
    void verify_acceptsLegacyPlaintextRow() {
        assertTrue(PasswordUtil.verify(PLAINTEXT_LEGACY, PLAINTEXT_LEGACY));
        assertFalse(PasswordUtil.verify("sai-mat-khau", PLAINTEXT_LEGACY));
    }

    @Test
    @DisplayName("verify trả về false với đầu vào null hoặc rỗng")
    void verify_rejectsNullAndEmpty() {
        assertFalse(PasswordUtil.verify(null, PasswordUtil.hash("abcdef")));
        assertFalse(PasswordUtil.verify("abcdef", null));
        assertFalse(PasswordUtil.verify("abcdef", ""));
    }

    @Test
    @DisplayName("isHashed phân biệt được hash BCrypt và plaintext")
    void isHashed_detectsBcryptFormat() {
        assertTrue(PasswordUtil.isHashed(PasswordUtil.hash("abcdef")));
        assertFalse(PasswordUtil.isHashed(PLAINTEXT_LEGACY));
        assertFalse(PasswordUtil.isHashed("$2a$12$qua-ngan"));
        assertFalse(PasswordUtil.isHashed(null));
    }

    @Test
    @DisplayName("needsUpgrade đúng với plaintext và hash cost thấp, sai với hash hiện hành")
    void needsUpgrade_flagsLegacyAndWeakHashes() {
        assertTrue(PasswordUtil.needsUpgrade(PLAINTEXT_LEGACY));
        // hash BCrypt đúng định dạng 60 ký tự nhưng cost 04 — thấp hơn cấu hình hiện tại
        assertTrue(PasswordUtil.needsUpgrade("$2a$04$" + "a".repeat(53)));
        assertFalse(PasswordUtil.needsUpgrade(PasswordUtil.hash("abcdef")));
    }

    @Test
    @DisplayName("hash từ chối mật khẩu rỗng hoặc vượt 72 byte")
    void hash_rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hash(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hash(""));
        assertThrows(IllegalArgumentException.class,
                () -> PasswordUtil.hash("a".repeat(PasswordUtil.MAX_PASSWORD_BYTES + 1)));
    }

    @Test
    @DisplayName("mật khẩu dài đúng 72 byte vẫn băm được")
    void hash_acceptsMaxLength() {
        String password = "a".repeat(PasswordUtil.MAX_PASSWORD_BYTES);

        assertTrue(PasswordUtil.verify(password, PasswordUtil.hash(password)));
    }
}
