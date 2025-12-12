package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.dto.Task;
import com.example.entity.dto.TaskLog;
import com.example.entity.vo.response.TaskLogVO;
import com.example.entity.vo.response.TaskStatisticsVO;
import com.example.mapper.TaskLogMapper;
import com.example.mapper.TaskMapper;
import com.example.service.TaskLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Task log service implementation
 */
@Service
public class TaskLogServiceImpl implements TaskLogService {
    
    @Resource
    TaskLogMapper taskLogMapper;
    
    @Resource
    TaskMapper taskMapper;
    
    @Override
    public void createLog(Integer userId, Long taskId, String operationType, String operationDetail) {
        TaskLog log = new TaskLog();
        log.setUserId(userId);
        log.setTaskId(taskId);
        log.setOperationType(operationType);
        log.setOperationDetail(operationDetail);
        log.setOperationTime(new Date());
        
        taskLogMapper.insert(log);
    }
    
    @Override
    public List<TaskLogVO> getUserLogs(Integer userId) {
        List<TaskLog> logs = taskLogMapper.getLogsByUserId(userId);
        return logs.stream()
                .map(log -> log.asViewObject(TaskLogVO.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public TaskStatisticsVO getStatistics(Integer userId) {
        TaskStatisticsVO stats = new TaskStatisticsVO();
        
        // Get task counts
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Task> allTasks = taskMapper.selectList(wrapper);
        
        stats.setTotalTasks(allTasks.size());
        stats.setCompletedTasks((int) allTasks.stream()
                .filter(task -> "COMPLETED".equals(task.getStatus()))
                .count());
        stats.setInProgressTasks((int) allTasks.stream()
                .filter(task -> "IN_PROGRESS".equals(task.getStatus()))
                .count());
        
        // Get operation type statistics
        List<Map<String, Object>> opTypeStats = taskLogMapper.getOperationTypeStats(userId);
        Map<String, Integer> operationTypeMap = new HashMap<>();
        for (Map<String, Object> stat : opTypeStats) {
            String type = (String) stat.get("operation_type");
            Long count = (Long) stat.get("count");
            operationTypeMap.put(type, count.intValue());
        }
        stats.setOperationTypeStats(operationTypeMap);
        
        // Get daily operation statistics (last 7 days)
        List<Map<String, Object>> dailyStats = taskLogMapper.getDailyOperationStats(userId, 7);
        Map<String, Integer> dailyMap = new HashMap<>();
        for (Map<String, Object> stat : dailyStats) {
            String date = stat.get("date").toString();
            Long count = (Long) stat.get("count");
            dailyMap.put(date, count.intValue());
        }
        stats.setDailyOperationStats(dailyMap);
        
        // Get completion trend (last 7 days)
        List<Map<String, Object>> completionStats = taskLogMapper.getCompletionTrend(userId, 7);
        Map<String, Integer> completionMap = new HashMap<>();
        for (Map<String, Object> stat : completionStats) {
            String date = stat.get("date").toString();
            Long count = (Long) stat.get("count");
            completionMap.put(date, count.intValue());
        }
        stats.setCompletionTrend(completionMap);
        
        return stats;
    }
    
    @Override
    public boolean deleteLog(Integer userId, Long logId) {
        TaskLog log = taskLogMapper.selectById(logId);
        if (log == null || !log.getUserId().equals(userId)) {
            return false;
        }
        
        int result = taskLogMapper.deleteById(logId);
        return result > 0;
    }
}
