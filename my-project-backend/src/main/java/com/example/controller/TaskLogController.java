package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.TaskLogVO;
import com.example.entity.vo.response.TaskStatisticsVO;
import com.example.service.TaskLogService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Task log and statistics controller
 */
@RestController
@RequestMapping("/api/task-log")
public class TaskLogController {
    
    @Resource
    TaskLogService taskLogService;
    
    /**
     * Get all operation logs for current user
     */
    @GetMapping("/list")
    public RestBean<List<TaskLogVO>> getUserLogs(@RequestAttribute(Const.ATTR_USER_ID) int userId) {
        List<TaskLogVO> logs = taskLogService.getUserLogs(userId);
        return RestBean.success(logs);
    }
    
    /**
     * Get task statistics
     */
    @GetMapping("/statistics")
    public RestBean<TaskStatisticsVO> getStatistics(@RequestAttribute(Const.ATTR_USER_ID) int userId) {
        TaskStatisticsVO stats = taskLogService.getStatistics(userId);
        return RestBean.success(stats);
    }
    
    /**
     * Delete an operation log
     */
    @DeleteMapping("/delete/{logId}")
    public RestBean<Void> deleteLog(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                     @PathVariable Long logId) {
        boolean success = taskLogService.deleteLog(userId, logId);
        return success ? RestBean.success() : RestBean.failure(400, "Failed to delete log or log not found");
    }
}
