package domain.entities;

import java.time.LocalDateTime;

public class AuditLog {
    private String auditId;
    private String actorUserId;
    private String action;
    private String diffJson;
    private String previousHash;
    private String currentHash;
    private LocalDateTime timestamp;

    public AuditLog(String auditId, String actorUserId, String action, String diffJson, String previousHash) {
        this.auditId = auditId;
        this.actorUserId = actorUserId;
        this.action = action;
        this.diffJson = diffJson;
        this.previousHash = previousHash;
        this.timestamp = LocalDateTime.now();
    }

    // Getters & Setters
    public String getAuditId() { return auditId; }
    
    public String getActorUserId() { return actorUserId; }
    
    public String getAction() { return action; }
    
    public String getDiffJson() { return diffJson; }
    
    public String getPreviousHash() { return previousHash; }
    
    public String getCurrentHash() { return currentHash; }
    
    public LocalDateTime getTimestamp() { return timestamp; }

    // --- Setter duy nhất cho currentHash (được AuditService tính toán sau) ---
    public void setCurrentHash(String currentHash) { this.currentHash = currentHash; }
}
