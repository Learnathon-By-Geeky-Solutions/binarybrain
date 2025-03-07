package com.binaryBrain.task_microservice.mapper;

import com.binaryBrain.task_microservice.dto.TaskDto;
import com.binaryBrain.task_microservice.model.Task;

public class TaskMapper {
    private TaskMapper(){
        throw new RuntimeException("This is a Utility class and can't be instantiated!");
    }

    public static TaskDto toTaskDto(Task task){
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setDeadline(task.getDeadline());
        taskDto.setAttachmentUrl(task.getAttachmentUrl());
        taskDto.setTeacherId(task.getTeacherId());
        taskDto.setStatus(task.getStatus());
        return taskDto;
    }

    public static Task toTask(TaskDto taskDto){
        Task task = new Task();
        task.setId(taskDto.getId());
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setCreatedAt(taskDto.getCreatedAt());
        task.setDeadline(taskDto.getDeadline());
        task.setAttachmentUrl(taskDto.getAttachmentUrl());
        task.setTeacherId(taskDto.getTeacherId());
        task.setStatus(taskDto.getStatus());
        return task;
    }
}
