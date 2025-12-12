package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;

import java.util.Date;

/**
 * Task operation log entity
 */
@Data
@TableName("db_task_log")
public class TaskLog implements BaseData {
    @TableId(type = IdType.AUTO)
    Long id;
    Integer userId;
    Long taskId;
    String operationType;
    String operationDetail;
    Date operationTime;
}
