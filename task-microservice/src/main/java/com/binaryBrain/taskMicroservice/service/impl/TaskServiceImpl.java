package com.binaryBrain.taskMicroservice.service.impl;

import com.binaryBrain.exception.ResourceNotFoundException;
import com.binaryBrain.exception.UserHasNotPermissionException;
import com.binaryBrain.taskMicroservice.dto.RoleDto;
import com.binaryBrain.taskMicroservice.dto.TaskDto;
import com.binaryBrain.taskMicroservice.dto.UserDto;
import com.binaryBrain.taskMicroservice.mapper.TaskMapper;
import com.binaryBrain.taskMicroservice.model.Task;
import com.binaryBrain.taskMicroservice.model.TaskStatus;
import com.binaryBrain.taskMicroservice.repository.TaskRepository;
import com.binaryBrain.taskMicroservice.service.TaskService;
import com.binaryBrain.taskMicroservice.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

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
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline must be in the future");
        }
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, List.of("TEACHER")))
            throw new UserHasNotPermissionException("Only TEACHER can create assignment!");

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
        userService.getUserProfile(username);
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

        if (updatedTaskDto.getTitle() != null)
            existingTaskDto.setTitle(updatedTaskDto.getTitle());
        if (updatedTaskDto.getDescription() != null)
            existingTaskDto.setDescription(updatedTaskDto.getDescription());
        if (updatedTaskDto.getDeadline() != null) {
            if (updatedTaskDto.getDeadline().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Deadline must be in the future");
            }
            existingTaskDto.setDeadline(updatedTaskDto.getDeadline());
        }
        if (updatedTaskDto.getStatus() != null)
            existingTaskDto.setStatus(updatedTaskDto.getStatus());
        if (updatedTaskDto.getAttachmentUrl() != null)
            existingTaskDto.setAttachmentUrl(updatedTaskDto.getAttachmentUrl());

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
        boolean isTeacher = validateRole(userDto, List.of("TEACHER"));

        if ((!isTeacher || !taskDto.getTeacherId().equals(userDto.getId()))) {
            throw new UserHasNotPermissionException("You do not have permission to modify this task.");
        }
    }
}
