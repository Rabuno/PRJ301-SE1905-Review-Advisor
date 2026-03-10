package application.dto;

import java.util.List;
import java.util.Map;

public class AlertDashboardDTO {

    // Thông tin cơ bản từ bảng Reviews
    private String reviewId;
    private String content;
    private int rating;
    private String createdAt;

    // Siêu dữ liệu AI từ bảng Alerts, AlertReasons, AlertEvidences
    private Double riskScore;
    private Map<String, Object> evidence; // Chứa burstScore, similarity, accountAge...
    private List<AIFeatureDTO> aiFeatures; // Lớp nội bộ mô phỏng {name, score}

    // Constructor, Getters và Setters
    public AlertDashboardDTO() {
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public Map<String, Object> getEvidence() {
        return evidence;
    }

    public void setEvidence(Map<String, Object> evidence) {
        this.evidence = evidence;
    }

    public List<AIFeatureDTO> getAiFeatures() {
        return aiFeatures;
    }

    public void setAiFeatures(List<AIFeatureDTO> aiFeatures) {
        this.aiFeatures = aiFeatures;
    }

    // DTO nội bộ (Inner Class) để khớp với r.aiFeatures[0].name và score
    public static class AIFeatureDTO {

        private String name;
        private int score;

        public AIFeatureDTO(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
}
