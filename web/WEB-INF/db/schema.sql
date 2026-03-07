-- ==============================================================================
-- BẢN THIẾT KẾ CƠ SỞ DỮ LIỆU - DỰ ÁN REVIEW ADVISOR (VER 2.1)
-- Tính năng: Cố định lỗi Batch Compilation + Mở rộng Dữ liệu Test UX/UI
-- ==============================================================================

-- 1. XÓA BẢNG CŨ CÓ KIỂM SOÁT (DROP PHASE)
IF OBJECT_ID('AlertReasons', 'U') IS NOT NULL DROP TABLE AlertReasons;
IF OBJECT_ID('AlertEvidences', 'U') IS NOT NULL DROP TABLE AlertEvidences;
IF OBJECT_ID('Alerts', 'U') IS NOT NULL DROP TABLE Alerts;
IF OBJECT_ID('ReviewEdits', 'U') IS NOT NULL DROP TABLE ReviewEdits;
IF OBJECT_ID('Reviews', 'U') IS NOT NULL DROP TABLE Reviews;
IF OBJECT_ID('Products', 'U') IS NOT NULL DROP TABLE Products;
IF OBJECT_ID('AuditLog', 'U') IS NOT NULL DROP TABLE AuditLog;
IF OBJECT_ID('RolePerm', 'U') IS NOT NULL DROP TABLE RolePerm;
IF OBJECT_ID('UserRole', 'U') IS NOT NULL DROP TABLE UserRole; -- Xóa bảng tàn dư nếu có
IF OBJECT_ID('Users', 'U') IS NOT NULL DROP TABLE Users;
IF OBJECT_ID('Permissions', 'U') IS NOT NULL DROP TABLE Permissions;
IF OBJECT_ID('Roles', 'U') IS NOT NULL DROP TABLE Roles;
GO -- BẮT BUỘC: Ép SSMS thực thi xong việc xóa trước khi biên dịch lệnh tạo

-- 2. TẠO CẤU TRÚC VẬT LÝ (CREATE PHASE)
CREATE TABLE Roles (
    role_id INT IDENTITY(1,1) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description NVARCHAR(255)
);

CREATE TABLE Permissions (
    permission_id INT IDENTITY(1,1) PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(255)
);

CREATE TABLE RolePerm (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES Roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES Permissions(permission_id) ON DELETE CASCADE
);

CREATE TABLE Users (
    user_id VARCHAR(50) PRIMARY KEY, 
    username VARCHAR(100) UNIQUE NOT NULL, 
    password VARCHAR(255) NOT NULL, 
    role_id INT NOT NULL, 
    created_at DATETIME DEFAULT GETDATE(), 
    FOREIGN KEY (role_id) REFERENCES Roles(role_id)
);

CREATE TABLE Products (
    product_id VARCHAR(50) PRIMARY KEY, 
    name NVARCHAR(255) NOT NULL, 
    description NVARCHAR(MAX), 
    price DECIMAL(18,2), 
    merchant_id VARCHAR(50) NOT NULL, 
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DEACTIVATED')), 
    created_at DATETIME DEFAULT GETDATE(), 
    FOREIGN KEY (merchant_id) REFERENCES Users(user_id)
);

CREATE TABLE Reviews (
    review_id VARCHAR(50) PRIMARY KEY, 
    product_id VARCHAR(50) NOT NULL, 
    user_id VARCHAR(50) NOT NULL, 
    rating INT CHECK (rating >= 1 AND rating <= 5), 
    content NVARCHAR(MAX), 
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PUBLISHED', 'HIDDEN', 'FLAGGED', 'DELETED')), 
    created_at DATETIME DEFAULT GETDATE(), 
    updated_at DATETIME DEFAULT GETDATE(), 
    FOREIGN KEY (product_id) REFERENCES Products(product_id), 
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE ReviewEdits (
    edit_id INT IDENTITY(1,1) PRIMARY KEY,
    review_id VARCHAR(50) NOT NULL,
    old_content NVARCHAR(MAX) NOT NULL,
    new_content NVARCHAR(MAX) NOT NULL,
    edit_reason NVARCHAR(255) NOT NULL,
    edited_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (review_id) REFERENCES Reviews(review_id) ON DELETE NO ACTION
);

CREATE TABLE AuditLog (
    audit_id VARCHAR(50) PRIMARY KEY, 
    actor_user_id VARCHAR(50) NOT NULL, 
    action VARCHAR(100) NOT NULL, 
    diff_json NVARCHAR(MAX), 
    previous_hash VARCHAR(64) NOT NULL, 
    current_hash VARCHAR(64) NOT NULL, 
    timestamp DATETIME DEFAULT GETDATE()
);

CREATE TABLE Alerts (
    alert_id VARCHAR(50) PRIMARY KEY,
    review_id VARCHAR(50) NOT NULL,
    risk_score DECIMAL(5,4) NOT NULL, 
    status VARCHAR(20) DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'RESOLVED', 'DISMISSED')),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (review_id) REFERENCES Reviews(review_id) ON DELETE CASCADE
);

CREATE TABLE AlertReasons (
    reason_id INT IDENTITY(1,1) PRIMARY KEY,
    alert_id VARCHAR(50) NOT NULL,
    feature_name VARCHAR(255) NOT NULL,
    importance_weight DECIMAL(8,4) NOT NULL, 
    description NVARCHAR(MAX),
    FOREIGN KEY (alert_id) REFERENCES Alerts(alert_id) ON DELETE CASCADE
);

CREATE TABLE AlertEvidences (
    evidence_id INT IDENTITY(1,1) PRIMARY KEY,
    alert_id VARCHAR(50) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    measured_value DECIMAL(10,2) NOT NULL,
    threshold_value DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (alert_id) REFERENCES Alerts(alert_id) ON DELETE CASCADE
);
GO -- BẮT BUỘC: Ép SSMS tạo bảng xong trước khi Insert dữ liệu

-- 3. NẠP DỮ LIỆU MẪU (SEED DATA PHASE)
-- 3.1. Vai trò và Quyền hạn
INSERT INTO Roles (role_name, description) VALUES
('CUSTOMER', N'Khách hàng tiêu dùng'),
('MERCHANT', N'Thương nhân / Chủ cơ sở kinh doanh'),
('MODERATOR', N'Kiểm duyệt viên nội dung AI'),
('AUDITOR', N'Kiểm toán viên hệ thống'),
('ADMIN', N'Quản trị viên toàn quyền');

INSERT INTO Permissions (permission_code, description) VALUES
('PERM_PRODUCT_READ', N'Xem sản phẩm'),
('PERM_PRODUCT_CREATE', N'Tạo sản phẩm'),
('PERM_REVIEW_READ', N'Đọc đánh giá'),
('PERM_REVIEW_CREATE', N'Viết đánh giá'),
('PERM_REVIEW_UPDATE', N'Sửa đánh giá'),
('PERM_REVIEW_DELETE', N'Xóa đánh giá'),
('PERM_MODERATE_ACTION', N'Duyệt/Ẩn đánh giá vi phạm'),
('PERM_ALERT_READ', N'Xem Gói bằng chứng XAI'),
('PERM_AI_RETRAIN', N'Kích hoạt tái huấn luyện AI');

-- Cấp quyền
INSERT INTO RolePerm (role_id, permission_id) SELECT r.role_id, p.permission_id FROM Roles r, Permissions p WHERE r.role_name = 'CUSTOMER' AND p.permission_code IN ('PERM_PRODUCT_READ', 'PERM_REVIEW_READ', 'PERM_REVIEW_CREATE', 'PERM_REVIEW_UPDATE');
INSERT INTO RolePerm (role_id, permission_id) SELECT r.role_id, p.permission_id FROM Roles r, Permissions p WHERE r.role_name = 'MERCHANT' AND p.permission_code IN ('PERM_PRODUCT_READ', 'PERM_PRODUCT_CREATE', 'PERM_REVIEW_READ');
INSERT INTO RolePerm (role_id, permission_id) SELECT r.role_id, p.permission_id FROM Roles r, Permissions p WHERE r.role_name = 'MODERATOR' AND p.permission_code IN ('PERM_PRODUCT_READ', 'PERM_REVIEW_READ', 'PERM_ALERT_READ', 'PERM_MODERATE_ACTION');
INSERT INTO RolePerm (role_id, permission_id) SELECT r.role_id, p.permission_id FROM Roles r, Permissions p WHERE r.role_name = 'ADMIN';

-- 3.2. Tài khoản Mẫu (Đa dạng độ tuổi tài khoản)
INSERT INTO Users (user_id, username, password, role_id, created_at) VALUES 
('U_ADMIN', 'admin', '123456', (SELECT role_id FROM Roles WHERE role_name = 'ADMIN'), GETDATE()), 
('U_MOD', 'mod_ai', '123456', (SELECT role_id FROM Roles WHERE role_name = 'MODERATOR'), GETDATE()), 
('U_MERCH1', 'fpt_shop', '123456', (SELECT role_id FROM Roles WHERE role_name = 'MERCHANT'), DATEADD(day, -300, GETDATE())), 
('U_MERCH2', 'book_store', '123456', (SELECT role_id FROM Roles WHERE role_name = 'MERCHANT'), DATEADD(day, -150, GETDATE())), 
('U_CUST1', 'khang_nguyen', '123456', (SELECT role_id FROM Roles WHERE role_name = 'CUSTOMER'), DATEADD(day, -50, GETDATE())),
('U_CUST2', 'spammer_007', '123456', (SELECT role_id FROM Roles WHERE role_name = 'CUSTOMER'), DATEADD(day, -2, GETDATE())), -- Tuổi siêu thấp (Nghi ngờ)
('U_CUST3', 'hieu_tran', '123456', (SELECT role_id FROM Roles WHERE role_name = 'CUSTOMER'), DATEADD(day, -120, GETDATE()));

-- 3.3. Sản phẩm Mẫu
INSERT INTO Products (product_id, name, description, price, merchant_id, status) VALUES
('P_PHONE1', N'iPhone 15 Pro Max 256GB', N'Hàng chính hãng VN/A, bảo hành 12 tháng Apple.', 29990000, 'U_MERCH1', 'ACTIVE'),
('P_LAPTOP1', N'MacBook Air M3 16GB', N'Chip M3 siêu mạnh, phù hợp cho lập trình viên.', 32000000, 'U_MERCH1', 'ACTIVE'),
('P_BOOK1', N'Clean Architecture - Robert C. Martin', N'Sách gối đầu giường cho Software Engineer.', 350000, 'U_MERCH2', 'ACTIVE');

-- 3.4. Đánh giá Mẫu (Đa dạng Trạng thái)
INSERT INTO Reviews (review_id, product_id, user_id, rating, content, status, created_at) VALUES 
('R_001', 'P_PHONE1', 'U_CUST1', 5, N'Sản phẩm xài cực kỳ mượt mà, giao hàng nhanh chóng. Rất hài lòng!', 'PUBLISHED', DATEADD(day, -10, GETDATE())),
('R_002', 'P_PHONE1', 'U_CUST2', 1, N'Sản phẩm lừa đảo, rác rưởi, mọi người đừng mua. Cam kết hoàn tiền 100% qua Zalo 09xx.', 'FLAGGED', DATEADD(day, -1, GETDATE())),
('R_003', 'P_LAPTOP1', 'U_CUST3', 4, N'Máy đẹp, pin trâu nhưng giá hơi cao so với mặt bằng chung.', 'PUBLISHED', DATEADD(day, -5, GETDATE())),
('R_004', 'P_BOOK1', 'U_CUST2', 5, N'Tuyển người nhận đánh giá hộ, việc nhẹ lương cao, hoa hồng 50k/đơn.', 'FLAGGED', DATEADD(hour, -2, GETDATE())),
('R_005', 'P_PHONE1', 'U_CUST3', 2, N'Máy bị xước viền, shop từ chối đổi trả.', 'HIDDEN', DATEADD(day, -20, GETDATE()));

-- 3.5. Dữ liệu Cảnh báo (XAI) cho các bài bị FLAGGED
-- Cảnh báo cho bài R_002 (Lừa đảo)
INSERT INTO Alerts (alert_id, review_id, risk_score, status) VALUES ('ALT_001', 'R_002', 0.8850, 'OPEN');
INSERT INTO AlertReasons (alert_id, feature_name, importance_weight, description) VALUES 
('ALT_001', 'lừa đảo', 0.3540, N'Đóng góp 35.4% vào quyết định rủi ro của mô hình AI.'),
('ALT_001', 'cam kết hoàn tiền', 0.2810, N'Đóng góp 28.1% vào quyết định rủi ro của mô hình AI.');
INSERT INTO AlertEvidences (alert_id, rule_type, measured_value, threshold_value) VALUES 
('ALT_001', 'ACCOUNT_AGE', 2.0, 30.0); -- Tuổi tài khoản thấp

-- Cảnh báo cho bài R_004 (Tuyển dụng rác)
INSERT INTO Alerts (alert_id, review_id, risk_score, status) VALUES ('ALT_002', 'R_004', 0.9210, 'OPEN');
INSERT INTO AlertReasons (alert_id, feature_name, importance_weight, description) VALUES 
('ALT_002', 'nhận đánh giá hộ', 0.5100, N'Đóng góp 51.0% vào quyết định rủi ro của mô hình AI.'),
('ALT_002', 'hoa hồng', 0.1500, N'Đóng góp 15.0% vào quyết định rủi ro của mô hình AI.');
INSERT INTO AlertEvidences (alert_id, rule_type, measured_value, threshold_value) VALUES 
('ALT_002', 'ACCOUNT_AGE', 2.0, 30.0),
('ALT_002', 'BURST_RATE', 6.0, 5.0); -- Đăng quá nhiều bài trong thời gian ngắn

-- 3.6. Genesis Block Audit
INSERT INTO AuditLog (audit_id, actor_user_id, action, diff_json, previous_hash, current_hash) VALUES 
('GENESIS_001', 'SYSTEM', 'SYSTEM_INIT', '{}', '0000000000000000000000000000000000000000000000000000000000000000', '1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t1u2v3w4x5y6z7a8b9c0d1e2f');
GO

