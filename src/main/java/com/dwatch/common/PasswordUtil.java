package com.dwatch.common;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Password hashing helper. Verifies both bcrypt hashes and legacy plaintext
 * passwords (rows created before bcrypt was introduced) so existing accounts
 * keep working without a forced reset — callers should re-hash on successful
 * legacy verification (see UserDAO.login / AdminDAO.authenticate).
 */
public final class PasswordUtil {

    private static final int BCRYPT_COST = 12;

    private PasswordUtil() {}

    public static String hash(String plain) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plain.toCharArray());
    }

    public static boolean isLegacyPlaintext(String stored) {
        return stored == null || !stored.startsWith("$2");
    }

    public static boolean verify(String plain, String stored) {
        if (plain == null || stored == null) return false;
        if (isLegacyPlaintext(stored)) {
            // Constant-time compare — String.equals short-circuits on the first
            // mismatched character, leaking a timing side-channel for accounts
            // not yet upgraded to bcrypt.
            return MessageDigest.isEqual(
                stored.getBytes(StandardCharsets.UTF_8),
                plain.getBytes(StandardCharsets.UTF_8)
            );
        }
        return BCrypt.verifyer().verify(plain.toCharArray(), stored).verified;
    }
}
