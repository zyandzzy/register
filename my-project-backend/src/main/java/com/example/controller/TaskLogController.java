package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.TaskLog;
import com.example.entity.vo.response.TaskLogVO;
import com.example.service.TaskLogService;
import com.example.utils.Const;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务日志控制器
 */
@RestController
@RequestMapping("/api/task-log")
@Tag(name = "任务日志", description = "任务日志相关接口")
public class TaskLogController {

    @Autowired
    private TaskLogService taskLogService;

    @GetMapping("/list/{taskId}")
    @Operation(summary = "获取任务日志列表")
    public RestBean<List<TaskLogVO>> getTaskLogs(@PathVariable int taskId) {
        List<TaskLog> logs = taskLogService.getTaskLogs(taskId);
        List<TaskLogVO> logVOs = logs.stream()
                .map(log -> log.asViewObject(TaskLogVO.class))
                .collect(Collectors.toList());
        return RestBean.success(logVOs);
    }
    
    @GetMapping("/user-logs")
    @Operation(summary = "获取用户所有日志")
    public RestBean<List<TaskLogVO>> getUserLogs(@RequestAttribute(Const.ATTR_USER_ID) int userId) {
        List<TaskLog> logs = taskLogService.getUserLogs(userId);
        List<TaskLogVO> logVOs = logs.stream()
                .map(log -> log.asViewObject(TaskLogVO.class))
                .collect(Collectors.toList());
        return RestBean.success(logVOs);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取任务统计信息")
    public RestBean<Map<String, Object>> getStatistics(@RequestAttribute(Const.ATTR_USER_ID) int userId) {
        Map<String, Object> stats = taskLogService.getStatistics(userId);
        return RestBean.success(stats);
    }
    
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除任务日志")
    public RestBean<Void> deleteLog(@PathVariable int id,
                                     @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        String result = taskLogService.deleteLog(userId, id);
        return result == null ? RestBean.success() : RestBean.failure(400, result);
    }
}
