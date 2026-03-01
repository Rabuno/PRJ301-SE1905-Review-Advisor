package domain.entities;

import domain.enums.ReviewStatus;
import java.time.LocalDateTime;

public class Review {
    private String reviewId;
    private String productId;
    private String userId;
    private String content;
    private int rating;
    private ReviewStatus status;
    private LocalDateTime createdAt;

    // Constructor phục vụ việc tạo Review mới từ Customer
    public Review(String reviewId, String productId, String userId, String content, int rating) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
        // Logic nghiệp vụ lõi: Mọi review mới đều phải ở trạng thái PENDING
        this.status = ReviewStatus.PENDING; 
        this.createdAt = LocalDateTime.now();
    }

    // Constructor phục vụ việc tái tạo đối tượng từ cơ sở dữ liệu
    public Review(String reviewId, String productId, String userId, String content, int rating, ReviewStatus status, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public String getReviewId() { return reviewId; }
    public String getContent() { return content; }
    public int getRating() { return rating; }
    public ReviewStatus getStatus() { return status; }
    public void setStatus(ReviewStatus status) { this.status = status; }
    public String getProductId() { return productId; }
    public String getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}