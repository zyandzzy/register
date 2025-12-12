package com.example.entity.vo.response;

import lombok.Data;

import java.util.Map;

/**
 * Task statistics view object
 */
@Data
public class TaskStatisticsVO {
    // Total task count
    Integer totalTasks;
    
    // Completed task count
    Integer completedTasks;
    
    // In-progress task count
    Integer inProgressTasks;
    
    // Operation type statistics
    Map<String, Integer> operationTypeStats;
    
    // Daily operation count (last 7 days)
    Map<String, Integer> dailyOperationStats;
    
    // Completion trend (last 7 days)
    Map<String, Integer> completionTrend;
}
