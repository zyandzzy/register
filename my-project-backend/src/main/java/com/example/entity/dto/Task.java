package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;

import java.util.Date;

/**
 * Task entity
 */
@Data
@TableName("db_task")
public class Task implements BaseData {
    @TableId(type = IdType.AUTO)
    Long id;
    Integer userId;
    Long parentId;
    String content;
    Date startTime;
    Date endTime;
    String status;
    Date createTime;
    Date updateTime;
}
