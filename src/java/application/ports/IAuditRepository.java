package application.ports;

import domain.entities.AuditLog;

public interface IAuditRepository {
    // Lưu bản ghi audit mới vào hệ thống lưu trữ
    void insertLog(AuditLog log);
    
    // Truy xuất mã băm của bản ghi cuối cùng để nối chuỗi (HashChain)
    String getLastLogHash(); 
}
