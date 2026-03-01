package application.services;

import domain.entities.Review;
import domain.enums.ReviewStatus;
import infrastructure.persistence.SqlReviewDAO;

import java.util.List;

public class ReviewService {

    private final SqlReviewDAO reviewDAO = new SqlReviewDAO();

    // Lấy danh sách review cần moderation
    public List<Review> getFlaggedReviews() {
        return reviewDAO.findByStatus(ReviewStatus.FLAGGED);
    }

    // Moderator xử lý review
    public void moderateReview(String reviewId, ReviewStatus newStatus) {
        reviewDAO.updateStatus(reviewId, newStatus);
    }
}