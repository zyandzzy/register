package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * 任务响应VO
 */
@Data
public class TaskVO {
    Integer id;
    Integer userId;
    String title;
    String description;
    String status;
    String priority;
    Integer parentId;
    Date createdAt;
    Date updatedAt;
    Date dueDate;
}
