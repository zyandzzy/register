package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Task;
import com.example.entity.dto.TaskLog;
import com.example.mapper.TaskLogMapper;
import com.example.mapper.TaskMapper;
import com.example.service.TaskLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务日志服务实现类
 */
@Service
public class TaskLogServiceImpl extends ServiceImpl<TaskLogMapper, TaskLog> implements TaskLogService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public void createLog(int taskId, int userId, String action, String description) {
        TaskLog log = new TaskLog();
        log.setTaskId(taskId);
        log.setUserId(userId);
        log.setAction(action);
        log.setDescription(description);
        log.setCreatedAt(new Date());
        this.save(log);
    }

    @Override
    public List<TaskLog> getTaskLogs(int taskId) {
        QueryWrapper<TaskLog> wrapper = new QueryWrapper<>();
        wrapper.eq("task_id", taskId);
        wrapper.orderByDesc("created_at");
        return this.list(wrapper);
    }

    @Override
    public Map<String, Object> getStatistics(int userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取用户的所有任务
        QueryWrapper<Task> taskWrapper = new QueryWrapper<>();
        taskWrapper.eq("user_id", userId);
        List<Task> tasks = taskMapper.selectList(taskWrapper);
        
        // 统计任务数量
        long totalTasks = tasks.size();
        long pendingTasks = tasks.stream().filter(t -> "pending".equals(t.getStatus())).count();
        long inProgressTasks = tasks.stream().filter(t -> "in_progress".equals(t.getStatus())).count();
        long completedTasks = tasks.stream().filter(t -> "completed".equals(t.getStatus())).count();
        long cancelledTasks = tasks.stream().filter(t -> "cancelled".equals(t.getStatus())).count();
        
        stats.put("totalTasks", totalTasks);
        stats.put("pendingTasks", pendingTasks);
        stats.put("inProgressTasks", inProgressTasks);
        stats.put("completedTasks", completedTasks);
        stats.put("cancelledTasks", cancelledTasks);
        
        // 统计日志数量
        QueryWrapper<TaskLog> logWrapper = new QueryWrapper<>();
        logWrapper.eq("user_id", userId);
        long totalLogs = this.count(logWrapper);
        stats.put("totalLogs", totalLogs);
        
        // 统计各种操作的数量
        QueryWrapper<TaskLog> createWrapper = new QueryWrapper<>();
        createWrapper.eq("user_id", userId).eq("action", "create");
        stats.put("createActions", this.count(createWrapper));
        
        QueryWrapper<TaskLog> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("user_id", userId).eq("action", "update");
        stats.put("updateActions", this.count(updateWrapper));
        
        QueryWrapper<TaskLog> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("user_id", userId).eq("action", "delete");
        stats.put("deleteActions", this.count(deleteWrapper));
        
        return stats;
    }
}
