package com.binarybrain.course.controller;

import com.binarybrain.course.dto.*;
import com.binarybrain.course.model.Course;
import com.binarybrain.course.repo.CourseRepository;
import com.binarybrain.course.service.TaskService;
import com.binarybrain.course.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {
    private static int runIteration = 0;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    CourseRepository courseRepository;

    @MockitoBean
    TaskService taskService;


    private final List<Long> createdCourseIds = new LinkedList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper

    private Course getCourse(boolean withTask) {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Java Programming");
        course.setCode("JP-101");
        course.setDescription("Learn Java Programming");
        course.setCreatedBy(1L);
        if(withTask){
            course.setTaskIds(new HashSet<>(List.of(1L)));
        }
        return course;
    }

    private TaskDto getTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Task 1");
        taskDto.setDescription("Task 1 Description");
        taskDto.setStatus(TaskStatus.OPEN);
        return taskDto;
    }


    @Test
    void createCourse() throws Exception {
        runIteration++;
        String response = mockMvc.perform(
                        post("/api/v1/private/course")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Username", "moinul")
                                .content("""
                                        {
                                          "title": "Java Programming %d",
                                          "description": "Learn Java Programming",
                                          "code": "JP-101 %d",
                                          "authorId": 1
                                        }""".formatted(runIteration, runIteration))
                )
                .andExpect(status().isCreated())  // Ensure status is 201 Created
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Verify content type
                .andExpect(content().json("""
                                {"title":"Java Programming %d","code":"JP-101 %d","description":"Learn Java Programming","status":"OPEN","createdBy":1,"taskIds":[]}
                        """.formatted(runIteration, runIteration)))
                .andReturn().getResponse().getContentAsString();

        createdCourseIds.add(extractCourseId(response));
    }

    private Long extractCourseId(String response) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.path("id").asLong();
    }

    @Test
    void getCourseById() throws Exception {
        mockMvc.perform(
                get("/api/v1/private/course/" + getCourse(true).getId())
                        .header("X-User-Username", "moinul")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(getCourse(true).getId()))
                .andExpect(jsonPath("$.title").value(getCourse(true).getTitle()));
    }

    @Test
    void getCoursesByIds() throws Exception {
        createCourse();
        String courseId1 = String.valueOf(createdCourseIds.getFirst());
        createCourse();
        String courseId2 = String.valueOf(createdCourseIds.get(1));
        mockMvc.perform(
                get("/api/v1/private/course/by-ids?courseIds=%s,%s".formatted(courseId1, courseId2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Username", "moinul")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllCoursesByAuthorId() throws Exception {
        createCourse();
        Long id = userService.getUserProfile("moinul").getId();
        mockMvc.perform(
                        get("/api/v1/private/course/author/%d".formatted(id))
                                .header("X-User-Username", "moinul")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void getAllCourses() throws Exception {
        createCourse();
        mockMvc.perform(
                        get("/api/v1/private/course")
                                .header("X-User-Username", "moinul")
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void assignTaskInCourse() throws Exception {
        when(taskService.getTaskById(1L, "moinul")).thenReturn(getTask());
        when(courseRepository.findById(1L)).thenReturn(Optional.of(getCourse(false)));
        Long courseId = getCourse(true).getId();
        Long taskId = getTask().getId();
        mockMvc.perform(
                put("/api/v1/private/course/{courseId}/add-task/{taskId}", courseId, taskId)
                        .header("X-User-Username", "moinul")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.taskIds").isArray())
                .andExpect(jsonPath("$.taskIds[0]").value(taskId));
    }

    @Test
    void removeTaskFromCourse() throws Exception {
        Long courseId = getCourse(true).getId();
        Long taskId = getTask().getId();
        mockMvc.perform(
                put("/api/v1/private/course/{courseId}/remove-task/{taskId}", courseId, taskId)
                        .header("X-User-Username", "moinul")
        ).andExpect(status().isOk());
    }

    @Test
    void getAllTasksFromCourse() throws Exception {
        Long courseId = getCourse(true).getId();
        mockMvc.perform(
                        get("/api/v1/private/course/{courseId}/tasks", courseId)
                                .header("X-User-Username", "moinul")
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void updateCourse() throws Exception {
        Long courseId = 1L;
        mockMvc.perform(
                        put("/api/v1/private/course/{id}", courseId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Username", "moinul")
                                .content("""
                                        {
                                          "title": "Updated Course",
                                          "description": "Updated Description",
                                          "code": "JP-102",
                                          "authorId": 1
                                        }
                                        """)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteCourse() throws Exception {
        Long courseId = 1L;
        createdCourseIds.remove(courseId);
        mockMvc.perform(
                delete("/api/v1/private/course/{id}", courseId)
                        .header("X-User-Username", "moinul")
        ).andExpect(status().isNoContent());

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/api/v1/private/course/{id}", courseId)
                        .header("X-User-Username", "moinul")
        ).andExpect(status().isNotFound());
    }

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("Moinul");
        userDto.setLastName("Islam");
        userDto.setUsername("moinul");
        userDto.setEmail("moinul@gmail.com");
        RoleDto roleDto = new RoleDto();
        roleDto.setName("ADMIN");
        roleDto.setId(1L);
        userDto.setRoles(new HashSet<>(List.of(roleDto)));

        Course courseDto1 = new Course();
        courseDto1.setId(1L);
        courseDto1.setTitle("Java Programming");
        courseDto1.setCode("JP-101");
        courseDto1.setDescription("Learn Java Programming");
        courseDto1.setCreatedBy(1L);
        courseDto1.setStatus(CourseStatus.OPEN);
        courseDto1.setTaskIds(new HashSet<>(List.of(1L)));

        Course courseDto2 = new Course();
        courseDto2.setId(2L);
        courseDto2.setTitle("Python Programming");
        courseDto2.setCode("PP-101");
        courseDto2.setDescription("Learn Python Programming");
        courseDto2.setCreatedBy(1L);
        courseDto2.setStatus(CourseStatus.OPEN);
        courseDto2.setTaskIds(new HashSet<>(List.of(2L)));
        when(userService.getUserProfile("moinul")).thenReturn(userDto);
        when(userService.getUserProfileById(1L,"moinul")).thenReturn(userDto);
        when(courseRepository.save(any(Course.class))).thenReturn(getCourse(true));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(getCourse(true)));
        when(courseRepository.findByIdIn(any())).thenReturn(List.of(courseDto1,courseDto2));
        when(courseRepository.findByCreatedBy(1L)).thenReturn(List.of(getCourse(true)));
        when(courseRepository.findAll()).thenReturn(List.of(getCourse(true)));
        when(taskService.getTasksByIds(List.of(1L),"moinul")).thenReturn(List.of(getTask()));
        doNothing().when(courseRepository).deleteById(1L);
    }
}