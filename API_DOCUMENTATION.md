# Task Management System API Documentation

## Overview
This document describes the RESTful API endpoints for the Task Management System.

Base URL: `/api`

All endpoints require authentication via Bearer token in the Authorization header.

## Task Management Endpoints

### 1. Create Task
**Endpoint:** `POST /api/task/create`

**Description:** Create a new task (root task or sub-task)

**Request Body:**
```json
{
  "parentId": null,  // Optional: null for root tasks, or parent task ID for sub-tasks
  "content": "Task description",
  "startTime": "2024-12-12T10:00:00",
  "endTime": "2024-12-15T18:00:00"
}
```

**Response:**
```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

### 2. Update Task
**Endpoint:** `POST /api/task/update`

**Description:** Update an existing task's details

**Request Body:**
```json
{
  "id": 1,
  "content": "Updated task description",
  "startTime": "2024-12-12T10:00:00",
  "endTime": "2024-12-15T18:00:00"
}
```

**Response:**
```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

### 3. Delete Task
**Endpoint:** `DELETE /api/task/delete/{taskId}`

**Description:** Delete a task and all its sub-tasks (cascade delete)

**Path Parameters:**
- `taskId`: The ID of the task to delete

**Response:**
```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

### 4. Complete Task
**Endpoint:** `POST /api/task/complete/{taskId}`

**Description:** Mark a task as completed

**Path Parameters:**
- `taskId`: The ID of the task to complete

**Response:**
```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

### 5. Get All Tasks
**Endpoint:** `GET /api/task/list`

**Description:** Retrieve all tasks in a tree structure

**Response:**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "parentId": null,
      "content": "Root task",
      "startTime": "2024-12-12T10:00:00",
      "endTime": "2024-12-15T18:00:00",
      "status": "IN_PROGRESS",
      "createTime": "2024-12-12T09:00:00",
      "updateTime": "2024-12-12T09:00:00",
      "children": [
        {
          "id": 2,
          "parentId": 1,
          "content": "Sub-task 1",
          "startTime": "2024-12-12T10:00:00",
          "endTime": "2024-12-13T18:00:00",
          "status": "COMPLETED",
          "createTime": "2024-12-12T09:05:00",
          "updateTime": "2024-12-12T15:00:00",
          "children": []
        }
      ]
    }
  ],
  "message": "success"
}
```

## Task Log Endpoints

### 1. Get Operation Logs
**Endpoint:** `GET /api/task-log/list`

**Description:** Retrieve all operation logs for the current user

**Response:**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "taskId": 1,
      "operationType": "CREATE",
      "operationDetail": "Created task: Root task",
      "operationTime": "2024-12-12T09:00:00"
    },
    {
      "id": 2,
      "taskId": 2,
      "operationType": "COMPLETE",
      "operationDetail": "Completed task: Sub-task 1",
      "operationTime": "2024-12-12T15:00:00"
    }
  ],
  "message": "success"
}
```

### 2. Get Statistics
**Endpoint:** `GET /api/task-log/statistics`

**Description:** Get task statistics and analytics

**Response:**
```json
{
  "code": 200,
  "data": {
    "totalTasks": 10,
    "completedTasks": 5,
    "inProgressTasks": 5,
    "operationTypeStats": {
      "CREATE": 10,
      "UPDATE": 5,
      "DELETE": 2,
      "COMPLETE": 5,
      "QUERY": 20
    },
    "dailyOperationStats": {
      "2024-12-12": 15,
      "2024-12-11": 8,
      "2024-12-10": 12
    },
    "completionTrend": {
      "2024-12-12": 3,
      "2024-12-11": 1,
      "2024-12-10": 2
    }
  },
  "message": "success"
}
```

### 3. Delete Log
**Endpoint:** `DELETE /api/task-log/delete/{logId}`

**Description:** Delete an operation log

**Path Parameters:**
- `logId`: The ID of the log to delete

**Response:**
```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

## Data Models

### Task Status
- `IN_PROGRESS`: Task is currently in progress
- `COMPLETED`: Task has been completed

### Operation Types
- `CREATE`: Task creation operation
- `UPDATE`: Task update operation
- `DELETE`: Task deletion operation
- `COMPLETE`: Task completion operation
- `QUERY`: Task query operation

## Error Responses

All endpoints may return error responses in the following format:

```json
{
  "code": 400,
  "data": null,
  "message": "Error message description"
}
```

Common error codes:
- `400`: Bad request or operation failed
- `401`: Unauthorized (token expired or invalid)
- `404`: Resource not found

## Notes

1. All timestamps are in ISO 8601 format
2. All operations are automatically logged to the task log
3. Deleting a parent task will cascade delete all sub-tasks
4. Task ownership is verified for all operations
5. Statistics are calculated in real-time based on the user's data
