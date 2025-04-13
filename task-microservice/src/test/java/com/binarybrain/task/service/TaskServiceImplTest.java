package com.binarybrain.task.service;

import com.binarybrain.exception.*;
import com.binarybrain.task.dto.RoleDto;
import com.binarybrain.task.dto.TaskDto;
import com.binarybrain.task.dto.UserDto;
import com.binarybrain.task.model.Task;
import com.binarybrain.task.model.TaskStatus;
import com.binarybrain.task.repository.TaskRepository;
import com.binarybrain.task.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private UserDto teacher;
    private UserDto otherTeacher;
    private UserDto student;
    private Task task;
    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        teacher = createUserDto(1L, "teacher", "TEACHER");
        otherTeacher = createUserDto(2L, "otherTeacher", "TEACHER");
        student = createUserDto(3L, "student", "STUDENT");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TaskStatus.OPEN);
        task.setTeacherId(1L);
        task.setCreatedAt(LocalDateTime.now());
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setAttachmentUrl("http://example.com/file.pdf");

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test Task");
        taskDto.setDescription("Test Description");
        taskDto.setStatus(TaskStatus.OPEN);
        taskDto.setTeacherId(1L);
        taskDto.setDeadline(LocalDateTime.now().plusDays(1));
        taskDto.setAttachmentUrl("http://example.com/file.pdf");
    }

    private UserDto createUserDto(Long id, String username, String role) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername(username);

        RoleDto roleDto = new RoleDto();
        roleDto.setName(role);
        userDto.setRoles(new HashSet<>(Collections.singletonList(roleDto)));

        return userDto;
    }

    @Test
    void createTask_WhenUserIsTeacher_ShouldCreateTask() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto result = taskService.createTask(taskDto, "teacher");

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals(TaskStatus.OPEN, result.getStatus());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_WhenUserIsStudent_ShouldThrowException() {
        when(userService.getUserProfile("student")).thenReturn(student);

        assertThrows(UserHasNotPermissionException.class,
                () -> taskService.createTask(taskDto, "student"));
    }

    @Test
    void createTask_WhenDeadlineIsPast_ShouldThrowException() {
        taskDto.setDeadline(LocalDateTime.now().minusDays(1));

        assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(taskDto, "teacher"));
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDto result = taskService.getTaskById(1L, "teacher");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTaskById_WhenTaskNotFound_ShouldThrowException() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.getTaskById(1L, "teacher"));
    }

    @Test
    void getAllTask_WithoutStatus_ShouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskDto> result = taskService.getAllTask(null, "teacher");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllTask_WithStatus_ShouldReturnFilteredTasks() {
        when(taskRepository.findByStatus(TaskStatus.OPEN)).thenReturn(List.of(task));

        List<TaskDto> result = taskService.getAllTask(TaskStatus.OPEN, "teacher");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(TaskStatus.OPEN, result.getFirst().getStatus());
    }

    @Test
    void getAllTaskByTeacherId_ShouldReturnTasks() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(taskRepository.findByTeacherId(1L)).thenReturn(List.of(task));

        List<TaskDto> result = taskService.getAllTaskByTeacherId(1L, "teacher");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getTeacherId());
    }

    @Test
    void getTasksByIds_ShouldReturnTasks() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(taskRepository.findByIdIn(List.of(1L))).thenReturn(List.of(task));

        List<TaskDto> result = taskService.getTasksbyIds(List.of(1L), "teacher");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void closeTask_WhenUserIsOwner_ShouldCloseTask() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto result = taskService.closeTask(1L, "teacher");

        assertNotNull(result);
        assertEquals(TaskStatus.CLOSED, result.getStatus());
    }

    @Test
    void closeTask_WhenUserIsNotOwner_ShouldThrowException() {
        when(userService.getUserProfile("otherTeacher")).thenReturn(otherTeacher);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(UserHasNotPermissionException.class,
                () -> taskService.closeTask(1L, "otherTeacher"));
    }

    @Test
    void updateTask_WhenUserIsOwner_ShouldUpdateAllFields() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto updatedDto = new TaskDto();
        updatedDto.setTitle("Updated Title");
        updatedDto.setDescription("Updated Description");
        updatedDto.setDeadline(LocalDateTime.now().plusDays(2));
        updatedDto.setStatus(TaskStatus.CLOSED);
        updatedDto.setAttachmentUrl("http://example.com/new.pdf");

        TaskDto result = taskService.updateTask(1L, updatedDto, "teacher");

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(TaskStatus.CLOSED, result.getStatus());
        assertEquals("http://example.com/new.pdf", result.getAttachmentUrl());
    }

    @Test
    void updateTask_WhenDeadlineIsPast_ShouldThrowException() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDto updatedDto = new TaskDto();
        updatedDto.setDeadline(LocalDateTime.now().minusDays(1));

        assertThrows(IllegalArgumentException.class,
                () -> taskService.updateTask(1L, updatedDto, "teacher"));
    }

    @Test
    void updateTask_WhenUserIsNotOwner_ShouldThrowException() {
        when(userService.getUserProfile("otherTeacher")).thenReturn(otherTeacher);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(UserHasNotPermissionException.class,
                () -> taskService.updateTask(1L, taskDto, "otherTeacher"));
    }

    @Test
    void deleteTaskById_WhenUserIsOwner_ShouldDeleteTask() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTaskById(1L, "teacher");

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTaskById_WhenUserIsNotOwner_ShouldThrowException() {
        when(userService.getUserProfile("otherTeacher")).thenReturn(otherTeacher);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(UserHasNotPermissionException.class,
                () -> taskService.deleteTaskById(1L, "otherTeacher"));
    }

    @Test
    void deleteTaskById_WhenTaskNotFound_ShouldThrowException() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTaskById(1L, "teacher"));
    }
}
