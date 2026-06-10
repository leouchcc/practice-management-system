-- 删除所有分类
DELETE FROM activity_category;

-- 重新插入正确的中文分类
INSERT INTO activity_category (id, name, description, deleted, create_time, update_time) VALUES
(1, '志愿服务', '包括社区服务、公益活动等', 0, NOW(), NOW()),
(2, '学术科技', '包括学术讲座、科技竞赛等', 0, NOW(), NOW()),
(3, '文化体育', '包括文艺活动、体育竞赛等', 0, NOW(), NOW()),
(4, '社会实践', '包括实习、调研等', 0, NOW(), NOW()),
(5, '其他活动', '其他类型的实践活动', 0, NOW(), NOW());