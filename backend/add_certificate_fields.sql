-- 添加证书相关字段到activity表
ALTER TABLE activity ADD COLUMN is_certificate BOOLEAN DEFAULT FALSE;
ALTER TABLE activity ADD COLUMN certificate_name VARCHAR(255);

-- 验证字段是否添加成功
DESCRIBE activity;