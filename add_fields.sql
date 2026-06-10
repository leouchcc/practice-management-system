USE practice_management;
ALTER TABLE activity_registration ADD COLUMN IF NOT EXISTS evaluation_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '点评状态';
ALTER TABLE activity_registration ADD COLUMN IF NOT EXISTS rating VARCHAR(20) COMMENT '点评等级';
ALTER TABLE activity_registration ADD COLUMN IF NOT EXISTS final_credit_points DECIMAL(5,2) COMMENT '最终学分';
SHOW COLUMNS FROM activity_registration;
