package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Task view object for response
 */
@Data
public class TaskVO {
    Long id;
    Long parentId;
    String content;
    Date startTime;
    Date endTime;
    String status;
    Date createTime;
    Date updateTime;
    List<TaskVO> children;  // Sub-tasks
}
