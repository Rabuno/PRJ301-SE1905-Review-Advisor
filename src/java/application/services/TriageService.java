package application.services;

import application.dto.AiTriageResult;
import application.ports.IReviewTriageAI;
import domain.entities.Review;
import domain.entities.Alert;
import domain.enums.ReviewStatus;
import java.util.UUID;

public class TriageService {

    private final IReviewTriageAI aiProvider;

    // Ngưỡng phân loại thực nghiệm
    private static final double RISK_THRESHOLD = 0.70;
    private static final double SUSPICIOUS_ACCOUNT_AGE = 30.0;

    public TriageService(IReviewTriageAI aiProvider) {
        this.aiProvider = aiProvider;
    }

    // Sửa đổi phương thức đánh giá để thu thập văn bản (dùng cho XAI)
    public Alert evaluateReview(Review review, double accountAgeDays, double burstRate) {
        try {
            AiTriageResult result = aiProvider.analyzeReview(review, accountAgeDays, burstRate);
            double riskScore = (result == null) ? 0.0 : result.getRiskScore();

            boolean severeLabel = result != null && (
                    result.getLabels().contains("policy_violation")
                    || result.getLabels().contains("impersonation")
            );

            if (riskScore >= RISK_THRESHOLD || severeLabel) {
                review.setStatus(ReviewStatus.FLAGGED);

                Alert alert = new Alert();
                alert.setAlertId("ALT-" + UUID.randomUUID().toString().substring(0, 8));
                alert.setReviewId(review.getReviewId());
                alert.setRiskScore(riskScore);
                alert.setStatus("OPEN");

                generateEvidencePack(alert, review, result, accountAgeDays, burstRate);

                return alert;
            } else {
                review.setStatus(ReviewStatus.PUBLISHED);
                return null;
            }
        } catch (Exception e) {
            System.err.println("AI Exception In TriageService: " + e.getMessage());
            e.printStackTrace();
            review.setStatus(ReviewStatus.PENDING);
            return null;
        }
    }

    /**
     * Builds an evidence pack for moderation/backfill.
     * Always returns an Alert object (even if risk is low) and never mutates review status.
     */
    public Alert buildAlertForModeration(Review review, double accountAgeDays, double burstRate) {
        Alert alert = new Alert();
        alert.setAlertId("ALT-" + UUID.randomUUID().toString().substring(0, 8));
        alert.setReviewId(review != null ? review.getReviewId() : null);
        alert.setStatus("OPEN");

        try {
            AiTriageResult result = aiProvider.analyzeReview(review, accountAgeDays, burstRate);
            double riskScore = (result == null) ? 0.0 : result.getRiskScore();
            alert.setRiskScore(riskScore);

            // Populate reasons/evidence no matter what so the UI has something actionable.
            generateEvidencePack(alert, review, result, accountAgeDays, burstRate);
            return alert;
        } catch (Exception e) {
            // Degraded mode: still provide system evidence + minimal reason.
            alert.setRiskScore(0.50);
            alert.addReason(new Alert.AlertReason("ai_unavailable", 0.50,
                    "AI provider khong phan hoi. Can moderator kiem tra thu cong."));
            alert.addEvidence(new Alert.AlertEvidence("ACCOUNT_AGE", accountAgeDays, SUSPICIOUS_ACCOUNT_AGE));
            alert.addEvidence(new Alert.AlertEvidence("BURST_RATE", burstRate, 5.0));
            return alert;
        }
    }

    private void generateEvidencePack(Alert alert, Review review, AiTriageResult result, double accountAgeDays,
            double burstRate) {
        // Layer 1: AI-provided reasons (labels + explanations)
        if (result == null || (result.getReasons().isEmpty() && result.getLabels().isEmpty())) {
            alert.addReason(new Alert.AlertReason("general_anomaly", 0.50,
                    "AI phát hiện dấu hiệu bất thường tổng quát."));
        } else if (!result.getReasons().isEmpty()) {
            for (AiTriageResult.Reason r : result.getReasons()) {
                alert.addReason(new Alert.AlertReason(
                        r.getFeature(),
                        r.getWeight(),
                        r.getDescription()
                ));
            }
        } else {
            // If provider only returns labels, still surface them.
            for (String label : result.getLabels()) {
                alert.addReason(new Alert.AlertReason(label, 0.35, "AI gắn nhãn rủi ro: " + label));
            }
        }

        // Layer 2: Evidence-based (luật hệ thống)
        // Always record these so moderators don't see N/A/Unknown.
        alert.addEvidence(new Alert.AlertEvidence("ACCOUNT_AGE", accountAgeDays, SUSPICIOUS_ACCOUNT_AGE));
        alert.addEvidence(new Alert.AlertEvidence("BURST_RATE", burstRate, 5.0));
    }
}
