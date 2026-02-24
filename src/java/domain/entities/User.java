package domain.entities;

import domain.enums.RoleType;

public class User {
    private String userId;
    private String username;
    private RoleType role;

    public User(String userId, String username, RoleType role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
    // Getters
    public String getUserId() { return userId; }
    public RoleType getRole() { return role; }
}