package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Task mapper
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    
    /**
     * Get all tasks for a user including sub-tasks
     */
    @Select("SELECT * FROM db_task WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Task> getAllTasksByUserId(@Param("userId") Integer userId);
    
    /**
     * Get root tasks (tasks without parent)
     */
    @Select("SELECT * FROM db_task WHERE user_id = #{userId} AND parent_id IS NULL ORDER BY create_time DESC")
    List<Task> getRootTasksByUserId(@Param("userId") Integer userId);
    
    /**
     * Get sub-tasks by parent ID
     */
    @Select("SELECT * FROM db_task WHERE parent_id = #{parentId} ORDER BY create_time DESC")
    List<Task> getSubTasksByParentId(@Param("parentId") Long parentId);
    
    /**
     * Delete task and all its sub-tasks recursively
     */
    void deleteTaskAndChildren(@Param("taskId") Long taskId);
}
