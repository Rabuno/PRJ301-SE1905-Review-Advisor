package application.services;

import application.ports.IAlertRepository;
import domain.entities.Review;
import domain.entities.Alert;
import domain.enums.ReviewStatus;
import infrastructure.ai.WekaProvider;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TriageService {

    private final WekaProvider wekaProvider;
    private final IAlertRepository alertRepository;

    // Ngưỡng phân loại thực nghiệm
    private static final double RISK_THRESHOLD = 0.70;
    private static final double SUSPICIOUS_ACCOUNT_AGE = 30.0;

    public TriageService(WekaProvider wekaProvider, IAlertRepository alertRepository) {
        this.wekaProvider = wekaProvider;
        this.alertRepository = alertRepository;
    }

    // Sửa đổi phương thức đánh giá để thu thập văn bản (dùng cho XAI)
    public Alert evaluateReview(Review review, double accountAgeDays, double burstRate) {
        try {
            double riskScore = wekaProvider.calculateRiskScore(review.getContent(), review.getRating(), accountAgeDays);

            if (riskScore >= RISK_THRESHOLD) {
                review.setStatus(ReviewStatus.FLAGGED);

                Alert alert = new Alert();
                alert.setAlertId("ALT-" + UUID.randomUUID().toString().substring(0, 8));
                alert.setReviewId(review.getReviewId());
                alert.setRiskScore(riskScore);
                alert.setStatus("OPEN");

                // Truyền toàn bộ đối tượng review vào để trích xuất văn bản
                generateEvidencePack(alert, review, riskScore, accountAgeDays, burstRate);
                
                alertRepository.saveAlert(alert);

                return alert;
            } else {
                review.setStatus(ReviewStatus.PUBLISHED);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Weka Exception In TriageService: " + e.getMessage());
            e.printStackTrace();
            review.setStatus(ReviewStatus.PENDING);
            return null;
        }
    }

    // Cập nhật hàm generateEvidencePack (Xóa Hardcode)
    private void generateEvidencePack(Alert alert, Review review, double riskScore, double accountAgeDays,
            double burstRate) {
        // Cập nhật hàm gọi để đồng bộ với giải thuật Ablation mới
        List<Map.Entry<String, Double>> topFeatures = wekaProvider
                .extractTopKRiskFeatures(review.getContent(), review.getRating(), accountAgeDays, riskScore, 3);

        if (topFeatures.isEmpty()) {
            alert.addReason(new Alert.AlertReason("general_semantic_anomaly", riskScore,
                    "Cấu trúc ngữ pháp tổng thể bất thường"));
        } else {
            for (Map.Entry<String, Double> feature : topFeatures) {
                // Định dạng hiển thị % rõ ràng cho Moderator
                double percentage = Math.round(feature.getValue() * 10000.0) / 100.0;
                alert.addReason(new Alert.AlertReason(
                        "keyword_trigger: " + feature.getKey(),
                        feature.getValue(),
                        "Đóng góp " + percentage + "% vào quyết định rủi ro của AI."));
            }
        }

        // Lớp 2: Evidence-based (Đối chiếu luật)
        if (accountAgeDays <= SUSPICIOUS_ACCOUNT_AGE) {
            alert.addEvidence(new Alert.AlertEvidence("ACCOUNT_AGE", accountAgeDays, SUSPICIOUS_ACCOUNT_AGE));
        }
        if (burstRate >= 5.0) {
            alert.addEvidence(new Alert.AlertEvidence("BURST_RATE", burstRate, 5.0));
        }
    }
}
