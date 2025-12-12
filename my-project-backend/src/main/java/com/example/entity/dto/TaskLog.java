package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 任务日志实体类
 */
@Data
@TableName("task_log")
@AllArgsConstructor
@NoArgsConstructor
public class TaskLog implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    
    @TableField("task_id")
    Integer taskId;
    
    @TableField("user_id")
    Integer userId;
    
    String action;
    String description;
    
    @TableField("created_at")
    Date createdAt;
}
