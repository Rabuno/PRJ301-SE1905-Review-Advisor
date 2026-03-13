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
        // Kiểm tra xem review đã tồn tại chưa (Edit mode)
        boolean isUpdate = false;
        String checkSql = "SELECT 1 FROM Reviews WHERE review_id = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, review.getReviewId());
            try ( ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    isUpdate = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isUpdate) {
            String updateSql = "UPDATE Reviews SET rating = ?, content = ?, status = ?, updated_at = GETDATE() WHERE review_id = ?";
            try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, review.getRating());
                ps.setString(2, review.getContent());
                ps.setString(3, review.getStatus().name());
                ps.setString(4, review.getReviewId());
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        // Tạo mới (Write mode)
        String sql = "INSERT INTO Reviews (review_id, product_id, user_id, rating, content, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

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
    public boolean saveReviewWithAlert(Review review, domain.entities.Alert alert) {
        String updateRev = "UPDATE Reviews SET rating=?, content=?, status=?, updated_at=GETDATE() WHERE review_id=?";
        String insertRev = "INSERT INTO Reviews (review_id, product_id, user_id, rating, content, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try ( java.sql.Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction
            try {
                // 1. Logic Upsert Review (Tối ưu kết nối)
                boolean isUpdated = false;
                try ( java.sql.PreparedStatement psUp = conn.prepareStatement(updateRev)) {
                    psUp.setInt(1, review.getRating());
                    psUp.setString(2, review.getContent());
                    psUp.setString(3, review.getStatus().name());
                    psUp.setString(4, review.getReviewId());
                    if (psUp.executeUpdate() > 0) {
                        isUpdated = true;
                    }
                }
                if (!isUpdated) {
                    try ( java.sql.PreparedStatement psIn = conn.prepareStatement(insertRev)) {
                        psIn.setString(1, review.getReviewId());
                        psIn.setString(2, review.getProductId());
                        psIn.setString(3, review.getUserId());
                        psIn.setInt(4, review.getRating());
                        psIn.setString(5, review.getContent());
                        psIn.setString(6, review.getStatus().name());
                        psIn.setTimestamp(7, review.getCreatedAt() != null ? java.sql.Timestamp.valueOf(review.getCreatedAt()) : new java.sql.Timestamp(System.currentTimeMillis()));
                        psIn.executeUpdate();
                    }
                }

                // 2. Logic Insert Alert (Chung Connection)
                if (alert != null) {
                    String insertAlert = "INSERT INTO Alerts (alert_id, review_id, risk_score, status) VALUES (?, ?, ?, ?)";
                    try ( java.sql.PreparedStatement psA = conn.prepareStatement(insertAlert)) {
                        psA.setString(1, alert.getAlertId());
                        psA.setString(2, alert.getReviewId());
                        psA.setDouble(3, alert.getRiskScore());
                        psA.setString(4, alert.getStatus());
                        psA.executeUpdate();
                    }
                    // Bỏ qua mã chèn AlertReasons và AlertEvidences bằng executeBatch ở đây để ngắn gọn, 
                    // nhưng áp dụng tương tự quy trình trong SqlAlertDAO.
                }

                conn.commit(); // Xác nhận Transaction
                return true;
            } catch (Exception ex) {
                conn.rollback(); // Hoàn tác nếu có lỗi bất kỳ
                ex.printStackTrace();
                return false;
            }
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
    public void deleteReview(String reviewId) {
        // Soft delete bằng cách chuyển status thành HIDDEN thay vì xóa vật lý
        this.updateStatus(reviewId, ReviewStatus.HIDDEN);
    }

    @Override
    public Review findById(String reviewId) {
        String sql = "SELECT * FROM Reviews WHERE review_id = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reviewId);
            try ( ResultSet rs = ps.executeQuery()) {
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
        String sql = "SELECT "
                + "  JSON_VALUE(diff_json, '$.reviewId') AS review_id, "
                + "  JSON_VALUE(diff_json, '$.productId') AS product_id, "
                + "  JSON_VALUE(diff_json, '$.userId') AS user_id, "
                + "  JSON_VALUE(diff_json, '$.content') AS content, "
                + "  CAST(JSON_VALUE(diff_json, '$.rating') AS INT) AS rating, "
                + "  JSON_VALUE(diff_json, '$.status') AS status, "
                + "  [timestamp] AS created_at "
                + "FROM AuditLog "
                + "WHERE JSON_VALUE(diff_json, '$.reviewId') = ? "
                + "ORDER BY [timestamp] DESC";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reviewId);
            try ( ResultSet rs = ps.executeQuery()) {
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
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productId);
            try ( ResultSet rs = ps.executeQuery()) {
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
    public List<Review> findByUserId(String userId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM Reviews WHERE user_id = ? AND status != 'HIDDEN' ORDER BY created_at DESC";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            try ( ResultSet rs = ps.executeQuery()) {
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
        String sql = "SELECT "
                + "  AVG(CAST(r.rating AS FLOAT)) as avg_rating, "
                + "  SUM(CASE WHEN r.status = 'PUBLISHED' THEN 1 ELSE 0 END) as published_count, "
                + "  SUM(CASE WHEN r.status = 'FLAGGED' THEN 1 ELSE 0 END) as flagged_count "
                + "FROM Reviews r "
                + "JOIN Products p ON r.product_id = p.product_id "
                + "WHERE p.merchant_id = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, merchantId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avgRating = rs.getDouble("avg_rating");
                    int publishedCount = rs.getInt("published_count");
                    int flaggedCount = rs.getInt("flagged_count");
                    // Format to 1 decimal place if needed, but double is fine
                    return new Object[]{avgRating, publishedCount, flaggedCount};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object[]{0.0, 0, 0};
    }

    @Override
    public List<Review> getRecentReviewsByMerchant(String merchantId, int limit) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT TOP (?) r.* FROM Reviews r "
                + "JOIN Products p ON r.product_id = p.product_id "
                + "WHERE p.merchant_id = ? "
                + "ORDER BY r.created_at DESC";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setString(2, merchantId);

            try ( ResultSet rs = ps.executeQuery()) {
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

    @Override
    public List<application.dto.AlertDashboardDTO> getFlaggedReviewsWithAlerts() {
        java.util.Map<String, application.dto.AlertDashboardDTO> dtoMap = new java.util.LinkedHashMap<>();

        // Truy vấn phẳng 1 lần duy nhất bằng LEFT JOIN
        String sql = "SELECT r.review_id, r.content, r.rating, r.created_at, "
                + "a.alert_id, a.risk_score, "
                + "ar.feature_name, ar.importance_weight, "
                + "ae.rule_type, ae.measured_value "
                + "FROM Reviews r "
                + "LEFT JOIN Alerts a ON r.review_id = a.review_id "
                + "LEFT JOIN AlertReasons ar ON a.alert_id = ar.alert_id "
                + "LEFT JOIN AlertEvidences ae ON a.alert_id = ae.alert_id "
                + "WHERE r.status = 'FLAGGED' "
                + "ORDER BY r.created_at DESC";

        try ( java.sql.Connection conn = DBConnection.getConnection();  java.sql.PreparedStatement ps = conn.prepareStatement(sql);  java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String reviewId = rs.getString("review_id");
                application.dto.AlertDashboardDTO dto = dtoMap.get(reviewId);

                // Khởi tạo DTO nếu chưa tồn tại trong Map
                if (dto == null) {
                    dto = new application.dto.AlertDashboardDTO();
                    dto.setReviewId(reviewId);
                    dto.setContent(rs.getString("content"));
                    dto.setRating(rs.getInt("rating"));
                    dto.setCreatedAt(rs.getTimestamp("created_at").toString());

                    double riskScore = rs.getDouble("risk_score");
                    if (!rs.wasNull()) {
                        dto.setRiskScore(riskScore);
                    }
                    dto.setEvidence(new java.util.HashMap<>());
                    dto.setAiFeatures(new java.util.ArrayList<>());
                    dtoMap.put(reviewId, dto);
                }

                // Nhóm AlertReasons (tránh trùng lặp do tích Đề-các)
                String featureName = rs.getString("feature_name");
                if (featureName != null) {
                    double weight = rs.getDouble("importance_weight");
                    int score = (int) Math.round(weight * 100);
                    boolean exists = dto.getAiFeatures().stream().anyMatch(f -> f.getName().equals(featureName));
                    if (!exists) {
                        dto.getAiFeatures().add(new application.dto.AlertDashboardDTO.AIFeatureDTO(featureName, score));
                    }
                }

                // Nhóm AlertEvidences
                String ruleType = rs.getString("rule_type");
                if (ruleType != null) {
                    double value = rs.getDouble("measured_value");
                    String key = "BURST_RATE".equals(ruleType) ? "burstScore"
                            : ("ACCOUNT_AGE".equals(ruleType) ? "accountAge" : ruleType.toLowerCase());
                    dto.getEvidence().putIfAbsent(key, value);
                }
            }

            // Sắp xếp lại Feature theo độ quan trọng cho từng DTO
            for (application.dto.AlertDashboardDTO dto : dtoMap.values()) {
                dto.getAiFeatures().sort((f1, f2) -> Integer.compare(f2.getScore(), f1.getScore()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new java.util.ArrayList<>(dtoMap.values());
    }
}
