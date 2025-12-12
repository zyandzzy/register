package com.example.service;

import com.example.entity.dto.Task;
import com.example.entity.vo.request.TaskCreateVO;
import com.example.entity.vo.request.TaskUpdateVO;
import com.example.entity.vo.response.TaskVO;

import java.util.List;

/**
 * Task service interface
 */
public interface TaskService {
    
    /**
     * Create a new task
     */
    boolean createTask(Integer userId, TaskCreateVO vo);
    
    /**
     * Update a task
     */
    boolean updateTask(Integer userId, TaskUpdateVO vo);
    
    /**
     * Delete a task and all its sub-tasks
     */
    boolean deleteTask(Integer userId, Long taskId);
    
    /**
     * Complete a task
     */
    boolean completeTask(Integer userId, Long taskId);
    
    /**
     * Get all tasks in tree structure
     */
    List<TaskVO> getAllTasks(Integer userId);
    
    /**
     * Get task by ID
     */
    Task getTaskById(Long taskId);
    
    /**
     * Verify task ownership
     */
    boolean verifyTaskOwnership(Integer userId, Long taskId);
}
