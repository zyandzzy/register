package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * 任务日志响应VO
 */
@Data
public class TaskLogVO {
    Integer id;
    Integer taskId;
    Integer userId;
    String action;
    String description;
    Date createdAt;
}
