package com.dwatch.common;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;

/**
 * Shared .env / environment-variable reader, used by DBUtil, EmailUtil, and
 * the admin bootstrap. Looks in the project root first (Maven/IDE), then in
 * the Tomcat root (catalina.base) for WAR deployments, then falls back to
 * real environment variables.
 */
public final class AppConfig {

    private static final Dotenv ROOT_ENV   = loadDotenv(null);
    private static final Dotenv TOMCAT_ENV = loadTomcatEnv();

    private AppConfig() {}

    public static String get(String key, String defaultValue) {
        String v = readKey(ROOT_ENV, key);
        if (v == null) v = readKey(TOMCAT_ENV, key);
        if (v == null) v = readSystemEnv(key);
        return v != null ? v : defaultValue;
    }

    private static Dotenv loadTomcatEnv() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase == null || catalinaBase.isEmpty()) return null;
        return loadDotenv(new File(catalinaBase));
    }

    private static Dotenv loadDotenv(File directory) {
        try {
            if (directory != null) {
                File envFile = new File(directory, ".env");
                if (envFile.canRead()) {
                    return Dotenv.configure().directory(directory.getAbsolutePath()).ignoreIfMissing().load();
                }
                return null;
            }
            return Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception ignored) { }
        return null;
    }

    private static String readKey(Dotenv dotenv, String key) {
        if (dotenv == null) return null;
        String v = dotenv.get(key);
        return (v != null && !v.isBlank()) ? v.trim() : null;
    }

    private static String readSystemEnv(String key) {
        String v = System.getenv(key);
        return (v != null && !v.isBlank()) ? v.trim() : null;
    }
}
