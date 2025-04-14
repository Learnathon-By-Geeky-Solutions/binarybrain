package com.binarybrain.classroom.service;

import com.binarybrain.classroom.dto.CourseDto;
import com.binarybrain.classroom.dto.RoleDto;
import com.binarybrain.classroom.dto.UserDto;
import com.binarybrain.classroom.model.Classroom;
import com.binarybrain.classroom.repo.ClassroomRepository;
import com.binarybrain.classroom.service.impl.ClassroomserviceImpl;
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
class ClassroomServiceImplTest {

    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private ClassroomserviceImpl classroomService;

    private UserDto teacher;
    private UserDto admin;
    private UserDto student;
    private Classroom classroom;
    private CourseDto course;

    @BeforeEach
    void setUp() {
        teacher = createUserDto(1L, "teacher", "TEACHER");
        admin = createUserDto(2L, "admin", "ADMIN");
        student = createUserDto(3L, "student", "STUDENT");

        classroom = new Classroom();
        classroom.setId(1L);
        classroom.setTitle("Test Classroom");
        classroom.setTeacherId(1L);
        classroom.setStudentIds(new HashSet<>());
        classroom.setCourseIds(new HashSet<>());

        course = new CourseDto();
        course.setId(1L);
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
    void createClassroom_WhenUserIsTeacher_ShouldCreateClassroom() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom);
        Classroom result = classroomService.createClassroom(classroom, "teacher");

        assertNotNull(result);
        assertEquals(1L, result.getTeacherId());
        assertNotNull(result.getStartDate());
        verify(classroomRepository).save(any(Classroom.class));
    }

    @Test
    void createClassroom_WhenUserIsAdmin_ShouldCreateClassroom() {
        when(userService.getUserProfile("admin")).thenReturn(admin);
        when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom);

        Classroom result = classroomService.createClassroom(classroom, "admin");

        assertNotNull(result);
        verify(classroomRepository).save(any(Classroom.class));
    }

    @Test
    void createClassroom_WhenUserIsStudent_ShouldThrowException() {
        when(userService.getUserProfile("student")).thenReturn(student);

        assertThrows(UserHasNotPermissionException.class, () -> classroomService.createClassroom(classroom, "student"));
    }

    @Test
    void getClassroomById_WhenUserHasPermission_ShouldReturnClassroom() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));

        Classroom result = classroomService.getClassroomById(1L, "teacher");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getClassroomById_WhenUserIsNotTeacherOrAdmin_ShouldThrowException() {
        when(userService.getUserProfile("student")).thenReturn(student);

        assertThrows(UserHasNotPermissionException.class, () -> classroomService.getClassroomById(3L, "student"));
    }

    @Test
    void getClassroomById_WhenClassroomNotFound_ShouldThrowException() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> classroomService.getClassroomById(1L, "teacher"));
    }

    @Test
    void getAllClassroomByTeacherId_ShouldReturnClassrooms() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findByTeacherId(1L)).thenReturn(Collections.singletonList(classroom));

        List<Classroom> result = classroomService.getAllClassroomByTeacherId(1L, "teacher");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllClassroomByTeacherId_WhenUserIsNotTeacherOrAdmin_ShouldThrowException() {
        when(userService.getUserProfile("student")).thenReturn(student);

        assertThrows(UserHasNotPermissionException.class, () -> classroomService.getAllClassroomByTeacherId(3L, "student"));
    }

    @Test
    void deleteClassroom_WhenUserIsTeacherOwner_ShouldDelete() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));

        classroomService.deleteClassroom(1L, "teacher");

        verify(classroomRepository).deleteById(1L);
    }

    @Test
    void deleteClassroom_WhenUserIsNotOwner_ShouldThrowException() {
        UserDto otherTeacher = createUserDto(99L, "otherTeacher", "TEACHER");
        when(userService.getUserProfile("otherTeacher")).thenReturn(otherTeacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));

        assertThrows(UserHasNotPermissionException.class, () -> classroomService.deleteClassroom(1L, "otherTeacher"));
    }

    @Test
    void addStudentInClassroom_ShouldAddStudent() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(userService.getUserProfileById(3L, "teacher")).thenReturn(student);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom);

        Classroom result = classroomService.addStudentInClassroom(1L, 3L, "teacher");

        assertTrue(result.getStudentIds().contains(3L));
    }

    @Test
    void addStudentInClassroom_WhenUserIsNotStudent_ShouldThrowException(){
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(userService.getUserProfileById(3L, "teacher")).thenReturn(teacher);

        UserHasNotPermissionException exception = assertThrows(
                UserHasNotPermissionException.class,
                () -> classroomService.addStudentInClassroom(1L, 3L, "teacher")
        );

        assertEquals("Only students can be added to the classroom!", exception.getMessage());

        verify(classroomRepository, never()).save(any());
    }

    @Test
    void addStudentInClassroom_WhenStudentAlreadyExists_ShouldThrowException() {
        classroom.getStudentIds().add(3L);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(userService.getUserProfileById(3L, "teacher")).thenReturn(student);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));

        assertThrows(AlreadyExistsException.class, () -> classroomService.addStudentInClassroom(1L, 3L, "teacher"));
    }

    @Test
    void removeStudentFromClassroomById_ShouldRemoveStudent() {
        classroom.getStudentIds().add(3L);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom);

        Classroom result = classroomService.removeStudentFromClassroomById(1L, 3L, "teacher");

        assertFalse(result.getStudentIds().contains(3L));
    }

    @Test
    void removeStudentFromClassroomById_WhenStudentNotInClassroom_ShouldThrowNotFoundException() {
        classroom.getStudentIds().add(99L);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> classroomService.removeStudentFromClassroomById(1L, 100L, "teacher")
        );

        assertEquals("Student not found in the classroom!", exception.getMessage());
        verify(classroomRepository, never()).save(any());
    }

    @Test
    void getClassroomsByStudentId_ShouldReturnClassrooms() {
        classroom.getStudentIds().add(3L);
        when(classroomRepository.findByStudentIdsContaining(3L)).thenReturn(Collections.singletonList(classroom));

        List<Classroom> result = classroomService.getClassroomsByStudentId(3L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void addCourseToClassroom_ShouldAddCourse() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(courseService.getCourseById(1L, "teacher")).thenReturn(course);
        when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom);

        Classroom result = classroomService.addCourseToClassroom(1L, 1L, "teacher");

        assertTrue(result.getCourseIds().contains(1L));
    }

    @Test
    void addCourseToClassroom_WhenCourseAlreadyExists_ShouldThrowException() {
        classroom.getCourseIds().add(33L);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(courseService.getCourseById(33L, "teacher")).thenReturn(course);

        AlreadyExistsException exception = assertThrows(
                AlreadyExistsException.class,
                () -> classroomService.addCourseToClassroom(1L, 33L, "teacher")
        );

        assertEquals("Course is already assigned to this classroom.", exception.getMessage());
        verify(classroomRepository, never()).save(any());
    }

    @Test
    void removeCourseFromClassroomById_ShouldRemoveCourse() {
        classroom.getCourseIds().add(3L);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom);

        Classroom result = classroomService.removeCourseFromClassroomById(1L, 3L, "teacher");

        assertFalse(result.getCourseIds().contains(3L));
    }

    @Test
    void removeCourseFromClassroomById_WhenCourseNotInClassroom_ShouldThrowNotFoundException() {
        classroom.getCourseIds().add(99L);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> classroomService.removeCourseFromClassroomById(1L, 100L, "teacher")
        );

        assertEquals("This Course is not added in this classroom!", exception.getMessage());
        verify(classroomRepository, never()).save(any());
    }

    @Test
    void getAllCourseInClassroom_ShouldReturnCourses() {
        classroom.getCourseIds().add(1L);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(courseService.getCoursesByIds(Collections.singletonList(1L), "teacher"))
                .thenReturn(Collections.singletonList(course));

        List<CourseDto> result = classroomService.getAllCourseInClassroom(1L, "teacher");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void validateRole_ShouldReturnTrueForMatchingRole() {
        when(userService.getUserProfile("teacher")).thenReturn(teacher);
        when(classroomRepository.findById(any())).thenReturn(Optional.of(new Classroom()));

        assertDoesNotThrow(() -> {
            classroomService.getClassroomById(1L, "teacher");
        });
    }
}
