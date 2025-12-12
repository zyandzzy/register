package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.TaskCreateVO;
import com.example.entity.vo.request.TaskUpdateVO;
import com.example.entity.vo.response.TaskVO;
import com.example.service.TaskService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Task management controller
 */
@RestController
@RequestMapping("/api/task")
public class TaskController {
    
    @Resource
    TaskService taskService;
    
    /**
     * Create a new task
     */
    @PostMapping("/create")
    public RestBean<Void> createTask(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                      @RequestBody @Valid TaskCreateVO vo) {
        boolean success = taskService.createTask(userId, vo);
        return success ? RestBean.success() : RestBean.failure(400, "Failed to create task");
    }
    
    /**
     * Update a task
     */
    @PostMapping("/update")
    public RestBean<Void> updateTask(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                      @RequestBody @Valid TaskUpdateVO vo) {
        boolean success = taskService.updateTask(userId, vo);
        return success ? RestBean.success() : RestBean.failure(400, "Failed to update task or task not found");
    }
    
    /**
     * Delete a task and all its sub-tasks
     */
    @DeleteMapping("/delete/{taskId}")
    public RestBean<Void> deleteTask(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                      @PathVariable Long taskId) {
        boolean success = taskService.deleteTask(userId, taskId);
        return success ? RestBean.success() : RestBean.failure(400, "Failed to delete task or task not found");
    }
    
    /**
     * Complete a task
     */
    @PostMapping("/complete/{taskId}")
    public RestBean<Void> completeTask(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                        @PathVariable Long taskId) {
        boolean success = taskService.completeTask(userId, taskId);
        return success ? RestBean.success() : RestBean.failure(400, "Failed to complete task or task not found");
    }
    
    /**
     * Get all tasks in tree structure
     */
    @GetMapping("/list")
    public RestBean<List<TaskVO>> getAllTasks(@RequestAttribute(Const.ATTR_USER_ID) int userId) {
        List<TaskVO> tasks = taskService.getAllTasks(userId);
        return RestBean.success(tasks);
    }
}
