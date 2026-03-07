package application.ports;

import domain.entities.Review;
import domain.enums.ReviewStatus;
import java.util.List;

public interface IReviewRepository {

    boolean save(Review review);

    void updateStatus(String reviewId, ReviewStatus newStatus);

    Review findById(String reviewId);

    List<Review> getReviewHistory(String reviewId);

    List<Review> findByStatus(ReviewStatus status);

    int countRecentReviewsByUser(String userId, int hours);
}
