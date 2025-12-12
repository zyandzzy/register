package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Task;
import com.example.entity.vo.request.CreateTaskVO;
import com.example.entity.vo.request.UpdateTaskVO;
import com.example.mapper.TaskMapper;
import com.example.service.TaskLogService;
import com.example.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 任务服务实现类
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private TaskLogService taskLogService;

    @Override
    public String createTask(int userId, CreateTaskVO vo) {
        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(vo.getTitle());
        task.setDescription(vo.getDescription());
        task.setStatus(vo.getStatus() != null ? vo.getStatus() : "pending");
        task.setPriority(vo.getPriority() != null ? vo.getPriority() : "medium");
        task.setParentId(vo.getParentId());
        task.setDueDate(vo.getDueDate());
        task.setCreatedAt(new Date());
        task.setUpdatedAt(new Date());

        if (this.save(task)) {
            taskLogService.createLog(task.getId(), userId, "create", "创建任务: " + task.getTitle());
            return null;
        }
        return "创建任务失败";
    }

    @Override
    public String updateTask(int userId, int taskId, UpdateTaskVO vo) {
        Task task = this.getTaskById(userId, taskId);
        if (task == null) {
            return "任务不存在";
        }

        if (vo.getTitle() != null) {
            task.setTitle(vo.getTitle());
        }
        if (vo.getDescription() != null) {
            task.setDescription(vo.getDescription());
        }
        if (vo.getStatus() != null) {
            task.setStatus(vo.getStatus());
        }
        if (vo.getPriority() != null) {
            task.setPriority(vo.getPriority());
        }
        if (vo.getParentId() != null) {
            task.setParentId(vo.getParentId());
        }
        if (vo.getDueDate() != null) {
            task.setDueDate(vo.getDueDate());
        }
        task.setUpdatedAt(new Date());

        if (this.updateById(task)) {
            taskLogService.createLog(taskId, userId, "update", "更新任务: " + task.getTitle());
            return null;
        }
        return "更新任务失败";
    }

    @Override
    public String deleteTask(int userId, int taskId) {
        Task task = this.getTaskById(userId, taskId);
        if (task == null) {
            return "任务不存在";
        }

        // 递归删除所有子任务
        deleteTaskWithChildren(userId, taskId);
        
        taskLogService.createLog(taskId, userId, "delete", "删除任务: " + task.getTitle());
        return null;
    }
    
    /**
     * 递归删除任务及其所有子任务
     */
    private void deleteTaskWithChildren(int userId, int taskId) {
        // 先查找所有子任务
        List<Task> subTasks = this.getSubTasks(userId, taskId);
        
        // 递归删除每个子任务
        for (Task subTask : subTasks) {
            try {
                deleteTaskWithChildren(userId, subTask.getId());
            } catch (Exception e) {
                // 如果子任务删除失败，抛出异常终止整个删除操作
                throw new RuntimeException("删除子任务失败: " + subTask.getTitle(), e);
            }
        }
        
        // 删除当前任务
        if (!this.removeById(taskId)) {
            throw new RuntimeException("删除任务失败，任务ID: " + taskId);
        }
    }

    @Override
    public List<Task> getAllTasks(int userId) {
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("created_at");
        return this.list(wrapper);
    }

    @Override
    public Task getTaskById(int userId, int taskId) {
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("id", taskId);
        wrapper.eq("user_id", userId);
        return this.getOne(wrapper);
    }

    @Override
    public List<Task> getSubTasks(int userId, int parentId) {
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("parent_id", parentId);
        wrapper.orderByDesc("created_at");
        return this.list(wrapper);
    }
}
