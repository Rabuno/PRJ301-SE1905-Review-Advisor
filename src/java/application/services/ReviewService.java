package application.services;

import application.ports.IReviewRepository;
import application.ports.IUserRepository;
import application.ports.IAlertRepository;
import application.ports.IFileStoragePort;
import domain.entities.Review;
import domain.entities.User;
import domain.entities.Alert;
import domain.enums.ReviewStatus;
import java.io.InputStream;
import java.util.List;

public class ReviewService {

    private final IReviewRepository reviewRepository;
    private final IUserRepository userRepository;
    private final IAlertRepository alertRepository;
    private final TriageService triageService;
    private final IFileStoragePort storagePort;

    // Cập nhật Constructor để tiêm (Inject) đủ các Repository cần thiết
    public ReviewService(IReviewRepository reviewRepository,
            IUserRepository userRepository,
            IAlertRepository alertRepository,
            TriageService triageService,
            IFileStoragePort storagePort) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.alertRepository = alertRepository;
        this.triageService = triageService;
        this.storagePort = storagePort;
    }

    public boolean submitReview(Review review, User user, InputStream imageStream, String extension) throws Exception {
        // 1. Xử lý lưu trữ tệp tin (I/O Operation) trước khi can thiệp cơ sở dữ liệu
        if (imageStream != null && extension != null && !extension.isEmpty()) {
            String imageUrl = storagePort.saveFile(imageStream, extension);
            review.setImageUrl(imageUrl);
        }

        // 2. Chuyển giao thực thể đã được làm giàu (enriched entity) cho luồng xử lý lõi
        return submitReviewAI(review, user);
    }

    private boolean submitReviewAI(Review review, User user) {
        // 1. Thu thập Siêu dữ liệu (Metadata) cho Mô hình Đa biến
        double accountAgeDays = calculateAccountAge(user);
        // Giả sử đếm số review trong 1 giờ qua để tính Burst Rate
        double burstRate = (double) reviewRepository.countRecentReviewsByUser(review.getUserId(), 1);

        // 2. Chạy Trí tuệ Nhân tạo sàng lọc (AI Triage) an toàn
        Alert alert = null;
        try {
            alert = triageService.evaluateReview(review, accountAgeDays, burstRate);
        } catch (Exception e) {
            System.err.println("Lỗi AI Evaluation: " + e.getMessage());
            review.setStatus(ReviewStatus.PENDING); // An toàn
        }

        // 3. Lưu trữ Đánh giá
        boolean isSaved = reviewRepository.saveReviewWithAlert(review, alert);
        if (!isSaved) {
            System.err.println("Giao dịch CSDL thất bại đối với Review ID: " + review.getReviewId());
            // Có thể bổ sung cơ chế Retry hoặc thông báo lỗi cho người dùng ở đây
        }
        return isSaved;
    }

    // --- CÁC HÀM PHỤ TRỢ (HELPER METHODS) ---
    private double calculateAccountAge(User user) {

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
        return reviewRepository.findByStatus(ReviewStatus.FLAGGED);
    }

    // Xử lý quyết định của con người (Approve/Reject)
    public void moderateReview(String reviewId, ReviewStatus newStatus) {
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

    // Bổ sung luồng gọi DTO phục vụ Moderator Dashboard
    public List<application.dto.AlertDashboardDTO> getFlaggedReviewsForDashboard() {
        return reviewRepository.getFlaggedReviewsWithAlerts();
    }
}
