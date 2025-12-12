package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Task;
import com.example.entity.vo.request.CreateTaskVO;
import com.example.entity.vo.request.UpdateTaskVO;

import java.util.List;

/**
 * 任务服务接口
 */
public interface TaskService extends IService<Task> {
    /**
     * 创建任务
     */
    String createTask(int userId, CreateTaskVO vo);
    
    /**
     * 更新任务
     */
    String updateTask(int userId, int taskId, UpdateTaskVO vo);
    
    /**
     * 删除任务
     */
    String deleteTask(int userId, int taskId);
    
    /**
     * 获取所有任务
     */
    List<Task> getAllTasks(int userId);
    
    /**
     * 根据ID获取任务
     */
    Task getTaskById(int userId, int taskId);
    
    /**
     * 获取子任务
     */
    List<Task> getSubTasks(int userId, int parentId);
}
