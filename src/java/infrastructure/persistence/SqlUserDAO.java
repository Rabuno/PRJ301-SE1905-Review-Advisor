package infrastructure.persistence;

import application.ports.IUserRepository;
import domain.entities.User;
import domain.enums.RoleType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SqlUserDAO implements IUserRepository {

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, password, role FROM Users WHERE username = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Chuyển đổi chuỗi role từ SQL thành Enum RoleType
                    RoleType role = RoleType.valueOf(rs.getString("role").toUpperCase());
                    User user = new User(
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            role
                    );
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Cần thay bằng hệ thống logging thực tế
        }
        return null; // Không tìm thấy tài khoản
    }
}
