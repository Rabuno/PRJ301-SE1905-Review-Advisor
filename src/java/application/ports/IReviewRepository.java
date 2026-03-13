package application.ports;

import domain.entities.Review;
import domain.enums.ReviewStatus;
import java.util.List;

public interface IReviewRepository {

    boolean save(Review review);

    boolean saveReviewWithAlert(domain.entities.Review review, domain.entities.Alert alert);

    void updateStatus(String reviewId, ReviewStatus newStatus);

    Review findById(String reviewId);

    void deleteReview(String reviewId);

    List<Review> getReviewHistory(String reviewId);

    List<Review> findByStatus(ReviewStatus status);

    List<Review> findByProductId(String productId);

    List<Review> findByUserId(String userId);

    Object[] getReviewStatsByMerchant(String merchantId);

    List<Review> getRecentReviewsByMerchant(String merchantId, int limit);

    // Trend series for merchant dashboard. Each row: (date, positiveCount, negativeCount, flaggedCount)
    List<Object[]> getMerchantReviewTrend(String merchantId, int days);

    int countRecentReviewsByUser(String userId, int hours);

    // Burst score displayed as Reviews/30m in moderation UI.
    int countRecentReviewsByUserMinutes(String userId, int minutes);

    // Used for basic duplicate detection in moderation evidence pack.
    int countDuplicatesByExactContent(String reviewId, String content);

    // Used for showing edit history count in moderation evidence pack.
    int countEditsByReview(String reviewId);

    List<application.dto.AlertDashboardDTO> getFlaggedReviewsWithAlerts();
}
