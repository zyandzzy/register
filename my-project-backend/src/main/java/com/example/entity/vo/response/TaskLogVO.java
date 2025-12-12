package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * Task log view object for response
 */
@Data
public class TaskLogVO {
    Long id;
    Long taskId;
    String operationType;
    String operationDetail;
    Date operationTime;
}
