package infrastructure.persistence;

import application.ports.IUserRepository;
import domain.entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SqlUserDAO implements IUserRepository {

    // Cache permissions by role_id to avoid expensive joins on every login.
    // This keeps connection usage the same (still DriverManager), but reduces query time.
    private static final ConcurrentHashMap<Integer, PermCacheEntry> PERM_CACHE = new ConcurrentHashMap<>();
    private static final long PERM_CACHE_TTL_MS = 5 * 60 * 1000; // 5 minutes

    private static class PermCacheEntry {
        final List<String> permissions;
        final long loadedAtMs;

        PermCacheEntry(List<String> permissions, long loadedAtMs) {
            this.permissions = permissions;
            this.loadedAtMs = loadedAtMs;
        }
    }

    public static void invalidatePermissionCache() {
        PERM_CACHE.clear();
    }

    @Override
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        // Base user query (1 row). Permissions are loaded separately to avoid row explosion.
        String sql = "SELECT u.user_id, u.username, u.password, u.role_id, u.created_at, r.role_name "
                + "FROM Users u "
                + "INNER JOIN Roles r ON u.role_id = r.role_id "
                + "WHERE u.username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                User user = new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                String roleIdStr = rs.getString("role_id");
                user.setRoleId(roleIdStr);
                user.setRole(rs.getString("role_name"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    user.setCreatedAt(createdAt.toLocalDateTime());
                }

                int roleId = 0;
                try {
                    roleId = Integer.parseInt(roleIdStr);
                } catch (Exception ignore) {
                    roleId = 0;
                }

                // Reuse the same connection (no change in connection strategy; just fewer rows).
                user.setPermissions(loadPermissionsForRole(conn, roleId));
                return user;
            }
        } catch (Exception e) {
            System.err.println("[SqlUserDAO] Lỗi tại findByUsername: " + e.getMessage());
            throw new RuntimeException("Lỗi truy xuất dữ liệu người dùng", e);
        }
    }

    private List<String> loadPermissionsForRole(Connection conn, int roleId) {
        if (roleId <= 0) return new ArrayList<>();

        long now = System.currentTimeMillis();
        PermCacheEntry cached = PERM_CACHE.get(roleId);
        if (cached != null && (now - cached.loadedAtMs) <= PERM_CACHE_TTL_MS) {
            return new ArrayList<>(cached.permissions);
        }

        String sql = "SELECT p.permission_code "
                + "FROM RolePerm rp "
                + "INNER JOIN Permissions p ON rp.permission_id = p.permission_id "
                + "WHERE rp.role_id = ?";

        Set<String> permissions = new LinkedHashSet<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String perm = rs.getString("permission_code");
                    if (perm != null) permissions.add(perm);
                }
            }
        } catch (Exception e) {
            System.err.println("[SqlUserDAO] Lỗi loadPermissionsForRole: " + e.getMessage());
        }

        List<String> list = new ArrayList<>(permissions);
        PERM_CACHE.put(roleId, new PermCacheEntry(list, now));
        return new ArrayList<>(list);
    }

    public User findById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId cannot be null or empty");
        }

        String sql = "SELECT u.user_id, u.username, u.password, u.role_id, u.created_at, r.role_name "
                + "FROM Users u "
                + "INNER JOIN Roles r ON u.role_id = r.role_id "
                + "WHERE u.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                User user = new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                user.setRoleId(rs.getString("role_id"));
                user.setRole(rs.getString("role_name"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    user.setCreatedAt(createdAt.toLocalDateTime());
                }

                // Permissions are intentionally not loaded here (this method is used for lightweight lookups).
                return user;
            }
        } catch (Exception e) {
            System.err.println("[SqlUserDAO] Lỗi tại findById: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean registerUser(User user, String roleName) {
        if (user == null || roleName == null) {
            throw new IllegalArgumentException("User and roleName cannot be null");
        }

        // Khắc phục lỗ hổng Subquery
        String sql = "INSERT INTO Users (user_id, username, password, role_id) "
                + "VALUES (?, ?, ?, (SELECT role_id FROM Roles WHERE role_name = ?))";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, roleName);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Không nuốt ngoại lệ, in lỗi rõ ràng để debug (VD: lỗi duplicate key)
            System.err.println("[SqlUserDAO] SQL Lỗi chèn dữ liệu registerUser: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[SqlUserDAO] Hệ thống lỗi tại registerUser: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        // Khắc phục khuyết thiếu dữ liệu bằng INNER JOIN
        String sql = "SELECT u.user_id, u.username, u.password, u.role_id, r.role_name, u.created_at "
                   + "FROM Users u "
                   + "INNER JOIN Roles r ON u.role_id = r.role_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role_id")
                );
                
                // Nạp thêm vai trò để tránh NullPointerException trên View
                user.setRole(rs.getString("role_name"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    user.setCreatedAt(createdAt.toLocalDateTime());
                }

                users.add(user);
            }
        } catch (Exception e) {
            System.err.println("[SqlUserDAO] Lỗi tại getAllUsers: " + e.getMessage());
        }
        return users;
    }

    @Override
    public boolean updateUserRole(String userId, String roleId) {
        if (userId == null || roleId == null) {
            throw new IllegalArgumentException("UserId and roleId cannot be null");
        }

        String sql = "UPDATE Users SET role_id = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleId);
            stmt.setString(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[SqlUserDAO] Lỗi tại updateUserRole: " + e.getMessage());
            return false;
        }
    }
}
