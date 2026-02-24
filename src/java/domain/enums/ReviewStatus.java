package domain.enums;

public enum ReviewStatus {
    PENDING,    // Đang chờ AI đánh giá
    PUBLISHED,  // Đã hiển thị công khai
    HIDDEN,     // Bị ẩn bởi người dùng hoặc hệ thống
    FLAGGED     // Bị AI cảnh báo, chờ Moderator xử lý
}