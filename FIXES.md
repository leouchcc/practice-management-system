# 代码修复说明

## 已修复的问题

### 1. 错误修复

#### 1.1 List类型无法解析
**问题位置：**
- ActivityEvaluationService.java:73
- CreditRecordService.java:54, 64
- SystemNotificationService.java:45

**原因：** 缺少 `java.util.List` 的导入

**修复：** 在所有受影响的文件中添加了 `import java.util.List;`

#### 1.2 BigDecimal.divide方法已废弃
**问题位置：**
- ActivityEvaluationService.java:83

**原因：** Java 9+ 中 `BigDecimal.divide(BigDecimal, int, int)` 方法已废弃

**修复：** 
```java
// 修复前
return sum.divide(new BigDecimal(evaluations.size()), 2, BigDecimal.ROUND_HALF_UP);

// 修复后
import java.math.RoundingMode;
return sum.divide(new BigDecimal(evaluations.size()), 2, RoundingMode.HALF_UP);
```

### 2. 警告修复

#### 2.1 未使用的导入
**问题位置：**
- AuthController.java - 未使用的 `import com.practice.entity.User;`
- ActivityCategoryService.java - 未使用的 `QueryWrapper` 和 `Page` 导入
- ActivityService.java - 未使用的 `LocalDateTime` 导入和 `registrationService` 字段
- CertificateService.java - 未使用的 `DateTimeFormatter` 导入

**修复：** 移除了所有未使用的导入和字段

#### 2.2 配置属性警告
**问题位置：**
- application.yml - Unknown property 'jwt'

**修复：** 创建了 `JwtProperties` 配置类，使用 `@ConfigurationProperties` 注解

```java
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private Long expiration;
    // getters and setters
}
```

### 3. 修改的文件列表

#### 后端文件
1. `ActivityEvaluationService.java` - 添加List和RoundingMode导入，修复divide方法
2. `CreditRecordService.java` - 添加List导入
3. `SystemNotificationService.java` - 添加List导入
4. `AuthController.java` - 移除未使用的User导入
5. `ActivityCategoryService.java` - 移除未使用的QueryWrapper和Page导入
6. `ActivityService.java` - 移除未使用的LocalDateTime导入和registrationService字段
7. `CertificateService.java` - 移除未使用的DateTimeFormatter导入
8. `JwtProperties.java` - 新增配置属性类

## 验证

所有修复已完成，项目应该可以正常编译和运行。建议执行以下命令验证：

```bash
cd backend
mvn clean compile
```

## 注意事项

1. **Spring Boot版本警告** - 提示Spring Boot 3.2.x的OSS和商业支持已结束，但这不影响项目运行，可以忽略或升级到3.5.10版本

2. **配置文件** - application.yml中的jwt配置现在有对应的配置类支持，不会再有警告

3. **代码质量** - 移除了所有未使用的导入，代码更加整洁

## 下一步

项目现在应该可以正常启动：

```bash
# 启动后端
cd backend
mvn spring-boot:run

# 启动前端
cd frontend
npm install
npm run dev
```
