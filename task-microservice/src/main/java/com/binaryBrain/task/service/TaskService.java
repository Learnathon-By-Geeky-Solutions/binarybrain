package com.binarybrain.task.service;

import com.binarybrain.task.dto.TaskDto;
import com.binarybrain.task.model.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskDto createTask(TaskDto taskDto, String username);
    TaskDto getTaskById(Long id, String username);
    List<TaskDto> getAllTask(TaskStatus status, String username);
    List<TaskDto> getAllTaskByTeacherId(Long id, String username);
    List<TaskDto> getTasksbyIds(List<Long> taskIds, String username);
    TaskDto closeTask(Long taskId, String username);
    TaskDto updateTask(Long taskId, TaskDto updatedTaskDto, String username);
    void deleteTaskById(Long id, String username);
}
