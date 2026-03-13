package application.services;

import application.ports.IAuditRepository;
import domain.entities.AuditLog;
import application.util.HashUtil;
import java.util.UUID;

public class AuditService {
    private final IAuditRepository auditRepository;

    // Dependency Injection qua Constructor
    public AuditService(IAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    /**
     * Ghi nhận một hành động nhạy cảm vào hệ thống Audit với cơ chế HashChain.
     */
    public void logAction(String actorUserId, String action, String diffJson) {
        // 1. Sinh ID duy nhất
        String auditId = UUID.randomUUID().toString();
        
        // 2. Lấy mã băm của bản ghi trước đó (H_n-1) từ Repository
        String previousHash = auditRepository.getLastLogHash();
        if (previousHash == null || previousHash.isEmpty()) {
            previousHash = "GENESIS_BLOCK_HASH_00000000000000000000"; // Bản ghi đầu tiên
        }

        // 3. Khởi tạo Entity
        AuditLog newLog = new AuditLog(auditId, actorUserId, action, diffJson, previousHash);

        // 4. Tính toán mã băm hiện tại (H_n)
        String dataToHash = newLog.getAuditId() + 
                            newLog.getActorUserId() + 
                            newLog.getAction() + 
                            newLog.getDiffJson() + 
                            newLog.getPreviousHash();
        
        String currentHash = HashUtil.generateSHA256(dataToHash);
        newLog.setCurrentHash(currentHash);

        // 5. Lưu xuống cơ sở dữ liệu thông qua Port
        auditRepository.insertLog(newLog);
    }
}
