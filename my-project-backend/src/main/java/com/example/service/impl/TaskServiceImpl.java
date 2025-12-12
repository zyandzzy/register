package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.dto.Task;
import com.example.entity.vo.request.TaskCreateVO;
import com.example.entity.vo.request.TaskUpdateVO;
import com.example.entity.vo.response.TaskVO;
import com.example.mapper.TaskMapper;
import com.example.service.TaskLogService;
import com.example.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Task service implementation
 */
@Service
public class TaskServiceImpl implements TaskService {
    
    @Resource
    TaskMapper taskMapper;
    
    @Resource
    TaskLogService taskLogService;
    
    @Override
    @Transactional
    public boolean createTask(Integer userId, TaskCreateVO vo) {
        Task task = new Task();
        task.setUserId(userId);
        task.setParentId(vo.getParentId());
        task.setContent(vo.getContent());
        task.setStartTime(vo.getStartTime());
        task.setEndTime(vo.getEndTime());
        task.setStatus("IN_PROGRESS");
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        
        int result = taskMapper.insert(task);
        
        if (result > 0) {
            // Log the operation
            String detail = String.format("Created task: %s", vo.getContent());
            taskLogService.createLog(userId, task.getId(), "CREATE", detail);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean updateTask(Integer userId, TaskUpdateVO vo) {
        Task task = taskMapper.selectById(vo.getId());
        if (task == null || !task.getUserId().equals(userId)) {
            return false;
        }
        
        task.setContent(vo.getContent());
        task.setStartTime(vo.getStartTime());
        task.setEndTime(vo.getEndTime());
        task.setUpdateTime(new Date());
        
        int result = taskMapper.updateById(task);
        
        if (result > 0) {
            // Log the operation
            String detail = String.format("Updated task: %s", vo.getContent());
            taskLogService.createLog(userId, task.getId(), "UPDATE", detail);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean deleteTask(Integer userId, Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null || !task.getUserId().equals(userId)) {
            return false;
        }
        
        // Delete task and all children recursively
        taskMapper.deleteTaskAndChildren(taskId);
        
        // Log the operation
        String detail = String.format("Deleted task and its sub-tasks: %s", task.getContent());
        taskLogService.createLog(userId, taskId, "DELETE", detail);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean completeTask(Integer userId, Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null || !task.getUserId().equals(userId)) {
            return false;
        }
        
        task.setStatus("COMPLETED");
        task.setUpdateTime(new Date());
        
        int result = taskMapper.updateById(task);
        
        if (result > 0) {
            // Log the operation
            String detail = String.format("Completed task: %s", task.getContent());
            taskLogService.createLog(userId, taskId, "COMPLETE", detail);
            return true;
        }
        return false;
    }
    
    @Override
    public List<TaskVO> getAllTasks(Integer userId) {
        // Get all root tasks
        List<Task> rootTasks = taskMapper.getRootTasksByUserId(userId);
        
        // Build tree structure
        List<TaskVO> result = rootTasks.stream()
                .map(task -> buildTaskTree(task))
                .collect(Collectors.toList());
        
        // Log the operation
        taskLogService.createLog(userId, null, "QUERY", "Queried all tasks");
        
        return result;
    }
    
    /**
     * Build task tree recursively
     */
    private TaskVO buildTaskTree(Task task) {
        TaskVO vo = task.asViewObject(TaskVO.class);
        
        // Get sub-tasks
        List<Task> subTasks = taskMapper.getSubTasksByParentId(task.getId());
        if (subTasks != null && !subTasks.isEmpty()) {
            List<TaskVO> children = subTasks.stream()
                    .map(this::buildTaskTree)
                    .collect(Collectors.toList());
            vo.setChildren(children);
        } else {
            vo.setChildren(new ArrayList<>());
        }
        
        return vo;
    }
    
    @Override
    public Task getTaskById(Long taskId) {
        return taskMapper.selectById(taskId);
    }
    
    @Override
    public boolean verifyTaskOwnership(Integer userId, Long taskId) {
        Task task = taskMapper.selectById(taskId);
        return task != null && task.getUserId().equals(userId);
    }
}
