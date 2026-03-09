package domain.entities;

import java.time.LocalDateTime;

public class Review {

    private String reviewId;
    private String productId;
    private String userId;
    private String content;
    private int rating;
    private String imageUrl;
    private ReviewStatus status;
    private LocalDateTime createdAt;

    // Constructor phuc vu viec tao Review moi tu Customer
    public Review(String reviewId, String productId, String userId, String content, int rating, String imageUrl) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.status = ReviewStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor tuong thich nguoc khi khong co anh
    public Review(String reviewId, String productId, String userId, String content, int rating) {
        this(reviewId, productId, userId, content, rating, null);
    }

    // Constructor tai tao doi tuong tu DB (day du)
    public Review(String reviewId, String productId, String userId, String content, int rating, String imageUrl,
            ReviewStatus status, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Constructor tuong thich nguoc cho DB khong co image_url
    public Review(String reviewId, String productId, String userId, String content, int rating, ReviewStatus status,
            LocalDateTime createdAt) {
        this(reviewId, productId, userId, content, rating, null, status, createdAt);
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getProductId() {
        return productId;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public int getRating() {
        return rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
