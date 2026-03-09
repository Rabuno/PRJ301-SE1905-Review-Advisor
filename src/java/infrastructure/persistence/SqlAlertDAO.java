package infrastructure.persistence;

import application.ports.IAlertRepository;
import domain.entities.Alert;
import domain.entities.Alert.AlertReason;
import domain.entities.Alert.AlertEvidence;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlAlertDAO implements IAlertRepository {

    @Override
    public boolean saveAlert(Alert alert) {
        String sqlAlert = "INSERT INTO Alerts (alert_id, review_id, risk_score, status) VALUES (?, ?, ?, ?)";
        String sqlReason = "INSERT INTO AlertReasons (alert_id, feature_name, importance_weight, description) VALUES (?, ?, ?, ?)";
        String sqlEvidence = "INSERT INTO AlertEvidences (alert_id, rule_type, measured_value, threshold_value) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psAlert = conn.prepareStatement(sqlAlert);
                    PreparedStatement psReason = conn.prepareStatement(sqlReason);
                    PreparedStatement psEvidence = conn.prepareStatement(sqlEvidence)) {

                psAlert.setString(1, alert.getAlertId());
                psAlert.setString(2, alert.getReviewId());
                psAlert.setDouble(3, alert.getRiskScore());
                psAlert.setString(4, alert.getStatus());
                psAlert.executeUpdate();

                for (AlertReason reason : alert.getReasons()) {
                    psReason.setString(1, alert.getAlertId());
                    psReason.setString(2, reason.getFeatureName());
                    psReason.setDouble(3, reason.getImportanceWeight());
                    psReason.setString(4, reason.getDescription());
                    psReason.addBatch();
                }
                psReason.executeBatch();

                for (AlertEvidence evidence : alert.getEvidences()) {
                    psEvidence.setString(1, alert.getAlertId());
                    psEvidence.setString(2, evidence.getRuleType());
                    psEvidence.setDouble(3, evidence.getMeasuredValue());
                    psEvidence.setDouble(4, evidence.getThresholdValue());
                    psEvidence.addBatch();
                }
                psEvidence.executeBatch();

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Alert findByReviewId(String reviewId) {
        // Truy van 1: Lay Alert + AlertReasons
        String sqlReasons = "SELECT a.alert_id, a.risk_score, a.status, " +
                "ar.feature_name, ar.importance_weight, ar.description " +
                "FROM Alerts a " +
                "LEFT JOIN AlertReasons ar ON a.alert_id = ar.alert_id " +
                "WHERE a.review_id = ?";

        // Truy van 2: Lay AlertEvidences
        String sqlEvidences = "SELECT ae.rule_type, ae.measured_value, ae.threshold_value " +
                "FROM Alerts a " +
                "JOIN AlertEvidences ae ON a.alert_id = ae.alert_id " +
                "WHERE a.review_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            Alert alert = null;

            try (PreparedStatement ps = conn.prepareStatement(sqlReasons)) {
                ps.setString(1, reviewId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (alert == null) {
                            alert = new Alert();
                            alert.setAlertId(rs.getString("alert_id"));
                            alert.setReviewId(reviewId);
                            alert.setRiskScore(rs.getDouble("risk_score"));
                            alert.setStatus(rs.getString("status"));
                        }
                        String featureName = rs.getString("feature_name");
                        if (featureName != null) {
                            alert.addReason(new AlertReason(
                                    featureName,
                                    rs.getDouble("importance_weight"),
                                    rs.getString("description")));
                        }
                    }
                }
            }

            if (alert != null) {
                try (PreparedStatement ps = conn.prepareStatement(sqlEvidences)) {
                    ps.setString(1, reviewId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            alert.addEvidence(new AlertEvidence(
                                    rs.getString("rule_type"),
                                    rs.getDouble("measured_value"),
                                    rs.getDouble("threshold_value")));
                        }
                    }
                }
            }

            return alert;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}