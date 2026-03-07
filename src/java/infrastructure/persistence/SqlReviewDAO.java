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
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, review.getReviewId());
            ps.setString(2, review.getProductId());
            ps.setString(3, review.getUserId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getContent());
            ps.setString(6, review.getStatus().name());

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
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review(
                            rs.getString("review_id"),
                            rs.getString("product_id"),
                            rs.getString("user_id"),
                            rs.getString("content"),
                            rs.getInt("rating"),
                            ReviewStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
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
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

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
        throw new UnsupportedOperationException("Chưa triển khai logic lấy chi tiết Review. Nhiệm vụ của Thành viên 1.");
    }

    @Override
    public List<Review> getReviewHistory(String reviewId) {
        throw new UnsupportedOperationException("Chưa triển khai logic lấy lịch sử Audit. Nhiệm vụ của Thành viên 1.");
    }

    @Override
    public int countRecentReviewsByUser(String userId, int hours) {
        int count = 0;
        // Logic truy vấn SQL Server: Đếm số lượng review trong X giờ qua
        String sql = "SELECT COUNT(*) AS total_reviews FROM Reviews "
                + "WHERE user_id = ? AND created_at >= DATEADD(hour, -?, GETDATE())";

        try ( java.sql.Connection conn = DBConnection.getConnection();  java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setInt(2, hours);

            try ( java.sql.ResultSet rs = ps.executeQuery()) {
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
