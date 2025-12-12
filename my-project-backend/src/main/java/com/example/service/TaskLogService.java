package com.example.service;

import com.example.entity.dto.TaskLog;
import com.example.entity.vo.response.TaskLogVO;
import com.example.entity.vo.response.TaskStatisticsVO;

import java.util.List;

/**
 * Task log service interface
 */
public interface TaskLogService {
    
    /**
     * Create operation log
     */
    void createLog(Integer userId, Long taskId, String operationType, String operationDetail);
    
    /**
     * Get all logs for a user
     */
    List<TaskLogVO> getUserLogs(Integer userId);
    
    /**
     * Get task statistics
     */
    TaskStatisticsVO getStatistics(Integer userId);
    
    /**
     * Delete a log
     */
    boolean deleteLog(Integer userId, Long logId);
}
