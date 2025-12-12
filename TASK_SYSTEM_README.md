# 任务管理系统使用指南

## 概述

本项目已成功集成任务管理系统，包含完整的前后端功能。

## 已实现的功能

### 后端功能 ✅

1. **任务管理 API** (`/api/task`)
   - ✅ 创建任务 (`POST /api/task/create`)
   - ✅ 更新任务 (`POST /api/task/update/{id}`)
   - ✅ 删除任务 (`POST /api/task/delete/{id}`)
   - ✅ 获取任务列表 (`GET /api/task/list`)
   - ✅ 获取任务详情 (`GET /api/task/get/{id}`)
   - ✅ 获取子任务列表 (`GET /api/task/subtasks/{parentId}`)

2. **任务日志 API** (`/api/task-log`)
   - ✅ 获取任务日志列表 (`GET /api/task-log/list/{taskId}`)
   - ✅ 获取任务统计信息 (`GET /api/task-log/statistics`)

3. **数据库表结构**
   - ✅ `task` 表：支持父子任务的层级结构
   - ✅ `task_log` 表：记录所有任务操作日志

### 前端功能 ✅

1. **任务管理页面** (`/index/task-management`)
   - ✅ 任务列表展示
   - ✅ 创建新任务
   - ✅ 编辑任务
   - ✅ 删除任务
   - ✅ 状态和优先级管理
   - ✅ 截止日期设置

2. **数据分析页面** (`/index/task-data`)
   - ✅ 任务总览统计
   - ✅ 任务状态分布
   - ✅ 完成率可视化
   - ✅ 操作日志统计

3. **菜单导航**
   - ✅ 新增"任务系统"大类菜单
   - ✅ 任务管理子菜单
   - ✅ 数据分析子菜单

## 快速开始

### 1. 数据库初始化

#### 方式一：使用主SQL文件（推荐）

```bash
mysql -u root -p test < database.sql
```

#### 方式二：使用迁移脚本

```bash
mysql -u root -p test < my-project-backend/src/main/resources/db/migration/V1__create_task_table.sql
```

### 2. 配置数据库连接

编辑 `my-project-backend/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: your_password  # 修改为你的密码
```

### 3. 启动后端

```bash
cd my-project-backend
mvn spring-boot:run
```

后端将在 `http://localhost:8080` 启动。

### 4. 启动前端

```bash
cd my-project-frontend
npm install
npm run dev
```

前端将在 `http://localhost:5173` 启动（或其他端口，查看终端输出）。

### 5. 访问系统

1. 打开浏览器访问前端地址
2. 登录系统（需要先注册账号）
3. 在左侧菜单找到"任务系统"
4. 点击"任务管理"开始使用

## 功能演示

### 创建任务

1. 点击右上角"创建任务"按钮
2. 填写任务信息：
   - 任务标题（必填）
   - 任务描述
   - 状态：待处理、进行中、已完成、已取消
   - 优先级：低、中、高、紧急
   - 截止日期
3. 点击"创建"保存

### 查看数据分析

1. 点击"数据分析"菜单
2. 查看：
   - 任务总数、待处理、进行中、已完成统计
   - 任务完成率进度条
   - 任务状态分布图表
   - 操作日志统计

## 数据库表结构

### task 表

| 字段 | 类型 | 说明 | 默认值 |
|-----|------|------|--------|
| id | int | 主键 | 自增 |
| user_id | int | 用户ID | - |
| title | varchar(255) | 任务标题 | '' |
| description | text | 任务描述 | NULL |
| status | varchar(50) | 状态 | 'pending' |
| priority | varchar(50) | 优先级 | 'medium' |
| parent_id | int | 父任务ID | NULL |
| created_at | timestamp | 创建时间 | CURRENT_TIMESTAMP |
| updated_at | timestamp | 更新时间 | CURRENT_TIMESTAMP |
| due_date | datetime | 截止日期 | NULL |

### task_log 表

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | int | 主键 |
| task_id | int | 任务ID |
| user_id | int | 用户ID |
| action | varchar(50) | 操作类型 |
| description | text | 操作描述 |
| created_at | timestamp | 创建时间 |

## 技术栈

### 后端
- Spring Boot 3.1.2
- MyBatis-Plus 3.5.3.1
- MySQL 8.x
- JWT 认证

### 前端
- Vue 3
- Element Plus
- Vue Router
- Axios

## 问题排查

### 接口返回 500 错误

**检查清单：**
1. ✅ 数据库是否正确初始化（运行 database.sql）
2. ✅ 数据库连接配置是否正确
3. ✅ 表结构是否包含所有必需字段（特别是 `parent_id` 和 `title` 默认值）
4. ✅ MyBatis-Plus 配置是否正确（`map-underscore-to-camel-case: true`）

### 前端页面打不开

**检查清单：**
1. ✅ 路由是否正确配置（`router/index.js`）
2. ✅ 菜单项是否有 `index` 属性指向正确路由
3. ✅ Vue 组件文件是否存在

### 字段映射错误

**解决方案：**
- 确保在 `application-dev.yml` 中配置了 `map-underscore-to-camel-case: true`
- 实体类中使用 `@TableField` 注解明确字段映射

## API 文档

启动后端后，访问 Swagger 文档：
```
http://localhost:8080/swagger-ui/index.html
```

## 详细文档

更多详细信息请参考：
- [database-setup.md](./database-setup.md) - 数据库设置详细文档

## 特性说明

### 已修复的问题

✅ **错误 1：缺少 parent_id 字段**
- 在 `task` 表中添加了 `parent_id` 字段
- 支持父子任务的层级结构

✅ **错误 2：title 字段没有默认值**
- `title` 字段现在有默认值 `''`（空字符串）
- 设置为 NOT NULL，防止数据不一致

✅ **错误 3：接口报错**
- `/api/task/list` 现在正常返回任务列表
- `/api/task-log/statistics` 正常返回统计数据

### 安全特性

- ✅ 所有接口都需要 JWT 认证
- ✅ 用户只能访问自己的任务数据
- ✅ 使用 MyBatis-Plus 防止 SQL 注入
- ✅ 输入数据验证（前端 + 后端）

## 贡献者

- 后端开发：Spring Boot + MyBatis-Plus
- 前端开发：Vue 3 + Element Plus
- 数据库设计：MySQL 8.x

## 许可证

本项目遵循原项目的许可证。
