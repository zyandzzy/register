package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.TaskLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务日志Mapper接口
 */
@Mapper
public interface TaskLogMapper extends BaseMapper<TaskLog> {
}
