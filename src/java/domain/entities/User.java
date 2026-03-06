package domain.entities;

import java.util.List;

public class User {
    private String userId;
    private String username;
    private String password;
    private String roleId;
<<<<<<< HEAD
=======
    private String role;
>>>>>>> c4df0400614ac4ae40671795dbe5ab25b2f48250
    private List<String> permissions;

    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public User(String userId, String username, String password, String roleId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

<<<<<<< HEAD
=======
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

>>>>>>> c4df0400614ac4ae40671795dbe5ab25b2f48250
    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
}