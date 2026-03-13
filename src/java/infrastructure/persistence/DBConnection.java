package infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // URL kết nối tới SQL Server 2019
    // Prefer environment variables for portability; fall back to local defaults.
    private static final String URL = getEnv("DB_URL",
            "jdbc:sqlserver://localhost:1433;databaseName=ReviewPlatform;trustServerCertificate=true;");
    private static final String USER = getEnv("DB_USER", "sa");
    private static final String PASSWORD = getEnv("DB_PASSWORD", "12345");

    static {
        try {
            // Đăng ký JDBC Driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Lỗi hệ thống: Không tìm thấy SQL Server Driver.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String getEnv(String key, String fallback) {
        try {
            String v = System.getenv(key);
            if (v != null && !v.trim().isEmpty()) return v.trim();
        } catch (Exception ignored) {
        }
        return fallback;
    }
}
