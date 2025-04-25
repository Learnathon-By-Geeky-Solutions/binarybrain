package com.binarybrain.course.service.impl;

import com.binarybrain.course.dto.*;
import com.binarybrain.course.mapper.CourseMapper;
import com.binarybrain.course.model.Course;
import com.binarybrain.course.repo.CourseRepository;
import com.binarybrain.course.service.*;
import com.binarybrain.exception.*;
import com.binarybrain.exception.global.GlobalExceptionHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    private static final String ADMIN = "ADMIN";
    private static final String TEACHER = "TEACHER";
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final TaskService taskService;

    public CourseServiceImpl(CourseRepository courseRepository, UserService userService, TaskService taskService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.taskService = taskService;
    }

    private boolean validateRole(UserDto userDto, List<String> targetRoles) {
        return userDto.getRoles()
                .stream()
                .map(RoleDto::getName)
                .anyMatch(targetRoles::contains);
    }

    @Override
    public CourseDto createCourse(CourseDto courseDto, String username) {
        UserDto userDto = userService.getUserProfile(username);
        boolean isAdminOrTeacher = validateRole(userDto, List.of(ADMIN, TEACHER));
        GlobalExceptionHandler.Thrower.throwIf(
                !isAdminOrTeacher,
                new UserHasNotPermissionException("Only ADMIN & TEACHER can create course!"));

        Long teacherId = userDto.getId();
        Course course = CourseMapper.mapToCourse(courseDto);
        course.setCreatedBy(teacherId);
        course.setStatus(CourseStatus.OPEN);
        courseRepository.save(course);
        return CourseMapper.mapToDto(course);
    }

    @Override
    public CourseDto getCourseByCourseId(Long id,  String username) {
        Course course = getCourseById(id);
        return CourseMapper.mapToDto(course);
    }

    @Override
    public List<CourseDto> getCoursesbyIds(List<Long> courseIds, String username) {
        List<Course> courseList = courseRepository.findByIdIn(courseIds);
        return courseList.stream()
                .map(CourseMapper::mapToDto)
                .toList();
    }

    @Override
    public List<CourseDto> getAllCourseByAuthorId(Long authorId, String username) {
        UserDto userDto = userService.getUserProfile(username);
        boolean isAdmin = validateRole(userDto, List.of(ADMIN));
        GlobalExceptionHandler.Thrower.throwIf(
                (!isAdmin && !userDto.getId().equals(authorId)),
                new UserHasNotPermissionException("Only ADMIN & TEACHER can get corresponding courses list!"));

        List<Course> courseList = courseRepository.findByCreatedBy(authorId);
        return courseList.stream()
                .map(CourseMapper::mapToDto)
                .toList();
    }

    @Override
    public List<CourseDto> getAllCourse(String username) {
        UserDto userDto = userService.getUserProfile(username);
        boolean isAdmin = validateRole(userDto, List.of(ADMIN));
        GlobalExceptionHandler.Thrower.throwIf(
                !isAdmin,
                new UserHasNotPermissionException("Only ADMIN can get all course list!"));

        List<Course> courseList = courseRepository.findAll();
        return courseList.stream()
                .map(CourseMapper::mapToDto)
                .toList();
    }

    @Override
    public CourseDto updateCourse(Long courseId, CourseDto updatedCourseDto, String username) {
        Course existingCourse = getCourseById(courseId);
        validateCourseModificationPermission(existingCourse, username);

        Optional.ofNullable(updatedCourseDto.getTitle()).ifPresent(existingCourse::setTitle);
        Optional.ofNullable(updatedCourseDto.getCode()).ifPresent(existingCourse::setCode);
        Optional.ofNullable(updatedCourseDto.getDescription()).ifPresent(existingCourse::setDescription);
        Optional.ofNullable(updatedCourseDto.getStatus()).ifPresent(existingCourse::setStatus);

        courseRepository.save(existingCourse);
        return CourseMapper.mapToDto(existingCourse);
    }

    @Override
    public CourseDto assignTaskInCourse(Long courseId, Long taskId, String username) {
        Course course = getCourseById(courseId);
        validateCourseModificationPermission(course, username);

        TaskDto taskDto = taskService.getTaskById(taskId, username);

        GlobalExceptionHandler.Thrower.throwIf(
                taskDto.getStatus().equals(TaskStatus.CLOSED),
                new UserHasNotPermissionException("CLOSED task can't be added! You should OPEN this first."));
        GlobalExceptionHandler.Thrower.throwIf(
                course.getTaskIds().contains(taskId),
                new AlreadyExistsException("Task is already assigned to this course."));

        course.getTaskIds().add(taskId);

        courseRepository.save(course);
        return CourseMapper.mapToDto(course);
    }

    @Override
    public CourseDto removeTaskFromCourse(Long courseId, Long taskId, String username) {
        Course course = getCourseById(courseId);
        validateCourseModificationPermission(course, username);

        GlobalExceptionHandler.Thrower.throwIf(
                !course.getTaskIds().remove(taskId),
                new ResourceNotFoundException("Task not found in the course!"));

        courseRepository.save(course);
        return CourseMapper.mapToDto(course);
    }

    @Override
    public List<TaskDto> getAllTaskFromCourse(Long courseId, String username) {
        Course course = getCourseById(courseId);

        List<Long> courseIds = new ArrayList<>(course.getTaskIds());
        return taskService.getTasksByIds(courseIds, username);
    }

    @Override
    public void deleteCourse(Long courseId, String username) {
        Course existingCourse = getCourseById(courseId);
        validateCourseModificationPermission(existingCourse, username);

        courseRepository.deleteById(courseId);
    }

    private void validateCourseModificationPermission(Course course, String username) {
        UserDto userDto = userService.getUserProfile(username);
        boolean isAdmin = validateRole(userDto, List.of(ADMIN));

        GlobalExceptionHandler.Thrower.throwIf((!isAdmin && !course.getCreatedBy().equals(userDto.getId())),new UserHasNotPermissionException("You do not have permission to modify this course."));
    }

    private Course getCourseById(Long id){
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }
}