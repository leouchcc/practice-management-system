# 高校学生实践活动管理系统

基于Vue.js + SpringBoot的前后端分离架构的高校学生实践活动管理系统。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- JWT (io.jsonwebtoken:jjwt:0.12.3)

### 前端
- Vue 3
- Vue Router 4
- Pinia
- Element Plus
- Axios
- Vite

## 功能模块

1. **用户管理** - 学生、教师、管理员登录注册
2. **活动管理** - 活动发布、编辑、审核、取消
3. **活动分类** - 志愿服务、社会实践、创新创业等
4. **活动报名** - 活动报名、审核、签到签退
5. **学时学分** - 学时学分记录、统计
6. **成绩评价** - 活动评分、评语
7. **证书管理** - 证书生成、查询
8. **公告通知** - 公告发布、系统通知
9. **数据统计** - 数据统计、排行榜

## 项目结构

```
lhh35/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/practice/
│   │   │   │   ├── config/     # 配置类
│   │   │   │   ├── controller/ # 控制器
│   │   │   │   ├── dto/        # 数据传输对象
│   │   │   │   ├── entity/     # 实体类
│   │   │   │   ├── mapper/     # MyBatis Mapper
│   │   │   │   ├── service/    # 服务层
│   │   │   │   └── common/     # 公共类
│   │   │   └── resources/
│   │   │       ├── sql/         # 数据库脚本
│   │   │       └── application.yml
│   │   └── pom.xml
│
└── frontend/                   # 前端项目
    ├── src/
    │   ├── api/                # API接口
    │   ├── components/         # 组件
    │   ├── layout/             # 布局
    │   ├── router/             # 路由
    │   ├── stores/             # 状态管理
    │   ├── utils/              # 工具类
    │   ├── views/              # 页面
    │   ├── App.vue
    │   └── main.js
    ├── index.html
    ├── package.json
    └── vite.config.js
```

## 快速开始

### 1. 数据库配置

创建MySQL数据库并执行初始化脚本：

```bash
mysql -u root -p < backend/src/main/resources/sql/init.sql
```

修改数据库配置文件 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/practice_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 2. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 默认账号

系统已预置以下测试账号（密码均为：123456）：

- 管理员：admin
- 教师：teacher1
- 学生：student1
- 学生：student2

## API接口文档

### 认证接口
- POST /api/auth/login - 用户登录
- POST /api/auth/register - 用户注册

### 活动接口
- GET /api/activity/list - 获取活动列表
- GET /api/activity/{id} - 获取活动详情
- POST /api/activity - 创建活动
- PUT /api/activity - 更新活动
- POST /api/activity/{id}/publish - 发布活动
- POST /api/activity/{id}/cancel - 取消活动
- DELETE /api/activity/{id} - 删除活动

### 报名接口
- POST /api/registration/register - 报名活动
- POST /api/registration/{id}/approve - 审核通过
- POST /api/registration/{id}/reject - 审核拒绝
- POST /api/registration/{id}/cancel - 取消报名
- POST /api/registration/{id}/check-in - 签到
- POST /api/registration/{id}/check-out - 签退
- GET /api/registration/list - 获取报名列表

### 学分接口
- GET /api/credit/list - 获取学分记录
- GET /api/credit/total/{studentId} - 获取总学分

### 评价接口
- POST /api/evaluation - 创建评价
- GET /api/evaluation/list - 获取评价列表
- GET /api/evaluation/average/{activityId} - 获取平均分

### 证书接口
- GET /api/certificate/list - 获取证书列表
- GET /api/certificate/{certificateNo} - 根据编号查询证书

### 公告接口
- GET /api/announcement/list - 获取公告列表
- GET /api/announcement/{id} - 获取公告详情
- POST /api/announcement - 创建公告
- PUT /api/announcement - 更新公告
- DELETE /api/announcement/{id} - 删除公告

### 通知接口
- GET /api/notification/list - 获取通知列表
- POST /api/notification/{id}/read - 标记已读
- POST /api/notification/read-all - 全部标记已读
- GET /api/notification/unread-count - 获取未读数量

### 用户接口
- GET /api/user/list - 获取用户列表
- GET /api/user/{id} - 获取用户详情
- PUT /api/user - 更新用户
- DELETE /api/user/{id} - 删除用户

### 分类接口
- GET /api/category/list - 获取分类列表
- GET /api/category/{id} - 获取分类详情
- POST /api/category - 创建分类
- PUT /api/category - 更新分类
- DELETE /api/category/{id} - 删除分类

## 开发说明

### 后端开发
- 使用MyBatis Plus进行数据库操作
- 使用JWT进行身份认证
- 使用Spring Boot REST风格API

### 前端开发
- 使用Vue 3 Composition API
- 使用Pinia进行状态管理
- 使用Element Plus UI组件库
- 使用Axios进行HTTP请求

## 注意事项

1. 确保MySQL服务已启动
2. 确保JDK版本为17或以上
3. 确保Node.js版本为16或以上
4. 修改数据库密码配置
5. 前端代理配置在vite.config.js中

## 许可证

MIT License
