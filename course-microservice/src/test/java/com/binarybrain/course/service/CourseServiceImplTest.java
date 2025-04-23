package com.binarybrain.course.service;

import com.binarybrain.course.dto.*;
import com.binarybrain.course.model.Course;
import com.binarybrain.course.repo.CourseRepository;
import com.binarybrain.course.service.impl.CourseServiceImpl;
import com.binarybrain.exception.AlreadyExistsException;
import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.exception.UserHasNotPermissionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private CourseServiceImpl courseService;

    private UserDto teacher;
    private UserDto admin;
    private UserDto student;
    private Course course;
    private CourseDto courseDto;
    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        teacher = createUserDto(1L, "teacher", "TEACHER");
        admin = createUserDto(2L, "admin", "ADMIN");
        student = createUserDto(3L, "student", "STUDENT");

        course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");
        course.setCode("CS101");
        course.setDescription("Test Description");
        course.setStatus(CourseStatus.OPEN);
        course.setCreatedBy(1L);
        course.setTaskIds(new HashSet<>());

        courseDto = new CourseDto();
        courseDto.setId(1L);
        courseDto.setTitle("Test Course");
        courseDto.setCode("CS101");
        courseDto.setDescription("Test Description");
        courseDto.setStatus(CourseStatus.OPEN);

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setStatus(TaskStatus.OPEN);
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
    void createCourse_WhenUserIsTeacher_ShouldCreateCourse() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDto result = courseService.createCourse(courseDto, "teacher");

        assertNotNull(result);
        assertEquals("Test Course", result.getTitle());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_WhenUserIsAdmin_ShouldCreateCourse() {
        when(userService.getUserProfile("admin")).thenReturn(admin);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDto result = courseService.createCourse(courseDto, "admin");

        assertNotNull(result);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_WhenUserIsStudent_ShouldThrowException() {
        when(userService.getUserProfile("student")).thenReturn(student);

        assertThrows(UserHasNotPermissionException.class,
                () -> courseService.createCourse(courseDto, "student"));
    }

    @Test
    void getCourseByCourseId_ShouldReturnCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseDto result = courseService.getCourseByCourseId(1L, "teacher");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCourseByCourseId_WhenCourseNotFound_ShouldThrowException() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> courseService.getCourseByCourseId(1L, "teacher"));
    }

    @Test
    void getCoursesByIds_ShouldReturnCourses() {
        when(courseRepository.findByIdIn(List.of(1L))).thenReturn(List.of(course));

        List<CourseDto> result = courseService.getCoursesbyIds(List.of(1L), "teacher");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllCourseByAuthorId_WhenUserIsTeacher_ShouldReturnCourses() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(courseRepository.findByCreatedBy(1L)).thenReturn(List.of(course));

        List<CourseDto> result = courseService.getAllCourseByAuthorId(1L, "teacher");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllCourseByAuthorId_WhenUserIsStudent_ShouldThrowException() {
        when(userService.getUserProfile("student")).thenReturn(student);

        assertThrows(UserHasNotPermissionException.class,
                () -> courseService.getAllCourseByAuthorId(1L, "student"));
    }

    @Test
    void getAllCourse_WhenUserIsAdmin_ShouldReturnAllCourses() {
        when(userService.getUserProfile("admin")).thenReturn(admin);
        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<CourseDto> result = courseService.getAllCourse("admin");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllCourse_WhenUserIsNotAdmin_ShouldThrowException() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);

        assertThrows(UserHasNotPermissionException.class,
                () -> courseService.getAllCourse("teacher"));
    }

    @Test
    void updateCourse_WhenUserIsOwner_ShouldUpdateCourse() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDto updatedDto = new CourseDto();
        updatedDto.setTitle("Updated Title");
        updatedDto.setCode("CS102");
        updatedDto.setDescription("Updated Description");
        updatedDto.setStatus(CourseStatus.CLOSED);
        CourseDto result = courseService.updateCourse(1L, updatedDto, "teacher");

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("CS102", result.getCode());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(CourseStatus.CLOSED, result.getStatus());
    }

    @Test
    void updateCourse_WhenUserIsNotOwner_ShouldThrowException() {
        UserDto otherTeacher = createUserDto(99L, "otherTeacher", "TEACHER");
        when(userService.getUserProfile("otherTeacher")).thenReturn(otherTeacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThrows(UserHasNotPermissionException.class,
                () -> courseService.updateCourse(1L, courseDto, "otherTeacher"));
    }

    @Test
    void assignTaskInCourse_ShouldAddTask() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskService.getTaskById(1L, "teacher")).thenReturn(taskDto);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDto result = courseService.assignTaskInCourse(1L, 1L, "teacher");

        assertTrue(result.getTaskIds().contains(1L));
    }

    @Test
    void assignTaskInCourse_WhenTaskIsClosed_ShouldThrowException() {
        taskDto.setStatus(TaskStatus.CLOSED);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskService.getTaskById(1L, "teacher")).thenReturn(taskDto);

        assertThrows(UserHasNotPermissionException.class,
                () -> courseService.assignTaskInCourse(1L, 1L, "teacher"));
    }

    @Test
    void assignTaskInCourse_WhenTaskAlreadyExists_ShouldThrowException() {
        course.getTaskIds().add(1L);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskService.getTaskById(1L, "teacher")).thenReturn(taskDto);

        assertThrows(AlreadyExistsException.class,
                () -> courseService.assignTaskInCourse(1L, 1L, "teacher"));
    }

    @Test
    void removeTaskFromCourse_ShouldRemoveTask() {
        course.getTaskIds().add(1L);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDto result = courseService.removeTaskFromCourse(1L, 1L, "teacher");

        assertFalse(result.getTaskIds().contains(1L));
    }

    @Test
    void removeTaskFromCourse_WhenTaskNotInCourse_ShouldThrowException() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThrows(ResourceNotFoundException.class,
                () -> courseService.removeTaskFromCourse(1L, 1L, "teacher"));
    }

    @Test
    void getAllTaskFromCourse_ShouldReturnTasks() {
        course.getTaskIds().add(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskService.getTasksByIds(List.of(1L), "teacher")).thenReturn(List.of(taskDto));

        List<TaskDto> result = courseService.getAllTaskFromCourse(1L, "teacher");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void deleteCourse_WhenUserIsOwner_ShouldDeleteCourse() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.deleteCourse(1L, "teacher");

        verify(courseRepository).deleteById(1L);
    }

    @Test
    void deleteCourse_WhenUserIsNotOwner_ShouldThrowException() {
        UserDto otherTeacher = createUserDto(99L, "otherTeacher", "TEACHER");
        when(userService.getUserProfile("otherTeacher")).thenReturn(otherTeacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThrows(UserHasNotPermissionException.class,
                () -> courseService.deleteCourse(1L, "otherTeacher"));
    }
}