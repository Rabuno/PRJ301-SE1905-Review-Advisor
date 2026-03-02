package infrastructure.persistence;

import application.ports.IUserRepository;
import domain.entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlUserDAO implements IUserRepository {

    @Override
public User findByUsername(String username) {

    String sql =
            "SELECT u.user_id, u.username, u.password, u.role, p.permission_code " +
            "FROM Users u " +
            "INNER JOIN Roles r ON u.role = r.role_id " +
            "INNER JOIN RolePerm rp ON r.role_id = rp.role_id " +
            "INNER JOIN Permissions p ON rp.permission_id = p.permission_id " +
            "WHERE u.username = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, username);

        try (ResultSet rs = stmt.executeQuery()) {

            User user = null;
            List<String> permissions = new ArrayList<>();

            while (rs.next()) {

                if (user == null) {
                    user = new User(
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }

                permissions.add(rs.getString("permission_code"));
            }

            if (user != null) {
                user.setPermissions(permissions);

                System.out.println("User: " + username);
                System.out.println("Permissions: " + permissions);

                return user;
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}
}