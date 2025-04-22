package com.binarybrain.course.service.impl;

import com.binarybrain.course.dto.*;
import com.binarybrain.course.mapper.CourseMapper;
import com.binarybrain.course.model.Course;
import com.binarybrain.course.repo.CourseRepository;
import com.binarybrain.course.service.*;
import com.binarybrain.exception.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    private boolean hasRole(UserDto userDto, List<String> roles) {
        return userDto.getRoles().stream()
                .map(RoleDto::getName)
                .anyMatch(roles::contains);
    }

    private void requireRole(UserDto userDto, List<String> requiredRoles, String errorMessage) {
        if (!hasRole(userDto, requiredRoles)) {
            throw new UserHasNotPermissionException(errorMessage);
        }
    }

    @Override
    public CourseDto createCourse(CourseDto courseDto, String username) {
        UserDto userDto = userService.getUserProfile(username);
        requireRole(userDto, Arrays.asList(TEACHER, ADMIN), "Only ADMIN & TEACHER can create course!");

        Course course = CourseMapper.mapToCourse(courseDto);
        course.setCreatedBy(userDto.getId());
        course.setStatus(CourseStatus.OPEN);
        courseRepository.save(course);

        return CourseMapper.mapToDto(course);
    }

    @Override
    public CourseDto getCourseByCourseId(Long id, String username) {
        return CourseMapper.mapToDto(getCourseById(id));
    }

    @Override
    public List<CourseDto> getCoursesbyIds(List<Long> courseIds, String username) {
        return courseRepository.findByIdIn(courseIds).stream()
                .map(CourseMapper::mapToDto)
                .toList();
    }

    @Override
    public List<CourseDto> getAllCourseByAuthorId(Long id, String username) {
        UserDto userDto = userService.getUserProfile(username);
        requireRole(userDto, Arrays.asList(TEACHER, ADMIN), "Only ADMIN & TEACHER can get corresponding courses list!");

        return courseRepository.findByCreatedBy(id).stream()
                .map(CourseMapper::mapToDto)
                .toList();
    }

    @Override
    public List<CourseDto> getAllCourse(String username) {
        UserDto userDto = userService.getUserProfile(username);
        requireRole(userDto, List.of(ADMIN), "Only ADMIN can get all course list!");

        return courseRepository.findAll().stream()
                .map(CourseMapper::mapToDto)
                .toList();
    }

    @Override
    public CourseDto updateCourse(Long courseId, CourseDto updatedCourseDto, String username) {
        Course existingCourse = getCourseById(courseId);
        validateCourseModificationPermission(existingCourse, username);

        updateCourseFields(existingCourse, updatedCourseDto);
        courseRepository.save(existingCourse);

        return CourseMapper.mapToDto(existingCourse);
    }

    private void updateCourseFields(Course course, CourseDto dto) {
        if (dto.getTitle() != null) course.setTitle(dto.getTitle());
        if (dto.getCode() != null) course.setCode(dto.getCode());
        if (dto.getDescription() != null) course.setDescription(dto.getDescription());
        if (dto.getStatus() != null) course.setStatus(dto.getStatus());
    }

    @Override
    public CourseDto assignTaskInCourse(Long courseId, Long taskId, String username) {
        Course course = getCourseById(courseId);
        validateCourseModificationPermission(course, username);

        TaskDto taskDto = taskService.getTaskById(taskId, username);
        if (TaskStatus.CLOSED.equals(taskDto.getStatus())) {
            throw new UserHasNotPermissionException("CLOSED task can't be added! You should OPEN this first.");
        }

        if (!course.getTaskIds().add(taskId)) {
            throw new AlreadyExistsException("Task is already assigned to this course.");
        }

        courseRepository.save(course);
        return CourseMapper.mapToDto(course);
    }

    @Override
    public CourseDto removeTaskFromCourse(Long courseId, Long taskId, String username) {
        Course course = getCourseById(courseId);
        validateCourseModificationPermission(course, username);

        if (!course.getTaskIds().remove(taskId)) {
            throw new ResourceNotFoundException("Task not found in the course!");
        }

        courseRepository.save(course);
        return CourseMapper.mapToDto(course);
    }

    @Override
    public List<TaskDto> getAllTaskFromCourse(Long courseId, String username) {
        Course course = getCourseById(courseId);
        validateCourseModificationPermission(course, username);

        return taskService.getTasksByIds(new ArrayList<>(course.getTaskIds()), username);
    }

    @Override
    public void deleteCourse(Long courseId, String username) {
        Course course = getCourseById(courseId);
        validateCourseModificationPermission(course, username);
        courseRepository.deleteById(courseId);
    }

    private void validateCourseModificationPermission(Course course, String username) {
        UserDto userDto = userService.getUserProfile(username);
        boolean isAdmin = hasRole(userDto, List.of(ADMIN));
        boolean isAuthor = hasRole(userDto, List.of(TEACHER)) && Objects.equals(course.getCreatedBy(), userDto.getId());

        if (!isAdmin && !isAuthor) {
            throw new UserHasNotPermissionException("You do not have permission to modify this course.");
        }
    }

    private Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }
}
