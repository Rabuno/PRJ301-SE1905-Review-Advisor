package domain.entities;

public class Permission {
    private int permissionId;
    private String permissionCode;

    public Permission() {
    }

    public Permission(int permissionId, String permissionCode) {
        this.permissionId = permissionId;
        this.permissionCode = permissionCode;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }
}
