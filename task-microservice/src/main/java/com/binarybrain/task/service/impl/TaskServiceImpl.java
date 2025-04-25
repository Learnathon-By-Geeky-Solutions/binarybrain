package com.binarybrain.task.service.impl;

import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.exception.UserHasNotPermissionException;
import com.binarybrain.exception.global.GlobalExceptionHandler;
import com.binarybrain.task.dto.*;
import com.binarybrain.task.mapper.TaskMapper;
import com.binarybrain.task.model.Task;
import com.binarybrain.task.model.TaskStatus;
import com.binarybrain.task.repository.TaskRepository;
import com.binarybrain.task.service.TaskService;
import com.binarybrain.task.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String ADMIN = "ADMIN";
    private static final String TEACHER = "TEACHER";
    private final TaskRepository taskRepository;
    private final UserService userService;


    public TaskServiceImpl(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }
    private boolean validateRole(UserDto userDto, List<String> targetRoles) {
        return userDto.getRoles()
                .stream()
                .map(RoleDto::getName)
                .anyMatch(targetRoles::contains);
    }
    @Override
    public TaskDto createTask(TaskDto taskDto, String username) {
        LocalDateTime deadline = taskDto.getDeadline();
        GlobalExceptionHandler.Thrower.throwIf(
                deadline.isBefore(LocalDateTime.now()),
                new IllegalArgumentException("Deadline must be in the future"));

        UserDto userDto = userService.getUserProfile(username);
        GlobalExceptionHandler.Thrower.throwIf(
                !validateRole(userDto, List.of(TEACHER)),
                new UserHasNotPermissionException("Only TEACHER can create assignment!"));

        Long teacherId = userDto.getId();
        Task task = TaskMapper.toTask(taskDto);
        task.setTeacherId(teacherId);
        task.setStatus(TaskStatus.OPEN);
        task.setCreatedAt(LocalDateTime.now());
        task.setDeadline(taskDto.getDeadline());
        taskRepository.save(task);
        return TaskMapper.toTaskDto(task);
    }
    @Override
    public TaskDto getTaskById(Long id, String username) {
        userService.getUserProfile(username);
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + id));
        return TaskMapper.toTaskDto(task);
    }
    @Override
    public List<TaskDto> getAllTask(TaskStatus status, String username) {
        List<Task> taskList;
        if(status == null)
            taskList = taskRepository.findAll();
        else
            taskList = taskRepository.findByStatus(status);

        return taskList.stream()
                .map(TaskMapper::toTaskDto)
                .toList();
    }
    @Override
    public List<TaskDto> getAllTaskByTeacherId(Long id, String username) {
        UserDto userDto = userService.getUserProfile(username);
        boolean isAdmin = validateRole(userDto, List.of(ADMIN));
        GlobalExceptionHandler.Thrower.throwIf(
                (!isAdmin && !userDto.getId().equals(id)),
                new UserHasNotPermissionException("You do not have permission to search another teacher's assignment!"));
        List<Task> taskList = taskRepository.findByTeacherId(id);
        return taskList.stream()
                .map(TaskMapper::toTaskDto)
                .toList();
    }
    @Override
    public List<TaskDto> getTasksbyIds(List<Long> taskIds, String username) {
        userService.getUserProfile(username);
        List<Task> taskList = taskRepository.findByIdIn(taskIds);

        return taskList.stream()
                .map(TaskMapper::toTaskDto)
                .toList();
    }
    @Override
    public TaskDto closeTask(Long taskId, String username) {
        TaskDto taskDto = getTaskById(taskId, username);
        validateTaskModificationPermission(taskDto, username);

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        task.setStatus(TaskStatus.CLOSED);
        taskRepository.save(task);
        return TaskMapper.toTaskDto(task);
    }
    @Override
    public TaskDto updateTask(Long taskId, TaskDto updatedTaskDto, String username) {
        TaskDto existingTaskDto = getTaskById(taskId, username);
        validateTaskModificationPermission(existingTaskDto, username);

        Optional.ofNullable(updatedTaskDto.getTitle()).ifPresent(existingTaskDto::setTitle);
        Optional.ofNullable(updatedTaskDto.getDescription()).ifPresent(existingTaskDto::setDescription);
        Optional.ofNullable(updatedTaskDto.getDeadline()).ifPresent(localDateTime -> {
            GlobalExceptionHandler.Thrower.throwIf(updatedTaskDto.getDeadline().isBefore(LocalDateTime.now()),new IllegalArgumentException("Deadline must be in the future"));
            existingTaskDto.setDeadline(updatedTaskDto.getDeadline());
        });

        Optional.ofNullable(updatedTaskDto.getStatus()).ifPresent(existingTaskDto::setStatus);
        Optional.ofNullable(updatedTaskDto.getAttachmentUrl()).ifPresent(existingTaskDto::setAttachmentUrl);

        Task existingTask = TaskMapper.toTask(existingTaskDto);
        taskRepository.save(existingTask);
        return TaskMapper.toTaskDto(existingTask);
    }
    @Override
    public void deleteTaskById(Long id, String username) {
        TaskDto taskDto = getTaskById(id, username);
        validateTaskModificationPermission(taskDto, username);

        taskRepository.deleteById(id);
    }
    private void validateTaskModificationPermission(TaskDto taskDto, String username) {
        UserDto userDto = userService.getUserProfile(username);
        boolean isAdmin = validateRole(userDto, List.of(ADMIN));

        GlobalExceptionHandler.Thrower.throwIf(
                (!isAdmin && !taskDto.getTeacherId().equals(userDto.getId())),
                new UserHasNotPermissionException("You do not have permission to modify this task."));
    }
}
