USE practice_management;

-- 添加点评相关字段
ALTER TABLE activity_registration ADD COLUMN IF NOT EXISTS evaluation_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '点评状态：PENDING-待点评 COMPLETED-已点评';
ALTER TABLE activity_registration ADD COLUMN IF NOT EXISTS rating VARCHAR(20) COMMENT '点评等级：EXCELLENT-优秀 GOOD-良好 PASS-及格';
ALTER TABLE activity_registration ADD COLUMN IF NOT EXISTS final_credit_points DECIMAL(5,2) COMMENT '最终获得的学分';

-- 添加索引
CREATE INDEX IF NOT EXISTS idx_evaluation_status ON activity_registration(evaluation_status);
