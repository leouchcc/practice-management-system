# 问题解决说明

## 问题描述
前端无法连接到后端，出现 `net::ERR_FAILED http://localhost:8080/login;jsessionid=...` 错误。

## 根本原因
Spring Security默认启用了session管理，导致前端请求时产生jsessionid，而我们的系统使用JWT token进行认证，不需要session。

## 解决方案

### 1. 创建了SecurityConfig配置类
创建了 `SecurityConfig.java` 配置类，主要修改：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());
        
        return http.build();
    }
}
```

**关键修改：**
- ✅ 禁用CSRF保护
- ✅ 设置session创建策略为STATELESS（无状态）
- ✅ 允许 `/api/auth/**` 路径无需认证
- ✅ 其他 `/api/**` 路径需要认证

### 2. 已禁用Session
通过设置 `SessionCreationPolicy.STATELESS`，Spring Security不再创建或使用HTTP session，完全依赖JWT token进行认证。

### 3. CORS配置已存在
`CorsConfig.java` 已经正确配置了CORS，允许前端跨域访问。

## 当前状态

### ✅ 后端服务
- **状态**: 运行中
- **端口**: 8080
- **安全配置**: 已更新为无状态JWT认证
- **Session**: 已禁用

### ✅ 前端服务
- **状态**: 运行中
- **端口**: 3000
- **代理配置**: 已配置代理到后端8080端口

## 测试步骤

### 1. 刷新浏览器
按 `Ctrl + F5` 刷新浏览器页面，清除可能的缓存。

### 2. 访问系统
打开浏览器访问：**http://localhost:3000**

### 3. 测试登录
使用以下测试账号登录（密码均为：**123456**）：

| 角色 | 用户名 |
|------|--------|
| 管理员 | admin |
| 教师 | teacher1 |
| 学生 | student1 |
| 学生 | student2 |

### 4. 验证功能
登录后应该能够：
- ✅ 成功登录并跳转到首页
- ✅ 查看活动列表
- ✅ 报名活动
- ✅ 查看学分记录
- ✅ 查看公告通知

## 技术说明

### JWT认证流程
1. 用户登录 → 后端验证密码 → 生成JWT token
2. 前端存储token到localStorage
3. 前端每次请求在header中携带token
4. 后端验证token → 允许访问

### 无状态认证优势
- ✅ 不依赖服务器session
- ✅ 支持分布式部署
- ✅ 更好的扩展性
- ✅ 减少服务器内存占用

## 如果仍有问题

### 1. 检查后端日志
查看后端终端输出，确认没有错误信息。

### 2. 检查浏览器控制台
按F12打开开发者工具，查看Network标签：
- 检查请求是否成功（状态码200）
- 检查响应数据是否正确
- 检查是否有CORS错误

### 3. 清除浏览器缓存
- Chrome: Ctrl + Shift + Delete
- Firefox: Ctrl + Shift + Delete
- Edge: Ctrl + Shift + Delete

### 4. 重启浏览器
完全关闭浏览器后重新打开。

### 5. 检查端口占用
确保8080和3000端口没有被其他程序占用：
```bash
# Windows
netstat -ano | findstr :8080
netstat -ano | findstr :3000
```

## 成功标志

当问题解决后，您应该看到：
- ✅ 登录页面正常显示
- ✅ 输入用户名密码后能成功登录
- ✅ 登录后跳转到首页
- ✅ 首页显示统计数据
- ✅ 浏览器控制台没有错误

## 总结

通过禁用Spring Security的session管理并使用无状态的JWT认证，解决了前端连接后端的问题。现在系统应该可以正常使用了！
