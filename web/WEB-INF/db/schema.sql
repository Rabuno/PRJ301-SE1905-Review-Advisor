-- ==============================================================================
-- BẢN THIẾT KẾ CƠ SỞ DỮ LIỆU - DỰ ÁN REVIEW ADVISOR (VER 2.4 - TRIPADVISOR DOMAIN)
-- Cấu trúc: Đồng bộ hóa 1:1 với Product.java và Review.java
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
IF OBJECT_ID('UserRole', 'U') IS NOT NULL DROP TABLE UserRole;
IF OBJECT_ID('Users', 'U') IS NOT NULL DROP TABLE Users;
IF OBJECT_ID('Permissions', 'U') IS NOT NULL DROP TABLE Permissions;
IF OBJECT_ID('Roles', 'U') IS NOT NULL DROP TABLE Roles;
GO 

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
    category NVARCHAR(100) DEFAULT 'Uncategorized', 
    description NVARCHAR(MAX), 
    price DECIMAL(18,2), 
    merchant_id VARCHAR(50) NOT NULL, 
    image_url VARCHAR(255), 
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('ACTIVE', 'DEACTIVATED', 'PENDING')), 
    created_at DATETIME DEFAULT GETDATE(), 
    FOREIGN KEY (merchant_id) REFERENCES Users(user_id)
);

CREATE TABLE Reviews (
    review_id VARCHAR(50) PRIMARY KEY, 
    product_id VARCHAR(50) NOT NULL, 
    user_id VARCHAR(50) NOT NULL, 
    rating INT CHECK (rating >= 1 AND rating <= 5), 
    content NVARCHAR(MAX), 
    image_url VARCHAR(255) NULL, 
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
GO 

-- 3. NẠP DỮ LIỆU MẪU (SEED DATA PHASE - TRIPADVISOR DOMAIN)
INSERT INTO Roles (role_name, description) VALUES
('CUSTOMER', N'Khách hàng/Du khách'),
('MERCHANT', N'Chủ cơ sở (Khách sạn/Nhà hàng)'),
('MODERATOR', N'Kiểm duyệt viên nội dung AI'),
('AUDITOR', N'Kiểm toán viên hệ thống'),
('ADMIN', N'Quản trị viên toàn quyền');

INSERT INTO Permissions (permission_code, description) VALUES
('PERM_PRODUCT_READ', N'Xem cơ sở lưu trú/dịch vụ'),
('PERM_PRODUCT_CREATE', N'Đăng ký cơ sở dịch vụ mới'),
('PERM_REVIEW_READ', N'Đọc đánh giá'),
('PERM_REVIEW_CREATE', N'Viết đánh giá'),
('PERM_REVIEW_UPDATE', N'Sửa đánh giá'),
('PERM_REVIEW_DELETE', N'Xóa đánh giá'),
('PERM_MODERATE_ACTION', N'Duyệt/Ẩn đánh giá vi phạm'),
('PERM_ALERT_READ', N'Xem Gói bằng chứng XAI'),
('PERM_AI_RETRAIN', N'Kích hoạt tái huấn luyện AI');

INSERT INTO RolePerm (role_id, permission_id) SELECT r.role_id, p.permission_id FROM Roles r, Permissions p WHERE r.role_name = 'CUSTOMER' AND p.permission_code IN ('PERM_PRODUCT_READ', 'PERM_REVIEW_READ', 'PERM_REVIEW_CREATE', 'PERM_REVIEW_UPDATE');
INSERT INTO RolePerm (role_id, permission_id) SELECT r.role_id, p.permission_id FROM Roles r, Permissions p WHERE r.role_name = 'MERCHANT' AND p.permission_code IN ('PERM_PRODUCT_READ', 'PERM_PRODUCT_CREATE', 'PERM_REVIEW_READ');
INSERT INTO RolePerm (role_id, permission_id) SELECT r.role_id, p.permission_id FROM Roles r, Permissions p WHERE r.role_name = 'MODERATOR' AND p.permission_code IN ('PERM_PRODUCT_READ', 'PERM_REVIEW_READ', 'PERM_ALERT_READ', 'PERM_MODERATE_ACTION');
INSERT INTO RolePerm (role_id, permission_id) SELECT r.role_id, p.permission_id FROM Roles r, Permissions p WHERE r.role_name = 'ADMIN';

-- Dữ liệu người dùng
INSERT INTO Users (user_id, username, password, role_id, created_at) VALUES 
('U_ADMIN', 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', (SELECT role_id FROM Roles WHERE role_name = 'ADMIN'), GETDATE()), 
('U_MOD', 'moderator', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', (SELECT role_id FROM Roles WHERE role_name = 'MODERATOR'), GETDATE()), 
('U_MERCH1', 'marriott_hn', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', (SELECT role_id FROM Roles WHERE role_name = 'MERCHANT'), DATEADD(day, -300, GETDATE())), 
('U_MERCH2', 'pizza_4ps', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', (SELECT role_id FROM Roles WHERE role_name = 'MERCHANT'), DATEADD(day, -150, GETDATE())), 
('U_CUST1', 'traveler_khang', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', (SELECT role_id FROM Roles WHERE role_name = 'CUSTOMER'), DATEADD(day, -50, GETDATE())),
('U_CUST2', 'seeding_bot_01', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', (SELECT role_id FROM Roles WHERE role_name = 'CUSTOMER'), DATEADD(day, -2, GETDATE())), 
('U_CUST3', 'hieu_foodie', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', (SELECT role_id FROM Roles WHERE role_name = 'CUSTOMER'), DATEADD(day, -120, GETDATE()));

-- Cơ sở dịch vụ
INSERT INTO Products (product_id, name, category, description, price, merchant_id, image_url, status) VALUES
('P_PROP1', N'JW Marriott Hotel Hanoi', N'Accommodation', N'Khách sạn 5 sao sang trọng với thiết kế lấy cảm hứng từ con rồng huyền thoại.', 4500000, 'U_MERCH1', '/assets/uploads/marriott_hn.jpg', 'ACTIVE'),
('P_PROP2', N'French Grill - JW Marriott', N'Dining', N'Nhà hàng Pháp cao cấp, phục vụ bò Wagyu và hải sản nhập khẩu.', 2000000, 'U_MERCH1', '/assets/uploads/french_grill.jpg', 'ACTIVE'),
('P_PROP3', N'Pizza 4P''s Tràng Tiền', N'Dining', N'Pizza kiểu Nhật kết hợp Ý, nổi tiếng với phô mai Burrata tự làm.', 350000, 'U_MERCH2', '/assets/uploads/pizza_4ps.jpg', 'ACTIVE');

-- Đánh giá
INSERT INTO Reviews (review_id, product_id, user_id, rating, content, image_url, status, created_at) VALUES 
('R_001', 'P_PROP1', 'U_CUST1', 5, N'Phòng ốc cực kỳ sạch sẽ và tiện nghi. Hồ bơi view kính rất đẹp. Sẽ quay lại!', '/assets/uploads/r001_pool.jpg', 'PUBLISHED', DATEADD(day, -10, GETDATE())),
('R_002', 'P_PROP1', 'U_CUST2', 1, N'Khách sạn thái độ phục vụ lồi lõm, có gián trong phòng. Nhận đặt phòng giá rẻ các ks 5 sao chiết khấu 40% qua Zalo 09xx.', NULL, 'FLAGGED', DATEADD(day, -1, GETDATE())),
('R_003', 'P_PROP3', 'U_CUST3', 4, N'Pizza ngon, không gian ấm cúng nhưng phải đặt bàn trước khá lâu.', '/assets/uploads/r003_pizza.jpg', 'PUBLISHED', DATEADD(day, -5, GETDATE())),
('R_004', 'P_PROP2', 'U_CUST2', 5, N'Tuyển CTV review nhà hàng/khách sạn, ngồi nhà lướt app kiếm 500k/ngày, cọc trước 100k.', NULL, 'FLAGGED', DATEADD(hour, -2, GETDATE())),
('R_005', 'P_PROP1', 'U_CUST3', 3, N'Cách âm giữa các phòng chưa thực sự tốt, giá buffet sáng hơi cao.', NULL, 'HIDDEN', DATEADD(day, -20, GETDATE()));

-- Cảnh báo XAI
INSERT INTO Alerts (alert_id, review_id, risk_score, status) VALUES ('ALT_001', 'R_002', 0.8950, 'OPEN');
INSERT INTO AlertReasons (alert_id, feature_name, importance_weight, description) VALUES 
('ALT_001', 'đặt phòng giá rẻ', 0.3840, N'Tín hiệu lừa đảo môi giới du lịch trái phép (38.4%).'),
('ALT_001', 'chiết khấu', 0.2510, N'Từ khóa bất thường trong văn cảnh review trải nghiệm (25.1%).');
INSERT INTO AlertEvidences (alert_id, rule_type, measured_value, threshold_value) VALUES 
('ALT_001', 'ACCOUNT_AGE', 2.0, 30.0);

INSERT INTO Alerts (alert_id, review_id, risk_score, status) VALUES ('ALT_002', 'R_004', 0.9420, 'OPEN');
INSERT INTO AlertReasons (alert_id, feature_name, importance_weight, description) VALUES 
('ALT_002', 'tuyển CTV review', 0.5500, N'Hành vi tuyển dụng seeding/review ảo đặc trưng trên nền tảng (55.0%).'),
('ALT_002', 'cọc trước', 0.1800, N'Từ khóa liên quan đến lừa đảo tài chính (18.0%).');
INSERT INTO AlertEvidences (alert_id, rule_type, measured_value, threshold_value) VALUES 
('ALT_002', 'ACCOUNT_AGE', 2.0, 30.0),
('ALT_002', 'BURST_RATE', 8.0, 5.0); 

INSERT INTO AuditLog (audit_id, actor_user_id, action, diff_json, previous_hash, current_hash) VALUES 
('GENESIS_001', 'SYSTEM', 'SYSTEM_INIT', '{}', '0000000000000000000000000000000000000000000000000000000000000000', '1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t1u2v3w4x5y6z7a8b9c0d1e2f');
GO
