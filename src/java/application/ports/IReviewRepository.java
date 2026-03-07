package application.ports;

import domain.entities.Review;
import domain.enums.ReviewStatus;
import java.util.List;

public interface IReviewRepository {
<<<<<<< HEAD
    void insert(Review review);
=======

    boolean save(Review review);
>>>>>>> main

    void updateStatus(String reviewId, ReviewStatus newStatus);

    Review findById(String reviewId);

    List<Review> getReviewHistory(String reviewId);

    List<Review> findByStatus(ReviewStatus status);

    List<Review> findByProductId(String productId);

<<<<<<< HEAD
    // Merchant Dashboard methods
    Object[] getReviewStatsByMerchant(String merchantId);

    List<Review> getRecentReviewsByMerchant(String merchantId, int limit);
}
=======
    Object[] getReviewStatsByMerchant(String merchantId);

    List<Review> getRecentReviewsByMerchant(String merchantId, int limit);

    int countRecentReviewsByUser(String userId, int hours);
}
>>>>>>> main
