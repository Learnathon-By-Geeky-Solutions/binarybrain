package com.binarybrain.task.controller;

import com.binarybrain.task.dto.RoleDto;
import com.binarybrain.task.dto.UserDto;
import com.binarybrain.task.model.Task;
import com.binarybrain.task.model.TaskStatus;
import com.binarybrain.task.repository.TaskRepository;
import com.binarybrain.task.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    private static final String USERNAME = "moinul";
    private static final String BASE_URL = "/api/v1/private/task";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TaskRepository taskRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        UserDto userDto = createUserDto(1L, "Moinul", "Islam", USERNAME, "moinul@gmail.com",
                List.of(createRoleDto(1L, "ADMIN"), createRoleDto(2L, "TEACHER")));
        when(userService.getUserProfile(USERNAME)).thenReturn(userDto);
    }

    @Test
    void createTask() throws Exception {
        Task task = createTask(1L, "Task 1", "Task 1 description", 1L, TaskStatus.OPEN);
        when(taskRepository.save(task)).thenReturn(task);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Username", USERNAME)
                        .content(toJsonString(task)))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedTaskJson(1L, "Task 1", "Task 1 description", 1L, "OPEN")));
    }

    @Test
    void getTaskById() throws Exception {
        Task task = createTask(1L, "Task 1", "Task 1 description", 1L, TaskStatus.OPEN);
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

        mockMvc.perform(get(BASE_URL + "/1")
                        .header("X-User-Username", USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedTaskJson(1L, "Task 1", "Task 1 description", 1L, "OPEN")));
    }

    @Test
    void getAllTasks() throws Exception {
        List<Task> tasks = List.of(
                createTask(1L, "Task 1", "Task 1 description", 1L, TaskStatus.OPEN),
                createTask(2L, "Task 2", "Task 2 description", 1L, TaskStatus.OPEN)
        );
        when(taskRepository.findAll()).thenReturn(tasks);

        mockMvc.perform(get(BASE_URL)
                        .header("X-User-Username", USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedTasksJson(tasks)));
    }

    @Test
    void getAllTasksByTeacherId() throws Exception {
        List<Task> tasks = List.of(
                createTask(1L, "Task 1", "Task 1 description", 1L, TaskStatus.OPEN),
                createTask(2L, "Task 2", "Task 2 description", 1L, TaskStatus.OPEN)
        );
        when(taskRepository.findByTeacherId(1L)).thenReturn(tasks);

        mockMvc.perform(get(BASE_URL + "/teacher/1")
                        .header("X-User-Username", USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedTasksJson(tasks)));
    }

    @Test
    void getTasksByIds() throws Exception {
        List<Task> tasks = List.of(
                createTask(1L, "Task 1", "Task 1 description", 1L, TaskStatus.OPEN),
                createTask(2L, "Task 2", "Task 2 description", 1L, TaskStatus.OPEN)
        );
        when(taskRepository.findByIdIn(List.of(1L, 2L))).thenReturn(tasks);

        mockMvc.perform(get(BASE_URL + "/by-ids")
                        .header("X-User-Username", USERNAME)
                        .param("taskIds", "1,2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedTasksJson(tasks)));
    }

    @Test
    void closeTask() throws Exception {
        Task task = createTask(1L, "Task 1", "Task 1 description", 1L, TaskStatus.OPEN);
        Task closedTask = createTask(1L, "Task 1", "Task 1 description", 1L, TaskStatus.CLOSED);
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));
        when(taskRepository.save(task)).thenReturn(closedTask);

        mockMvc.perform(put(BASE_URL + "/close/1")
                        .header("X-User-Username", USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedTaskJson(1L, "Task 1", "Task 1 description", 1L, "CLOSED")));
    }

    @Test
    void updateTask() throws Exception {
        Task task = createTask(1L, "Task 1", "Task 1 description", 1L, TaskStatus.OPEN);
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        mockMvc.perform(put(BASE_URL + "/1")
                        .header("X-User-Username", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(task)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedTaskJson(1L, "Task 1", "Task 1 description", 1L, "OPEN")));
    }

    @Test
    void deleteTaskById() throws Exception {
        Task task = createTask(1L, "Task 1", "Task 1 description", 1L, TaskStatus.OPEN);
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

        mockMvc.perform(delete(BASE_URL + "/1")
                        .header("X-User-Username", USERNAME))
                .andExpect(status().isNoContent());
    }

    private UserDto createUserDto(Long id, String firstName, String lastName, String username, String email, List<RoleDto> roles) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setRoles(new HashSet<>(roles));
        return userDto;
    }

    private RoleDto createRoleDto(Long id, String name) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(id);
        roleDto.setName(name);
        return roleDto;
    }

    private Task createTask(Long id, String title, String description, Long teacherId, TaskStatus status) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setTeacherId(teacherId);
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setCreatedAt(LocalDateTime.now());
        task.setStatus(status);
        return task;
    }

    private String toJsonString(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    private String expectedTaskJson(Long id, String title, String description, Long teacherId, String status) {
        return String.format("""
                {
                    "id": %d,
                    "title": "%s",
                    "description": "%s",
                    "teacherId": %d,
                    "status": "%s"
                }
                """, id, title, description, teacherId, status);
    }

    private String expectedTasksJson(List<Task> tasks) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            json.append(expectedTaskJson(task.getId(), task.getTitle(), task.getDescription(), task.getTeacherId(), task.getStatus().name()));
            if (i < tasks.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}