package util;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBUtil - SQL Server connection helper.
 * Credentials are read from environment variables or from a .env file
 * (so each user can set their own without changing code).
 *
 * .env can be in: project root (Maven/IDE), or Tomcat root (catalina.base) when chạy WAR.
 * Or set: DB_SERVER, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
 */
public class DBUtil {

    private static final String SERVER;
    private static final String PORT;
    private static final String DATABASE;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final String URL;

    private static String getConfig(Dotenv dotenv, String key, String defaultValue) {
        if (dotenv != null) {
            String v = dotenv.get(key);
            if (v != null && !v.isBlank()) return v.trim();
        }
        String v = System.getenv(key);
        if (v != null && !v.isBlank()) return v.trim();
        return defaultValue;
    }
    private static Dotenv loadDotenv(File directory) {
        try {
            if (directory != null) {
                File envFile = new File(directory, ".env");
                if (envFile.canRead()) {
                    return Dotenv.configure().directory(directory.getAbsolutePath()).ignoreIfMissing().load();
                }
            } else {
                return Dotenv.configure().ignoreIfMissing().load();
            }
        } catch (Exception ignored) { }
        return null;
    }

    static {
        Dotenv dotenv = loadDotenv(null);
        // Khi chạy trên Tomcat (WAR), thư mục hiện tại không phải project → thử đọc .env từ thư mục Tomcat
        if (dotenv != null) {
            String u = getConfig(dotenv, "DB_USER", "");
            String p = getConfig(dotenv, "DB_PASSWORD", "");
            if ((u == null || u.isEmpty()) || (p == null || p.isEmpty())) {
                String catalinaBase = System.getProperty("catalina.base");
                if (catalinaBase != null && !catalinaBase.isEmpty()) {
                    Dotenv tomcatEnv = loadDotenv(new File(catalinaBase));
                    if (tomcatEnv != null) dotenv = tomcatEnv;
                }
            }
        } else {
            String catalinaBase = System.getProperty("catalina.base");
            if (catalinaBase != null && !catalinaBase.isEmpty()) {
                dotenv = loadDotenv(new File(catalinaBase));
            }
        }

        SERVER   = getConfig(dotenv, "DB_SERVER", "localhost");
        PORT     = getConfig(dotenv, "DB_PORT", "1433");
        DATABASE = getConfig(dotenv, "DB_NAME", "DWatchDB");
        USERNAME = getConfig(dotenv, "DB_USER", "");
        PASSWORD = getConfig(dotenv, "DB_PASSWORD", "");

        URL = "jdbc:sqlserver://" + SERVER + ":" + PORT
            + ";databaseName=" + DATABASE
            + ";encrypt=false"
            + ";trustServerCertificate=true";

        if (USERNAME.isEmpty() || PASSWORD.isEmpty()) {
            throw new IllegalStateException(
                "DB_USER and DB_PASSWORD must be set. "
                + "Create a .env file (copy from .env.example) in project root or in Tomcat root (catalina.base), or set environment variables."
            );
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQL Server JDBC driver not found. "
                + "Add mssql-jdbc-*.jar to your project libraries.", e);
        }
    }

    /**
     * Open and return a new Connection.
     * Caller is responsible for closing it (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
