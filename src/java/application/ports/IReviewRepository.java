package application.ports;

import domain.entities.Review;
import domain.enums.Status;
import java.util.List;

public interface IReviewRepository {

    boolean save(Review review);

    void updateStatus(String reviewId, Status newStatus);

    Review findById(String reviewId);

    void deleteReview(String reviewId);

    List<Review> getReviewHistory(String reviewId);

    List<Review> findByStatus(Status status);

    List<Review> findByProductId(String productId);

    List<Review> findByUserId(String userId);

    Object[] getReviewStatsByMerchant(String merchantId);

    List<Review> getRecentReviewsByMerchant(String merchantId, int limit);

    int countRecentReviewsByUser(String userId, int hours);
}
