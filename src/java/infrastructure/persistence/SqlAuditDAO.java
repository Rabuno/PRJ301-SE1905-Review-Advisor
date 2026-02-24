package infrastructure.persistence;

import application.ports.IAuditRepository;
import domain.entities.AuditLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlAuditDAO implements IAuditRepository {

    @Override
    public String getLastLogHash() {
        // Lưu ý: Trong kiến trúc thực tế, hàm này nên được gọi trong cùng một Transaction 
        // với hàm insertLog bên dưới. Ở mức độ prototype 2 tuần, ta mô phỏng truy vấn đọc.
        String sql = "SELECT TOP 1 current_hash FROM AuditLog ORDER BY timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getString("current_hash");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Trong thực tế cần sử dụng Logger chuyên dụng
        }
        return null;
    }

    @Override
    public void insertLog(AuditLog log) {
        // Câu lệnh INSERT vào bảng AuditLog
        String sql = "INSERT INTO AuditLog (audit_id, actor_user_id, action, diff_json, previous_hash, current_hash, timestamp) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, log.getAuditId());
            stmt.setString(2, log.getActorUserId());
            stmt.setString(3, log.getAction());
            stmt.setString(4, log.getDiffJson());
            stmt.setString(5, log.getPreviousHash());
            stmt.setString(6, log.getCurrentHash());
            stmt.setObject(7, java.sql.Timestamp.valueOf(log.getTimestamp()));
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy xuất CSDL khi ghi Audit Log", e);
        }
    }
}
