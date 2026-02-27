package domain.entities;

import domain.enums.RoleType;

public class User {
    private String userId;
    private String username;
    private String password;
    private RoleType role;

    public User(String userId, String username, String password, RoleType role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public RoleType getRole() { return role; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}