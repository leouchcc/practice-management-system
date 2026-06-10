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