package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.TaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Task log mapper
 */
@Mapper
public interface TaskLogMapper extends BaseMapper<TaskLog> {
    
    /**
     * Get all logs for a user
     */
    @Select("SELECT * FROM db_task_log WHERE user_id = #{userId} ORDER BY operation_time DESC")
    List<TaskLog> getLogsByUserId(@Param("userId") Integer userId);
    
    /**
     * Get operation type statistics
     */
    @Select("SELECT operation_type, COUNT(*) as count FROM db_task_log WHERE user_id = #{userId} GROUP BY operation_type")
    List<Map<String, Object>> getOperationTypeStats(@Param("userId") Integer userId);
    
    /**
     * Get daily operation statistics for last N days
     */
    @Select("SELECT DATE(operation_time) as date, COUNT(*) as count " +
            "FROM db_task_log WHERE user_id = #{userId} " +
            "AND operation_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(operation_time) ORDER BY date")
    List<Map<String, Object>> getDailyOperationStats(@Param("userId") Integer userId, @Param("days") Integer days);
    
    /**
     * Get completion trend for last N days
     */
    @Select("SELECT DATE(operation_time) as date, COUNT(*) as count " +
            "FROM db_task_log WHERE user_id = #{userId} " +
            "AND operation_type = 'COMPLETE' " +
            "AND operation_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(operation_time) ORDER BY date")
    List<Map<String, Object>> getCompletionTrend(@Param("userId") Integer userId, @Param("days") Integer days);
}
