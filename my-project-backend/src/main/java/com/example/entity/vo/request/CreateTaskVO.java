package com.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 创建任务请求VO
 */
@Data
public class CreateTaskVO {
    @NotNull(message = "任务标题不能为空")
    String title;
    String description;
    String status;
    String priority;
    Integer parentId;
    Date dueDate;
}
