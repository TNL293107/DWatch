package com.dwatch.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBUtil - SQL Server connection helper.
 * Credentials are read from environment variables or from a .env file
 * (so each user can set their own without changing code) via AppConfig.
 * Or set: DB_SERVER, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
 */
public class DBUtil {

    private static final String SERVER   = AppConfig.get("DB_SERVER", "localhost");
    private static final String PORT     = AppConfig.get("DB_PORT", "1433");
    private static final String DATABASE = AppConfig.get("DB_NAME", "DWatchDB");
    private static final String USERNAME = AppConfig.get("DB_USER", "");
    private static final String PASSWORD = AppConfig.get("DB_PASSWORD", "");
    private static final String URL;

    static {
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
