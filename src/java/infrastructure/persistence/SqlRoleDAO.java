package infrastructure.persistence;

import application.ports.IRoleRepository;
import domain.entities.Permission;
import domain.entities.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SqlRoleDAO implements IRoleRepository {

    @Override
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT role_id, role_name FROM Roles";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roles.add(new Role(rs.getString("role_id"), rs.getString("role_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public List<Permission> getAllPermissions() {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT permission_id, permission_code FROM Permissions";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                permissions.add(new Permission(rs.getInt("permission_id"), rs.getString("permission_code")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permissions;
    }

    @Override
    public List<Integer> getRolePermissions(String roleId) {
        List<Integer> permissionIds = new ArrayList<>();
        String sql = "SELECT permission_id FROM RolePerm WHERE role_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissionIds.add(rs.getInt("permission_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permissionIds;
    }

    @Override
    public boolean updateRolePermissions(String roleId, List<Integer> permissionIds) {
        String deleteSql = "DELETE FROM RolePerm WHERE role_id = ?";
        String insertSql = "INSERT INTO RolePerm (role_id, permission_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, roleId);
                deleteStmt.executeUpdate();
            }

            if (permissionIds != null && !permissionIds.isEmpty()) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    for (Integer permId : permissionIds) {
                        insertStmt.setString(1, roleId);
                        insertStmt.setInt(2, permId);
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
