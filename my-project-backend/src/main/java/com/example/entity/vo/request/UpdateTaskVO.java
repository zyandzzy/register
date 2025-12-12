package com.example.entity.vo.request;

import lombok.Data;

import java.util.Date;

/**
 * 更新任务请求VO
 */
@Data
public class UpdateTaskVO {
    String title;
    String description;
    String status;
    String priority;
    Integer parentId;
    Date dueDate;
}
