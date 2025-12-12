# 任务管理系统部署指南 / Task Management System Deployment Guide

## 中文说明

### 问题诊断
您遇到的 500 错误是因为：
1. 数据库表尚未创建
2. MyBatis 配置需要更新（已在此次提交中修复）

### 部署步骤

#### 1. 数据库配置
首先需要在 MySQL 数据库中创建任务管理相关的表：

```bash
# 登录 MySQL
mysql -u root -p

# 选择数据库（根据你的配置文件，默认是 test）
USE test;

# 执行 SQL 脚本创建表
SOURCE /path/to/task_management.sql;

# 或者直接在 MySQL 中执行
```

或者使用命令行：
```bash
mysql -u root -p test < task_management.sql
```

#### 2. 验证表创建成功
```sql
SHOW TABLES LIKE 'db_task%';
-- 应该看到:
-- db_task
-- db_task_log

DESC db_task;
DESC db_task_log;
```

#### 3. 重新启动后端服务
```bash
cd my-project-backend
mvn clean package -DskipTests
mvn spring-boot:run
```

#### 4. 验证 API 可用
访问以下 URL 测试（需要先登录获取 token）：
- http://localhost:8081/api/task/list
- http://localhost:8081/api/task-log/statistics

### 本次修复内容
在此提交中，已经添加了 MyBatis 配置来扫描 XML 映射文件：
- 更新了 `application-dev.yml`
- 更新了 `application-prod.yml`
- 添加了 `mybatis-plus.mapper-locations` 配置

---

## English Instructions

### Issue Diagnosis
The 500 errors you encountered are due to:
1. Database tables not yet created
2. MyBatis configuration needed updating (fixed in this commit)

### Deployment Steps

#### 1. Database Setup
First, create the task management tables in your MySQL database:

```bash
# Login to MySQL
mysql -u root -p

# Select database (default is 'test' based on your config)
USE test;

# Execute SQL script to create tables
SOURCE /path/to/task_management.sql;
```

Or use command line:
```bash
mysql -u root -p test < task_management.sql
```

#### 2. Verify Tables Created
```sql
SHOW TABLES LIKE 'db_task%';
-- Should see:
-- db_task
-- db_task_log

DESC db_task;
DESC db_task_log;
```

#### 3. Restart Backend Service
```bash
cd my-project-backend
mvn clean package -DskipTests
mvn spring-boot:run
```

#### 4. Verify API Availability
Test these URLs (after logging in to get token):
- http://localhost:8081/api/task/list
- http://localhost:8081/api/task-log/statistics

### What Was Fixed in This Commit
Added MyBatis configuration to scan XML mapper files:
- Updated `application-dev.yml`
- Updated `application-prod.yml`
- Added `mybatis-plus.mapper-locations` configuration

---

## 技术细节 / Technical Details

### 配置更改 / Configuration Changes

**application-dev.yml & application-prod.yml:**
```yaml
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.example.entity.dto
```

### 数据表结构 / Database Schema

**db_task** - 任务表
- `id`: 任务ID (自增主键)
- `user_id`: 用户ID
- `parent_id`: 父任务ID (NULL 表示根任务)
- `content`: 任务内容
- `start_time`: 开始时间
- `end_time`: 结束时间
- `status`: 状态 (IN_PROGRESS/COMPLETED)
- `create_time`: 创建时间
- `update_time`: 更新时间

**db_task_log** - 任务日志表
- `id`: 日志ID (自增主键)
- `user_id`: 用户ID
- `task_id`: 任务ID
- `operation_type`: 操作类型 (CREATE/UPDATE/DELETE/QUERY/COMPLETE)
- `operation_detail`: 操作详情
- `operation_time`: 操作时间

### 故障排查 / Troubleshooting

如果仍然遇到问题 / If you still encounter issues:

1. **检查数据库连接 / Check database connection:**
   ```bash
   # 确认 MySQL 服务运行
   systemctl status mysql
   # 或
   sudo service mysql status
   ```

2. **检查表是否存在 / Check if tables exist:**
   ```sql
   SELECT COUNT(*) FROM db_task;
   SELECT COUNT(*) FROM db_task_log;
   ```

3. **查看后端日志 / Check backend logs:**
   后端启动时应该能看到 MyBatis 加载映射文件的日志
   Look for MyBatis mapper loading logs during backend startup

4. **验证配置文件 / Verify configuration:**
   确认使用的是开发环境配置（默认）
   Confirm using dev environment config (default)

### 联系支持 / Support
如果问题仍然存在，请提供：
- 完整的错误日志
- MySQL 版本
- Java 版本

If issues persist, please provide:
- Complete error logs
- MySQL version  
- Java version
