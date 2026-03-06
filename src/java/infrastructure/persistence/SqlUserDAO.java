package infrastructure.persistence;

import application.ports.IUserRepository;
import domain.entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlUserDAO implements IUserRepository {

    @Override
    public User findByUsername(String username) {

<<<<<<< HEAD
        String sql = "SELECT u.user_id, u.username, u.password, u.role_id, p.permission_code " +
=======
        String sql = "SELECT u.user_id, u.username, u.password, u.role_id, r.role_name, p.permission_code " +
>>>>>>> c4df0400614ac4ae40671795dbe5ab25b2f48250
                "FROM Users u " +
                "INNER JOIN Roles r ON u.role_id = r.role_id " +
                "LEFT JOIN RolePerm rp ON r.role_id = rp.role_id " +
                "LEFT JOIN Permissions p ON rp.permission_id = p.permission_id " +
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
                                rs.getString("password"));
<<<<<<< HEAD
=======
                        user.setRoleId(rs.getString("role_id"));
                        user.setRole(rs.getString("role_name"));
>>>>>>> c4df0400614ac4ae40671795dbe5ab25b2f48250
                    }

                    String perm = rs.getString("permission_code");
                    if (perm != null) {
                        permissions.add(perm);
                    }
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

    @Override
    public boolean registerUser(User user, String roleName) {
        String sql = "INSERT INTO Users (user_id, username, password, role_id) " +
                "VALUES (?, ?, ?, (SELECT role_id FROM Roles WHERE role_name = ?))";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, roleName);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role_id FROM Users";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role_id")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean updateUserRole(String userId, String roleId) {
        String sql = "UPDATE Users SET role_id = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleId);
            stmt.setString(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}