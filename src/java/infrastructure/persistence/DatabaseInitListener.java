package infrastructure.persistence;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = DBConnection.getConnection()) {
            DatabaseMetaData dbm = conn.getMetaData();
            // Kiểm tra xem bảng Users đã tồn tại chưa
            try (ResultSet tables = dbm.getTables(null, null, "Users", null)) {
                if (!tables.next()) {
                    System.out.println("[System] Bắt đầu khởi tạo cơ sở dữ liệu...");
                    executeSqlScript(sce.getServletContext().getResourceAsStream("/WEB-INF/db/schema.sql"), conn);
                    System.out.println("[System] Khởi tạo dữ liệu thành công!");
                } else {
                    System.out.println("[System] Cơ sở dữ liệu đã tồn tại, bỏ qua khởi tạo.");
                }
            }
        } catch (Exception e) {
            System.err.println("[System Error] Lỗi khởi tạo cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void executeSqlScript(InputStream is, Connection conn) throws Exception {
        if (is == null) throw new Exception("Không tìm thấy file schema.sql");
        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("--")) continue;
                sql.append(line);
                if (line.trim().endsWith(";")) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql.toString());
                    }
                    sql.setLength(0); // Làm sạch buffer cho lệnh tiếp theo
                }
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Đóng kết nối hệ thống nếu cần thiết khi tắt server
    }
}