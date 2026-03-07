package domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Alert {

    // Thuộc tính cốt lõi (Khớp với bảng Alerts)
    private String alertId;
    private String reviewId;
    private double riskScore;
    private String status; // 'OPEN', 'RESOLVED', 'DISMISSED'
    private LocalDateTime createdAt;

    // Gói Bằng Chứng XAI (Khớp với bảng AlertReasons và AlertEvidences)
    private List<AlertReason> reasons;
    private List<AlertEvidence> evidences;

    public Alert() {
        this.reasons = new ArrayList<>();
        this.evidences = new ArrayList<>();
    }

    // --- GETTERS & SETTERS CHO LÕI ---
    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // --- QUẢN LÝ GÓI BẰNG CHỨNG ---
    public List<AlertReason> getReasons() {
        return reasons;
    }

    public void addReason(AlertReason reason) {
        this.reasons.add(reason);
    }

    public List<AlertEvidence> getEvidences() {
        return evidences;
    }

    public void addEvidence(AlertEvidence evidence) {
        this.evidences.add(evidence);
    }

    // =========================================================
    // LỚP LỒNG (NESTED CLASSES) ĐẠI DIỆN CHO CẤU TRÚC BẰNG CHỨNG
    // =========================================================
    public static class AlertReason {

        private String featureName;
        private double importanceWeight;
        private String description;

        public AlertReason(String featureName, double importanceWeight, String description) {
            this.featureName = featureName;
            this.importanceWeight = importanceWeight;
            this.description = description;
        }

        public String getFeatureName() {
            return featureName;
        }

        public double getImportanceWeight() {
            return importanceWeight;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class AlertEvidence {

        private String ruleType;
        private double measuredValue;
        private double thresholdValue;

        public AlertEvidence(String ruleType, double measuredValue, double thresholdValue) {
            this.ruleType = ruleType;
            this.measuredValue = measuredValue;
            this.thresholdValue = thresholdValue;
        }

        public String getRuleType() {
            return ruleType;
        }

        public double getMeasuredValue() {
            return measuredValue;
        }

        public double getThresholdValue() {
            return thresholdValue;
        }
    }
}
