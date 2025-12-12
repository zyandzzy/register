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
 * 任务实体类
 */
@Data
@TableName("task")
@AllArgsConstructor
@NoArgsConstructor
public class Task implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    
    @TableField("user_id")
    Integer userId;
    
    String title;
    String description;
    String status;
    String priority;
    
    @TableField("parent_id")
    Integer parentId;
    
    @TableField("start_date")
    Date startDate;
    
    @TableField("end_date")
    Date endDate;
    
    @TableField("created_at")
    Date createdAt;
    
    @TableField("updated_at")
    Date updatedAt;
    
    @TableField("due_date")
    Date dueDate;
}
