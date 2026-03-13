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

    List<application.dto.AlertDashboardDTO> getFlaggedReviewsWithAlerts();
}
