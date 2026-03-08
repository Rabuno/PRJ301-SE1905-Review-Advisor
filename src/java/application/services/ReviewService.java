package application.services;

import application.ports.IReviewRepository;
import application.ports.IUserRepository;
import application.ports.IAlertRepository;
import domain.entities.Review;
import domain.entities.User;
import domain.entities.Alert;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReviewService {

    private final IReviewRepository reviewRepository;
    private final IUserRepository userRepository;
    private final IAlertRepository alertRepository;
    private final TriageService triageService;

    // Cập nhật Constructor để tiêm (Inject) đủ các Repository cần thiết
    public ReviewService(IReviewRepository reviewRepository,
            IUserRepository userRepository,
            IAlertRepository alertRepository,
            TriageService triageService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.alertRepository = alertRepository;
        this.triageService = triageService;
    }

    public boolean submitReview(Review review, String username) {
        // 1. Thu thập Siêu dữ liệu (Metadata) cho Mô hình Đa biến
        double accountAgeDays = calculateAccountAge(username);
        // Giả sử đếm số review trong 1 giờ qua để tính Burst Rate
        double burstRate = (double) reviewRepository.countRecentReviewsByUser(review.getUserId(), 1);

        // 2. Chạy Trí tuệ Nhân tạo sàng lọc (AI Triage) an toàn
        Alert alert = null;
        try {
            alert = triageService.evaluateReview(review, accountAgeDays, burstRate);
        } catch (Exception e) {
            System.err.println("Lỗi AI Evaluation: " + e.getMessage());
            review.setStatus(domain.enums.ReviewStatus.PENDING); // An toàn
        }

        // 3. Lưu trữ Đánh giá
        boolean isReviewSaved = reviewRepository.save(review);

        // 4. Lưu trữ Gói Cảnh báo (Nếu có)
        if (isReviewSaved && alert != null) {
            try {
                alertRepository.saveAlert(alert);
            } catch (Exception e) {
                System.err.println("Lỗi lưu Alert: " + e.getMessage());
            }
        }
        return isReviewSaved;
    }

    // --- CÁC HÀM PHỤ TRỢ (HELPER METHODS) ---
    private double calculateAccountAge(String username) {
        User user = userRepository.findByUsername(username);

        if (user != null && user.getCreatedAt() != null) {
            // Trích xuất phần ngày (LocalDate) từ LocalDateTime để đối chiếu với LocalDate.now()
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    user.getCreatedAt().toLocalDate(),
                    java.time.LocalDate.now()
            );
            return (double) days;
        }

        return 0.0; // Mặc định là tài khoản mới tạo (0 ngày) nếu có lỗi
    }

    // --- CÁC HÀM DÀNH CHO LUỒNG MODERATOR (KIỂM DUYỆT) ---
    // Lấy danh sách bài bị AI cắm cờ
    public java.util.List<Review> getFlaggedReviews() {
        return reviewRepository.findByStatus(domain.enums.ReviewStatus.FLAGGED);
    }

    // Xử lý quyết định của con người (Approve/Reject)
    public void moderateReview(String reviewId, domain.enums.ReviewStatus newStatus) {
        // Có thể bổ sung logic lưu AuditLog tại đây trong tương lai
        reviewRepository.updateStatus(reviewId, newStatus);
    }

    public void deleteReview(String reviewId) {
        reviewRepository.deleteReview(reviewId);
    }

    public List<Review> getReviewsByProduct(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> getReviewsByUser(String userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Review getReviewById(String reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public Object[] getMerchantReviewStats(String merchantId) {
        return reviewRepository.getReviewStatsByMerchant(merchantId);
    }

    public List<Review> getRecentMerchantReviews(String merchantId, int limit) {
        return reviewRepository.getRecentReviewsByMerchant(merchantId, limit);
    }
}
