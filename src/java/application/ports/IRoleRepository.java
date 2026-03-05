package application.ports;

import domain.entities.Permission;
import domain.entities.Role;
import java.util.List;

public interface IRoleRepository {
    List<Role> getAllRoles();

    List<Permission> getAllPermissions();

    List<Integer> getRolePermissions(String roleId);

    boolean updateRolePermissions(String roleId, List<Integer> permissionIds);
}
