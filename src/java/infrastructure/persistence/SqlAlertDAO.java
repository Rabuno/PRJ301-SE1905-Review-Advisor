package infrastructure.persistence;

import application.ports.IAlertRepository;
import domain.entities.Alert;
import domain.entities.Alert.AlertReason;
import domain.entities.Alert.AlertEvidence;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlAlertDAO implements IAlertRepository {

    @Override
    public boolean saveAlert(Alert alert) {
        String sqlAlert = "INSERT INTO Alerts (alert_id, review_id, risk_score, status) VALUES (?, ?, ?, ?)";
        String sqlReason = "INSERT INTO AlertReasons (alert_id, feature_name, importance_weight, description) VALUES (?, ?, ?, ?)";
        String sqlEvidence = "INSERT INTO AlertEvidences (alert_id, rule_type, measured_value, threshold_value) VALUES (?, ?, ?, ?)";

        // Sử dụng Transaction (ACID)
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction

            try (PreparedStatement psAlert = conn.prepareStatement(sqlAlert);
                 PreparedStatement psReason = conn.prepareStatement(sqlReason);
                 PreparedStatement psEvidence = conn.prepareStatement(sqlEvidence)) {

                // 1. Lưu Thực thể Cha (Alerts)
                psAlert.setString(1, alert.getAlertId());
                psAlert.setString(2, alert.getReviewId());
                psAlert.setDouble(3, alert.getRiskScore());
                psAlert.setString(4, alert.getStatus());
                psAlert.executeUpdate();

                // 2. Lưu Lớp XAI 1 (AlertReasons)
                for (AlertReason reason : alert.getReasons()) {
                    psReason.setString(1, alert.getAlertId());
                    psReason.setString(2, reason.getFeatureName());
                    psReason.setDouble(3, reason.getImportanceWeight());
                    psReason.setString(4, reason.getDescription());
                    psReason.addBatch(); // Sử dụng Batch Insert để tối ưu I/O
                }
                psReason.executeBatch();

                // 3. Lưu Lớp XAI 2 (AlertEvidences)
                for (AlertEvidence evidence : alert.getEvidences()) {
                    psEvidence.setString(1, alert.getAlertId());
                    psEvidence.setString(2, evidence.getRuleType());
                    psEvidence.setDouble(3, evidence.getMeasuredValue());
                    psEvidence.setDouble(4, evidence.getThresholdValue());
                    psEvidence.addBatch();
                }
                psEvidence.executeBatch();

                conn.commit(); // Khớp giao dịch thành công
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Hoàn tác toàn bộ nếu có lỗi
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}