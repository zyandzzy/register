package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.TaskLog;

import java.util.List;
import java.util.Map;

/**
 * 任务日志服务接口
 */
public interface TaskLogService extends IService<TaskLog> {
    /**
     * 创建任务日志
     */
    void createLog(int taskId, int userId, String action, String description);
    
    /**
     * 获取任务日志
     */
    List<TaskLog> getTaskLogs(int taskId);
    
    /**
     * 获取统计信息
     */
    Map<String, Object> getStatistics(int userId);
}
