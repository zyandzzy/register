-- Create task table with all required fields
CREATE TABLE IF NOT EXISTS `task` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '任务标题',
  `description` text COMMENT '任务描述',
  `status` varchar(50) NOT NULL DEFAULT 'pending' COMMENT '任务状态: pending, in_progress, completed, cancelled',
  `priority` varchar(50) NOT NULL DEFAULT 'medium' COMMENT '优先级: low, medium, high, urgent',
  `parent_id` int DEFAULT NULL COMMENT '父任务ID，用于子任务',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `due_date` datetime DEFAULT NULL COMMENT '截止日期',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务表';

-- Create task_log table for task activity tracking
CREATE TABLE IF NOT EXISTS `task_log` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `task_id` int NOT NULL COMMENT '任务ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `action` varchar(50) NOT NULL COMMENT '操作类型: create, update, delete, complete, cancel',
  `description` text COMMENT '操作描述',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务日志表';
