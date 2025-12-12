# 数据库设置文档

## 1. 数据库初始化

### 1.1 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS test CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE test;
```

### 1.2 执行SQL脚本

运行项目根目录下的 `database.sql` 文件来创建所有必需的数据库表：

```bash
mysql -u root -p test < database.sql
```

或者手动执行以下迁移脚本：

```bash
mysql -u root -p test < my-project-backend/src/main/resources/db/migration/V1__create_task_table.sql
```

## 2. 表结构说明

### 2.1 task 表（任务表）

存储用户的任务信息，支持层级结构（父子任务）。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | int | 任务ID | 主键，自增 |
| user_id | int | 用户ID | NOT NULL，有索引 |
| title | varchar(255) | 任务标题 | NOT NULL，默认值为空字符串 |
| description | text | 任务描述 | 可为空 |
| status | varchar(50) | 任务状态 | NOT NULL，默认'pending'，有索引 |
| priority | varchar(50) | 优先级 | NOT NULL，默认'medium' |
| parent_id | int | 父任务ID | 可为空，有索引，用于子任务 |
| created_at | timestamp | 创建时间 | 默认CURRENT_TIMESTAMP |
| updated_at | timestamp | 更新时间 | 默认CURRENT_TIMESTAMP，自动更新 |
| due_date | datetime | 截止日期 | 可为空 |

**status 可选值：**
- `pending` - 待处理
- `in_progress` - 进行中
- `completed` - 已完成
- `cancelled` - 已取消

**priority 可选值：**
- `low` - 低
- `medium` - 中
- `high` - 高
- `urgent` - 紧急

### 2.2 task_log 表（任务日志表）

记录任务的所有操作历史。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | int | 日志ID | 主键，自增 |
| task_id | int | 任务ID | NOT NULL，有索引 |
| user_id | int | 用户ID | NOT NULL，有索引 |
| action | varchar(50) | 操作类型 | NOT NULL |
| description | text | 操作描述 | 可为空 |
| created_at | timestamp | 创建时间 | 默认CURRENT_TIMESTAMP，有索引 |

**action 可选值：**
- `create` - 创建任务
- `update` - 更新任务
- `delete` - 删除任务
- `complete` - 完成任务
- `cancel` - 取消任务

## 3. 数据库配置

### 3.1 开发环境配置

修改 `my-project-backend/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### 3.2 生产环境配置

修改 `my-project-backend/src/main/resources/application-prod.yml` 中的数据库连接信息。

## 4. API 接口说明

### 4.1 任务管理接口

**基础路径：** `/api/task`

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|---------|
| POST | /create | 创建任务 | 是 |
| POST | /update/{id} | 更新任务 | 是 |
| POST | /delete/{id} | 删除任务 | 是 |
| GET | /list | 获取任务列表 | 是 |
| GET | /get/{id} | 获取任务详情 | 是 |
| GET | /subtasks/{parentId} | 获取子任务列表 | 是 |

### 4.2 任务日志接口

**基础路径：** `/api/task-log`

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|---------|
| GET | /list/{taskId} | 获取任务日志列表 | 是 |
| GET | /statistics | 获取任务统计信息 | 是 |

## 5. 常见问题解决

### 5.1 错误：Unknown column 'parent_id' in 'where clause'

**原因：** 数据库表缺少 `parent_id` 字段。

**解决方法：**
```sql
ALTER TABLE task ADD COLUMN parent_id int DEFAULT NULL COMMENT '父任务ID，用于子任务';
ALTER TABLE task ADD INDEX idx_parent_id (parent_id);
```

### 5.2 错误：Field 'title' doesn't have a default value

**原因：** `title` 字段设置为 NOT NULL 但没有默认值。

**解决方法：**
```sql
ALTER TABLE task MODIFY COLUMN title varchar(255) NOT NULL DEFAULT '' COMMENT '任务标题';
```

### 5.3 接口返回 500 错误

**可能原因：**
1. 数据库连接失败 - 检查 application-dev.yml 中的数据库配置
2. 表不存在 - 执行数据库初始化脚本
3. 字段映射错误 - 确保 MyBatis-Plus 配置中启用了下划线转驼峰

**调试步骤：**
1. 查看后端日志：`my-project-backend/logs/` 目录
2. 检查数据库表是否存在：`SHOW TABLES;`
3. 检查表结构：`DESC task;`
4. 验证数据库连接：使用 MySQL 客户端测试连接

### 5.4 MyBatis-Plus 字段映射问题

**问题：** Java 驼峰命名与数据库下划线命名不匹配。

**解决方法：** 
- 确保在 application-dev.yml 中配置了：
  ```yaml
  mybatis-plus:
    configuration:
      map-underscore-to-camel-case: true
  ```
- 或者在实体类中使用 `@TableField` 注解明确指定字段映射

## 6. 数据迁移

### 6.1 从旧表迁移数据

如果已有旧的任务表，可以使用以下 SQL 迁移数据：

```sql
-- 备份旧数据
CREATE TABLE task_backup AS SELECT * FROM task;

-- 删除旧表
DROP TABLE task;

-- 创建新表（使用 migration 脚本）
SOURCE my-project-backend/src/main/resources/db/migration/V1__create_task_table.sql;

-- 迁移数据（根据实际字段调整）
INSERT INTO task (user_id, title, description, status, priority, created_at, updated_at)
SELECT user_id, title, description, status, priority, created_at, updated_at
FROM task_backup;
```

## 7. 性能优化建议

### 7.1 索引优化

已创建的索引：
- `idx_user_id` - 用户ID索引，加速按用户查询
- `idx_parent_id` - 父任务ID索引，加速子任务查询
- `idx_status` - 状态索引，加速按状态过滤

### 7.2 查询优化

- 使用分页查询大量任务数据
- 避免查询不必要的字段
- 定期清理过期的任务日志数据

## 8. 安全建议

1. **权限控制：** 所有任务操作都需要验证用户身份（通过 JWT）
2. **数据隔离：** 用户只能操作自己的任务（通过 user_id 过滤）
3. **SQL注入防护：** 使用 MyBatis-Plus 的参数绑定，避免拼接 SQL
4. **数据验证：** 前端和后端都要验证输入数据

## 9. 测试数据

### 9.1 插入测试任务

```sql
-- 假设用户ID为1
INSERT INTO task (user_id, title, description, status, priority, due_date)
VALUES 
(1, '完成项目文档', '编写项目使用说明文档', 'in_progress', 'high', '2024-12-31 18:00:00'),
(1, '代码审查', '审查任务管理模块代码', 'pending', 'medium', '2024-12-25 17:00:00'),
(1, '数据库优化', '优化任务查询性能', 'pending', 'low', NULL);
```

### 9.2 查询示例

```sql
-- 查询用户的所有待处理任务
SELECT * FROM task WHERE user_id = 1 AND status = 'pending' ORDER BY created_at DESC;

-- 查询高优先级任务
SELECT * FROM task WHERE user_id = 1 AND priority = 'high' ORDER BY due_date ASC;

-- 查询即将到期的任务（7天内）
SELECT * FROM task 
WHERE user_id = 1 
  AND due_date IS NOT NULL 
  AND due_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 7 DAY)
ORDER BY due_date ASC;
```
