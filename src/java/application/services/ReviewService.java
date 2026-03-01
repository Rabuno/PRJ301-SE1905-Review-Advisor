package application.services;

import application.ports.IAIService;
import application.ports.IReviewRepository;
import domain.entities.Review;
import domain.enums.ReviewStatus;
import java.util.List;

public class ReviewService {

    private final IReviewRepository reviewRepository;
    private final IAIService aiService;

    // Dependency Injection thông qua Constructor
    public ReviewService(IReviewRepository reviewRepository, IAIService aiService) {
        this.reviewRepository = reviewRepository;
        this.aiService = aiService;
    }

    // Luồng nghiệp vụ cốt lõi: Khách hàng gửi đánh giá mới
    public void submitReview(Review review) {
        // 1. Kích hoạt AI phân tích ngữ nghĩa văn bản
        double riskScore = aiService.calculateRiskScore(review);
        System.out.println("[AI Module] ReviewID: " + review.getReviewId() + " | Khả năng SPAM: " + (riskScore * 100) + "%");

        // 2. Quyết định trạng thái dựa trên điểm rủi ro (Ngưỡng 70%)
        if (riskScore >= 0.7) {
            review.setStatus(ReviewStatus.FLAGGED); // Bị hệ thống giam giữ
        } else {
            review.setStatus(ReviewStatus.PUBLISHED); // Hợp lệ, cho phép hiển thị
        }

        // 3. Đẩy xuống tầng Database để lưu trữ
        reviewRepository.insert(review); 
    }

    public List<Review> getFlaggedReviews() {
        return reviewRepository.findByStatus(ReviewStatus.FLAGGED);
    }

    public void moderateReview(String reviewId, ReviewStatus newStatus) {
        reviewRepository.updateStatus(reviewId, newStatus);
    }
}