package infrastructure.persistence;

import domain.entities.Review;
import domain.enums.ReviewStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlReviewDAO {

    // ==============================
    // 1. Lấy review theo status
    // ==============================
    public List<Review> findByStatus(ReviewStatus status) {

        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM Reviews WHERE status = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String reviewId = rs.getString("review_id");
                String productId = rs.getString("product_id");
                String userId = rs.getString("user_id");
                String content = rs.getString("content");
                int rating = rs.getInt("rating");

                ReviewStatus reviewStatus =
                        ReviewStatus.valueOf(rs.getString("status"));

                LocalDateTime createdAt =
                        rs.getTimestamp("created_at").toLocalDateTime();

                Review review = new Review(
                        reviewId,
                        productId,
                        userId,
                        content,
                        rating,
                        reviewStatus,
                        createdAt
                );

                list.add(review);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ==============================
    // 2. Update status
    // ==============================
    public void updateStatus(String reviewId, ReviewStatus newStatus) {

        String sql = "UPDATE Reviews SET status = ?, updated_at = GETDATE() WHERE review_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.name());
            ps.setString(2, reviewId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}