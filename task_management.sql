/*
 Task Management System Database Schema
 
 Date: 12/12/2024
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for db_task
-- ----------------------------
DROP TABLE IF EXISTS `db_task`;
CREATE TABLE `db_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Task ID',
  `user_id` int NOT NULL COMMENT 'User ID',
  `parent_id` bigint DEFAULT NULL COMMENT 'Parent task ID, NULL for root tasks',
  `content` text NOT NULL COMMENT 'Task content/description',
  `start_time` datetime NOT NULL COMMENT 'Task start time',
  `end_time` datetime NOT NULL COMMENT 'Task end time',
  `status` varchar(20) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT 'Task status: IN_PROGRESS, COMPLETED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Task table';

-- ----------------------------
-- Table structure for db_task_log
-- ----------------------------
DROP TABLE IF EXISTS `db_task_log`;
CREATE TABLE `db_task_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Log ID',
  `user_id` int NOT NULL COMMENT 'User ID',
  `task_id` bigint DEFAULT NULL COMMENT 'Task ID',
  `operation_type` varchar(20) NOT NULL COMMENT 'Operation type: CREATE, UPDATE, DELETE, QUERY, COMPLETE',
  `operation_detail` text COMMENT 'Operation details',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Operation time',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Task operation log table';

SET FOREIGN_KEY_CHECKS = 1;
