package domain.entities;

import domain.enums.Status;
import java.time.LocalDateTime;

public class Review {

    private String reviewId;
    private String productId;
    private String userId;
    private String content;
    private int rating;
    private Status status;
    private LocalDateTime createdAt;

    // Constructor phục vụ việc tạo Review mới từ Customer
    public Review(String reviewId, String productId, String userId, String content, int rating) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor phục vụ việc tái tạo đối tượng từ cơ sở dữ liệu
    public Review(String reviewId, String productId, String userId, String content, int rating, Status status, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public String getReviewId() {
        return reviewId;
    }

    public String getContent() {
        return content;
    }

    public int getRating() {
        return rating;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getProductId() {
        return productId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
