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
    public boolean save(Review review) {
        String sql = "INSERT INTO Reviews (review_id, product_id, user_id, rating, content, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về boolean cho ReviewService

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
    public void deleteReview(String reviewId) {
        // Soft delete bằng cách chuyển status thành HIDDEN thay vì xóa vật lý
        this.updateStatus(reviewId, ReviewStatus.HIDDEN);
    }

    @Override
    public Review findById(String reviewId) {
        String sql = "SELECT * FROM Reviews WHERE review_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Review(
                            rs.getString("review_id"),
                            rs.getString("product_id"),
                            rs.getString("user_id"),
                            rs.getString("content"),
                            rs.getInt("rating"),
                            ReviewStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Review> getReviewHistory(String reviewId) {
        List<Review> list = new ArrayList<>();
        // Sử dụng hàm JSON_VALUE của SQL Server để lấy dữ liệu từ AuditLog mà không cần
        // thư viện ngoài
        String sql = "SELECT " +
                "  JSON_VALUE(diff_json, '$.reviewId') AS review_id, " +
                "  JSON_VALUE(diff_json, '$.productId') AS product_id, " +
                "  JSON_VALUE(diff_json, '$.userId') AS user_id, " +
                "  JSON_VALUE(diff_json, '$.content') AS content, " +
                "  CAST(JSON_VALUE(diff_json, '$.rating') AS INT) AS rating, " +
                "  JSON_VALUE(diff_json, '$.status') AS status, " +
                "  [timestamp] AS created_at " +
                "FROM AuditLog " +
                "WHERE JSON_VALUE(diff_json, '$.reviewId') = ? " +
                "ORDER BY [timestamp] DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String rId = rs.getString("review_id");
                    if (rId != null) {
                        list.add(new Review(
                                rId,
                                rs.getString("product_id"),
                                rs.getString("user_id"),
                                rs.getString("content"),
                                rs.getInt("rating"),
                                ReviewStatus.valueOf(rs.getString("status")),
                                rs.getTimestamp("created_at").toLocalDateTime()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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

    @Override
    public Object[] getReviewStatsByMerchant(String merchantId) {
        String sql = "SELECT " +
                "  AVG(CAST(r.rating AS FLOAT)) as avg_rating, " +
                "  SUM(CASE WHEN r.status = 'PUBLISHED' THEN 1 ELSE 0 END) as published_count, " +
                "  SUM(CASE WHEN r.status = 'FLAGGED' THEN 1 ELSE 0 END) as flagged_count " +
                "FROM Reviews r " +
                "JOIN Products p ON r.product_id = p.product_id " +
                "WHERE p.merchant_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, merchantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avgRating = rs.getDouble("avg_rating");
                    int publishedCount = rs.getInt("published_count");
                    int flaggedCount = rs.getInt("flagged_count");
                    // Format to 1 decimal place if needed, but double is fine
                    return new Object[] { avgRating, publishedCount, flaggedCount };
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object[] { 0.0, 0, 0 };
    }

    @Override
    public List<Review> getRecentReviewsByMerchant(String merchantId, int limit) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT TOP (?) r.* FROM Reviews r " +
                "JOIN Products p ON r.product_id = p.product_id " +
                "WHERE p.merchant_id = ? " +
                "ORDER BY r.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setString(2, merchantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Review(
                            rs.getString("review_id"),
                            rs.getString("product_id"),
                            rs.getString("user_id"),
                            rs.getString("content"),
                            rs.getInt("rating"),
                            ReviewStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("created_at").toLocalDateTime()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int countRecentReviewsByUser(String userId, int hours) {
        int count = 0;
        // Logic truy vấn SQL Server: Đếm số lượng review trong X giờ qua
        String sql = "SELECT COUNT(*) AS total_reviews FROM Reviews "
                + "WHERE user_id = ? AND created_at >= DATEADD(hour, -?, GETDATE())";

        try (java.sql.Connection conn = DBConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setInt(2, hours);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("total_reviews");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi tính toán Burst Rate trong SqlReviewDAO: " + e.getMessage());
        }
        return count;
    }
}
