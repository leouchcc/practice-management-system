USE practice_management;

-- 添加证书相关字段到activity表
ALTER TABLE activity ADD COLUMN is_certificate BOOLEAN DEFAULT FALSE;
ALTER TABLE activity ADD COLUMN certificate_name VARCHAR(255);

-- 创建certificate表
CREATE TABLE IF NOT EXISTS certificate (
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

-- 检查更新结果
DESCRIBE activity;
SELECT * FROM activity LIMIT 1;
