package adapters.dto;

import domain.entities.Alert;
import domain.entities.Review;

/**
 * DTO (Data Transfer Object) gộp Review + Alert lại để truyền sang JSP.
 * Dùng trong Moderation Dashboard để hiển thị đầy đủ bằng chứng AI.
 */
public class FlaggedReviewDTO {

    private final Review review;
    private final Alert alert; // Có thể null nếu Alert chưa được lưu DB

    public FlaggedReviewDTO(Review review, Alert alert) {
        this.review = review;
        this.alert = alert;
    }

    public Review getReview() {
        return review;
    }

    public Alert getAlert() {
        return alert;
    }

    // Các helper method tiện lợi để dùng trong JSP (EL expression)
    // Risk Score từ Alert, hiển thị 2 chữ số thập phân
    public String getRiskScoreDisplay() {
        if (alert == null)
            return "N/A";
        return String.format("%.2f", alert.getRiskScore());
    }

    // Lấy AlertEvidence theo ruleType (ACCOUNT_AGE, BURST_RATE...)
    public double getEvidenceValue(String ruleType) {
        if (alert == null || alert.getEvidences() == null)
            return -1;
        for (Alert.AlertEvidence ev : alert.getEvidences()) {
            if (ruleType.equals(ev.getRuleType())) {
                return ev.getMeasuredValue();
            }
        }
        return -1;
    }

    // Kiểm tra có Evidence không (để JSP có thể dùng c:if)
    public boolean hasEvidence(String ruleType) {
        return getEvidenceValue(ruleType) >= 0;
    }
}
