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
        if (accountAgeDays <= SUSPICIOUS_ACCOUNT_AGE) {
            alert.addEvidence(new Alert.AlertEvidence("ACCOUNT_AGE", accountAgeDays, SUSPICIOUS_ACCOUNT_AGE));
        }
        if (burstRate >= 5.0) {
            alert.addEvidence(new Alert.AlertEvidence("BURST_RATE", burstRate, 5.0));
        }
    }
}
