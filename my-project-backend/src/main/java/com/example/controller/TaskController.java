package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Task;
import com.example.entity.vo.request.CreateTaskVO;
import com.example.entity.vo.request.UpdateTaskVO;
import com.example.entity.vo.response.TaskVO;
import com.example.service.TaskService;
import com.example.utils.Const;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务管理控制器
 */
@RestController
@RequestMapping("/api/task")
@Tag(name = "任务管理", description = "任务相关接口")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    @Operation(summary = "创建任务")
    public RestBean<Void> createTask(@Valid @RequestBody CreateTaskVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        String result = taskService.createTask(userId, vo);
        return result == null ? RestBean.success() : RestBean.failure(400, result);
    }

    @PostMapping("/update/{id}")
    @Operation(summary = "更新任务")
    public RestBean<Void> updateTask(@PathVariable int id,
                                      @Valid @RequestBody UpdateTaskVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        String result = taskService.updateTask(userId, id, vo);
        return result == null ? RestBean.success() : RestBean.failure(400, result);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除任务")
    public RestBean<Void> deleteTask(@PathVariable int id,
                                      @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        String result = taskService.deleteTask(userId, id);
        return result == null ? RestBean.success() : RestBean.failure(400, result);
    }

    @GetMapping("/list")
    @Operation(summary = "获取任务列表")
    public RestBean<List<TaskVO>> getAllTasks(@RequestAttribute(Const.ATTR_USER_ID) int userId) {
        List<Task> tasks = taskService.getAllTasks(userId);
        List<TaskVO> taskVOs = tasks.stream()
                .map(task -> task.asViewObject(TaskVO.class))
                .collect(Collectors.toList());
        return RestBean.success(taskVOs);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取任务详情")
    public RestBean<TaskVO> getTask(@PathVariable int id,
                                     @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        Task task = taskService.getTaskById(userId, id);
        if (task == null) {
            return RestBean.failure(404, "任务不存在");
        }
        return RestBean.success(task.asViewObject(TaskVO.class));
    }

    @GetMapping("/subtasks/{parentId}")
    @Operation(summary = "获取子任务列表")
    public RestBean<List<TaskVO>> getSubTasks(@PathVariable int parentId,
                                               @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        List<Task> tasks = taskService.getSubTasks(userId, parentId);
        List<TaskVO> taskVOs = tasks.stream()
                .map(task -> task.asViewObject(TaskVO.class))
                .collect(Collectors.toList());
        return RestBean.success(taskVOs);
    }
}
