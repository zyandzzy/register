package com.example.entity.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * Request object for updating a task
 */
@Data
public class TaskUpdateVO {
    @NotNull(message = "Task ID cannot be empty")
    Long id;
    
    @NotBlank(message = "Task content cannot be empty")
    String content;
    
    @NotNull(message = "Start time cannot be empty")
    Date startTime;
    
    @NotNull(message = "End time cannot be empty")
    Date endTime;
}
