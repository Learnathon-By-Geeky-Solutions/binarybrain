package com.binarybrain.classroom.controller;

import com.binarybrain.classroom.dto.CourseDto;
import com.binarybrain.classroom.dto.RoleDto;
import com.binarybrain.classroom.dto.UserDto;
import com.binarybrain.classroom.model.Classroom;
import com.binarybrain.classroom.repo.ClassroomRepository;
import com.binarybrain.classroom.service.CourseService;
import com.binarybrain.classroom.service.UserService;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ClassroomControllerTest {

    private static final String TEACHER_USERNAME = "moinul";
    private static final Long TEACHER_ID = 1L;
    private static final Long STUDENT_ID = 2L;
    private static final Long NEW_STUDENT_ID = 3L;
    private static final Long CLASSROOM_ID_1 = 1L;
    private static final Long CLASSROOM_ID_2 = 2L;
    private static final Long CLASSROOM_ID_3 = 3L;
    private static final Long CLASSROOM_ID_4 = 4L;
    private static final Long COURSE_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ClassroomRepository classroomRepository;

    @MockitoBean
    private CourseService courseService;

    private UserDto teacher;
    private UserDto student;
    private UserDto newStudent;
    private Classroom classroom1;
    private Classroom classroom2;
    private Classroom classroom3;
    private Classroom classroom4;

    @BeforeEach
    void setUp() {
        initializeTestData();
        setupUserMocks();
        setupClassroomMocks();
        setupCourseMocks();
        resetMocks();
    }

    private void initializeTestData() {
        teacher = createUserDto(TEACHER_ID, "Moinul", "Islam", TEACHER_USERNAME, "moinul@gmail.com", "ADMIN", 1L);
        student = createUserDto(STUDENT_ID, "Student", "Student", "student", "std@std.com", "STUDENT", 2L);
        newStudent = createUserDto(NEW_STUDENT_ID, "Student 2", "Student 2", "to_be_student", "to_be_std@std.com", "STUDENT", 3L);

        classroom1 = createClassroom(CLASSROOM_ID_1, "Classroom 1", TEACHER_ID, Set.of(STUDENT_ID), null);
        classroom2 = createClassroom(CLASSROOM_ID_2, "Classroom 2", TEACHER_ID, Set.of(STUDENT_ID), null);
        classroom3 = createClassroom(CLASSROOM_ID_3, "Classroom 3", TEACHER_ID, Set.of(STUDENT_ID, NEW_STUDENT_ID), null);
        classroom4 = createClassroom(CLASSROOM_ID_4, "Classroom 4", TEACHER_ID, Set.of(STUDENT_ID, NEW_STUDENT_ID), Set.of(COURSE_ID));
    }

    private UserDto createUserDto(Long id, String firstName, String lastName, String username, String email, String roleName, Long roleId) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setUsername(username);
        userDto.setEmail(email);
        RoleDto roleDto = new RoleDto();
        roleDto.setName(roleName);
        roleDto.setId(roleId);
        userDto.setRoles(new HashSet<>(List.of(roleDto)));
        return userDto;
    }

    private Classroom createClassroom(Long id, String title, Long teacherId, Set<Long> studentIds, Set<Long> courseIds) {
        Classroom classroom = new Classroom();
        classroom.setId(id);
        classroom.setTitle(title);
        classroom.setDescription(title + " description");
        classroom.setTeacherId(teacherId);
        classroom.setStudentIds(studentIds != null ? new HashSet<>(studentIds) : new HashSet<>());
        classroom.setCourseIds(courseIds != null ? new HashSet<>(courseIds) : new HashSet<>());
        classroom.setResourceIds(new HashSet<>());
        return classroom;
    }

    private CourseDto createCourseDto(Long id) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(id);
        return courseDto;
    }

    private void setupUserMocks() {
        when(userService.getUserProfile(TEACHER_USERNAME)).thenReturn(teacher);
        when(userService.getUserProfileById(TEACHER_ID, TEACHER_USERNAME)).thenReturn(teacher);
        when(userService.getUserProfileById(STUDENT_ID, TEACHER_USERNAME)).thenReturn(student);
        when(userService.getUserProfileById(NEW_STUDENT_ID, TEACHER_USERNAME)).thenReturn(newStudent);
    }

    private void setupClassroomMocks() {
        when(classroomRepository.findById(CLASSROOM_ID_1)).thenReturn(Optional.of(classroom1));
        when(classroomRepository.findById(CLASSROOM_ID_2)).thenReturn(Optional.of(classroom2));
        when(classroomRepository.findById(CLASSROOM_ID_3)).thenReturn(Optional.of(classroom3));
        when(classroomRepository.findById(CLASSROOM_ID_4)).thenReturn(Optional.of(classroom4));
        when(classroomRepository.findByTeacherId(TEACHER_ID)).thenReturn(List.of(classroom1));
        when(classroomRepository.findByStudentIdsContaining(STUDENT_ID)).thenReturn(List.of(classroom1));
        when(classroomRepository.save(any(Classroom.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void setupCourseMocks() {
        CourseDto course = createCourseDto(COURSE_ID);
        when(courseService.getCourseById(COURSE_ID, TEACHER_USERNAME)).thenReturn(course);
        when(courseService.getCoursesByIds(List.of(COURSE_ID), TEACHER_USERNAME)).thenReturn(List.of(course));
    }

    private void resetMocks() {
        reset(userService, classroomRepository, courseService);
        setupUserMocks();
        setupClassroomMocks();
        setupCourseMocks();
    }

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void createClassroom() throws Exception {
        Classroom newClassroom = createClassroom(null, "Classroom 1", TEACHER_ID, null, null);
        when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom1);

        mockMvc.perform(post("/api/v1/private/classroom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Username", TEACHER_USERNAME)
                        .content(asJsonString(newClassroom)))
                .andExpect(status().isCreated());
    }

    @Test
    void getClassroomById() throws Exception {
        mockMvc.perform(get("/api/v1/private/classroom/" + CLASSROOM_ID_1)
                        .header("X-User-Username", TEACHER_USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(classroom1)));
    }

    @Test
    void getAllClassroomByTeacherId() throws Exception {
        mockMvc.perform(get("/api/v1/private/classroom/teacher/" + TEACHER_ID)
                        .header("X-User-Username", TEACHER_USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(List.of(classroom1))));
    }

    @Test
    void getClassroomsByStudentId() throws Exception {
        mockMvc.perform(get("/api/v1/private/classroom/by-student/" + STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(List.of(classroom1))));
    }

    @Test
    void addStudentInClassroom() throws Exception {
        Classroom updatedClassroom = createClassroom(CLASSROOM_ID_2, "Classroom 2", TEACHER_ID, Set.of(STUDENT_ID, NEW_STUDENT_ID), null);
        when(classroomRepository.save(classroom2)).thenReturn(updatedClassroom);

        mockMvc.perform(put("/api/v1/private/classroom/" + CLASSROOM_ID_2 + "/add-student/" + NEW_STUDENT_ID)
                        .header("X-User-Username", TEACHER_USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(updatedClassroom)));
    }

    @Test
    void removeStudentFromClassroomById() throws Exception {
        Classroom updatedClassroom = createClassroom(CLASSROOM_ID_3, "Classroom 3", TEACHER_ID, Set.of(STUDENT_ID), null);
        when(classroomRepository.save(classroom3)).thenReturn(updatedClassroom);

        mockMvc.perform(delete("/api/v1/private/classroom/" + CLASSROOM_ID_3 + "/remove-student/" + NEW_STUDENT_ID)
                        .header("X-User-Username", TEACHER_USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(updatedClassroom)));
    }

    @Test
    void deleteClassroom() throws Exception {
        mockMvc.perform(delete("/api/v1/private/classroom/" + CLASSROOM_ID_1)
                        .header("X-User-Username", TEACHER_USERNAME))
                .andExpect(status().isNoContent());
    }

    @Test
    void addCourseToClassroom() throws Exception {
        Classroom updatedClassroom = createClassroom(CLASSROOM_ID_3, "Classroom 3", TEACHER_ID, Set.of(STUDENT_ID, NEW_STUDENT_ID), Set.of(COURSE_ID));
        when(classroomRepository.save(classroom3)).thenReturn(updatedClassroom);

        mockMvc.perform(put("/api/v1/private/classroom/" + CLASSROOM_ID_3 + "/add-course/" + COURSE_ID)
                        .header("X-User-Username", TEACHER_USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(updatedClassroom)));
    }

    @Test
    void removeCourseFromClassroomById() throws Exception {
        Classroom updatedClassroom = createClassroom(CLASSROOM_ID_4, "Classroom 4", TEACHER_ID, Set.of(STUDENT_ID, NEW_STUDENT_ID), Set.of());
        when(classroomRepository.save(classroom4)).thenReturn(updatedClassroom);

        mockMvc.perform(delete("/api/v1/private/classroom/" + CLASSROOM_ID_4 + "/remove-course/" + COURSE_ID)
                        .header("X-User-Username", TEACHER_USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(updatedClassroom)));
    }

    @Test
    void getAllCourseInClassroom() throws Exception {
        Classroom classroomWithCourse = createClassroom(CLASSROOM_ID_3, "Classroom 3", TEACHER_ID, Set.of(STUDENT_ID, NEW_STUDENT_ID), Set.of(COURSE_ID));
        when(classroomRepository.findById(CLASSROOM_ID_3)).thenReturn(Optional.of(classroomWithCourse));

        CourseDto course = createCourseDto(COURSE_ID);
        when(courseService.getCoursesByIds(List.of(COURSE_ID), TEACHER_USERNAME)).thenReturn(List.of(course));

        mockMvc.perform(get("/api/v1/private/classroom/" + CLASSROOM_ID_3 + "/courses")
                        .header("X-User-Username", TEACHER_USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(List.of(course))));
    }
}