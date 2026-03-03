CREATE TABLE Users (
    user_id VARCHAR(50) PRIMARY KEY, 
    username VARCHAR(100) UNIQUE NOT NULL, 
    password VARCHAR(255) NOT NULL, 
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'MERCHANT', 'CUSTOMER', 'MODERATOR', 'AUDITOR'))
);
CREATE TABLE Products (
    product_id VARCHAR(50) PRIMARY KEY, 
    name NVARCHAR(255) NOT NULL, 
    description NVARCHAR(MAX), 
    price DECIMAL(18,2), 
    merchant_id VARCHAR(50) NOT NULL, 
    created_at DATETIME DEFAULT GETDATE(), 
    FOREIGN KEY (merchant_id) REFERENCES Users(user_id)
);
CREATE TABLE Reviews (
    review_id VARCHAR(50) PRIMARY KEY, 
    product_id VARCHAR(50) NOT NULL, 
    user_id VARCHAR(50) NOT NULL, 
    rating INT CHECK (rating >= 1 AND rating <= 5), 
    content NVARCHAR(MAX), 
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PUBLISHED', 'HIDDEN', 'FLAGGED')), 
    created_at DATETIME DEFAULT GETDATE(), updated_at DATETIME DEFAULT GETDATE(), FOREIGN KEY (product_id) REFERENCES Products(product_id), 
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
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
    review_id VARCHAR(50) NOT NULL UNIQUE,
    risk_score DECIMAL(5,4) NOT NULL, 
    status VARCHAR(20) DEFAULT 'OPEN' 
        CHECK (status IN ('OPEN', 'RESOLVED', 'DISMISSED')),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (review_id) REFERENCES Reviews(review_id) ON DELETE CASCADE
);

CREATE TABLE AlertReasons (
    reason_id INT IDENTITY(1,1) PRIMARY KEY,
    alert_id VARCHAR(50) NOT NULL,
    feature_name VARCHAR(100) NOT NULL,
    importance_weight DECIMAL(5,4) NOT NULL,
    description NVARCHAR(255),
    FOREIGN KEY (alert_id) REFERENCES Alerts(alert_id) ON DELETE CASCADE
);

CREATE TABLE AlertEvidences (
    evidence_id INT IDENTITY(1,1) PRIMARY KEY,
    alert_id VARCHAR(50) NOT NULL,
    rule_type VARCHAR(50) NOT NULL 
        CHECK (rule_type IN ('BURST_RATE', 'ACCOUNT_AGE', 'TEXT_SIMILARITY', 'IP_BLACKLIST')),
    measured_value DECIMAL(10,2) NOT NULL,
    threshold_value DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (alert_id) REFERENCES Alerts(alert_id) ON DELETE CASCADE
);

INSERT INTO Users (user_id, username, password, role) VALUES 
('U01', 'admin', '123456', 'ADMIN'), 
('U02', 'merchant_fpt', '123456', 'MERCHANT'), ('U03', 'customer_khang', '123456', 'CUSTOMER'), 
('U04', 'mod_ai', '123456', 'MODERATOR'), ('U05', 'auditor_sec', '123456', 'AUDITOR');

INSERT INTO Products (product_id, name, description, price, merchant_id) VALUES 
('P01', N'Khách sạn Review', N'Dịch vụ lưu trú tiêu chuẩn', 1500000, 'U02');

INSERT INTO AuditLog (audit_id, actor_user_id, action, diff_json, previous_hash, current_hash) VALUES 
('GENESIS_001', 'SYSTEM', 'SYSTEM_INIT', '{}', '0000000000000000000000000000000000000000000000000000000000000000', '1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t1u2v3w4x5y6z7a8b9c0d1e2f');