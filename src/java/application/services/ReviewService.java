package application.services;

import domain.entities.Review;
import domain.enums.ReviewStatus;
import infrastructure.persistence.SqlReviewDAO;
import infrastructure.ai.WekaProvider;
import java.util.List;

public class ReviewService {

    private final SqlReviewDAO reviewDAO;
    private final WekaProvider aiProvider;

    public ReviewService(SqlReviewDAO reviewDAO, WekaProvider aiProvider) {
        this.reviewDAO = reviewDAO;
        this.aiProvider = aiProvider;
    }

    public void submitReview(Review review) {
        double riskScore = aiProvider.calculateRiskScore(review);
        System.out.println(
                "[AI Module] ReviewID: " + review.getReviewId() + " | Khả năng SPAM: " + (riskScore * 100) + "%");

        if (riskScore >= 0.7) {
            review.setStatus(ReviewStatus.FLAGGED);
        } else {
            review.setStatus(ReviewStatus.PUBLISHED);
        }

        reviewDAO.insert(review);
    }

    public List<Review> getFlaggedReviews() {
        return reviewDAO.findByStatus(ReviewStatus.FLAGGED);
    }

    public void moderateReview(String reviewId, ReviewStatus status) {
        reviewDAO.updateStatus(reviewId, status);
    }

    public List<Review> getReviewsByProduct(String productId) {
        return reviewDAO.findByProductId(productId);
    }

    public Object[] getMerchantReviewStats(String merchantId) {
        return reviewDAO.getReviewStatsByMerchant(merchantId);
    }

    public List<Review> getRecentMerchantReviews(String merchantId, int limit) {
        return reviewDAO.getRecentReviewsByMerchant(merchantId, limit);
    }
}