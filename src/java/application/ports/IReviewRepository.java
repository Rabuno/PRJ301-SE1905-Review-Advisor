package application.ports;

import domain.entities.Review;
import java.util.List;

public interface IReviewRepository {
    void insert(Review review);
    void updateStatus(String reviewId, String newStatus);
    Review findById(String reviewId);
    List<Review> getReviewHistory(String reviewId);
}