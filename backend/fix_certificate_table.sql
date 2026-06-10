-- 删除现有的certificate表（如果存在）
DROP TABLE IF EXISTS certificate;

-- 重新创建certificate表，确保字段名正确
CREATE TABLE certificate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 
    certificate_number VARCHAR(50) UNIQUE NOT NULL, 
    activity_id BIGINT NOT NULL, 
    student_id BIGINT NOT NULL, 
    certificate_name VARCHAR(255) NOT NULL, 
    issue_date DATE NOT NULL, 
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    FOREIGN KEY (activity_id) REFERENCES activity(id), 
    FOREIGN KEY (student_id) REFERENCES sys_user(id) 
);

-- 插入一条测试数据
INSERT INTO certificate (certificate_number, activity_id, student_id, certificate_name, issue_date) 
VALUES ('CERT-TEST000001', 1, 7, '测试证书', CURDATE());