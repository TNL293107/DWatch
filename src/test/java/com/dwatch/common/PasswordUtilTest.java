package com.dwatch.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordUtilTest {

    @Test
    void hash_producesBcryptFormattedString() {
        String hash = PasswordUtil.hash("mySecret123");
        assertThat(hash).startsWith("$2");
    }

    @Test
    void verify_correctPasswordAgainstItsHash_returnsTrue() {
        String hash = PasswordUtil.hash("mySecret123");
        assertThat(PasswordUtil.verify("mySecret123", hash)).isTrue();
    }

    @Test
    void verify_wrongPasswordAgainstHash_returnsFalse() {
        String hash = PasswordUtil.hash("mySecret123");
        assertThat(PasswordUtil.verify("wrongPassword", hash)).isFalse();
    }

    @Test
    void verify_legacyPlaintextMatch_returnsTrue() {
        // Accounts created before bcrypt was introduced store the raw password.
        assertThat(PasswordUtil.verify("legacyPass", "legacyPass")).isTrue();
    }

    @Test
    void verify_legacyPlaintextMismatch_returnsFalse() {
        assertThat(PasswordUtil.verify("wrong", "legacyPass")).isFalse();
    }

    @Test
    void verify_nullArguments_returnsFalse() {
        assertThat(PasswordUtil.verify(null, "somehash")).isFalse();
        assertThat(PasswordUtil.verify("plain", null)).isFalse();
    }

    @Test
    void isLegacyPlaintext_bcryptHash_returnsFalse() {
        String hash = PasswordUtil.hash("mySecret123");
        assertThat(PasswordUtil.isLegacyPlaintext(hash)).isFalse();
    }

    @Test
    void isLegacyPlaintext_rawStringOrNull_returnsTrue() {
        assertThat(PasswordUtil.isLegacyPlaintext("plainOldPassword")).isTrue();
        assertThat(PasswordUtil.isLegacyPlaintext(null)).isTrue();
    }
}
