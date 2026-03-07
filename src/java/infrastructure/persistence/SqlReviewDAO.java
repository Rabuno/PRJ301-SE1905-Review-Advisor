package infrastructure.persistence;

import application.ports.IReviewRepository;
import domain.entities.Review;
import domain.enums.ReviewStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SqlReviewDAO implements IReviewRepository {

    @Override
    public void insert(Review review) {
        String sql = "INSERT INTO Reviews (review_id, product_id, user_id, rating, content, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, review.getReviewId());
            ps.setString(2, review.getProductId());
            ps.setString(3, review.getUserId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getContent());
            ps.setString(6, review.getStatus().name()); // Chuyển Enum thành chuỗi

            // Xử lý thời gian từ Java LocalDateTime sang SQL DATETIME
            Timestamp timestamp = (review.getCreatedAt() != null)
                    ? Timestamp.valueOf(review.getCreatedAt())
                    : new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(7, timestamp);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi lưu Review vào CSDL: " + e.getMessage());
        }
    }

    @Override
    public List<Review> findByStatus(ReviewStatus status) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM Reviews WHERE status = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review(
                            rs.getString("review_id"),
                            rs.getString("product_id"),
                            rs.getString("user_id"),
                            rs.getString("content"),
                            rs.getInt("rating"),
                            ReviewStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("created_at").toLocalDateTime());
                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateStatus(String reviewId, ReviewStatus newStatus) {
        String sql = "UPDATE Reviews SET status = ?, updated_at = GETDATE() WHERE review_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.name());
            ps.setString(2, reviewId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi cập nhật trạng thái Review: " + e.getMessage());
        }
    }

    @Override
    public Review findById(String reviewId) {
        throw new UnsupportedOperationException(
                "Chưa triển khai logic lấy chi tiết Review. Nhiệm vụ của Thành viên 1.");
    }

    @Override
    public List<Review> getReviewHistory(String reviewId) {
        throw new UnsupportedOperationException("Chưa triển khai logic lấy lịch sử Audit. Nhiệm vụ của Thành viên 1.");
    }

    @Override
    public List<Review> findByProductId(String productId) {
        List<Review> list = new ArrayList<>();
        // Chỉ lấy các review đã được PUBLISHED cho user xem
        String sql = "SELECT * FROM Reviews WHERE product_id = ? AND status = 'PUBLISHED' ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review(
                            rs.getString("review_id"),
                            rs.getString("product_id"),
                            rs.getString("user_id"),
                            rs.getString("content"),
                            rs.getInt("rating"),
                            ReviewStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("created_at").toLocalDateTime());
                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}