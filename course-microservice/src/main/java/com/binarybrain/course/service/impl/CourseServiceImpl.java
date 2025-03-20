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
        if (!validateRole(userDto, Arrays.asList(TEACHER, ADMIN))){
            throw new UserHasNotPermissionException("Only ADMIN & TEACHER can create course!");
        }
        Long teacherId = userDto.getId();
        Course course = CourseMapper.mapToCourse(courseDto);
        course.setCreatedBy(teacherId);
        course.setStatus(CourseStatus.OPEN);
        courseRepository.save(course);
        return CourseMapper.mapToDto(course);
    }

    @Override
    public CourseDto getCourseByCourseId(Long id,  String username) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
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
    public List<CourseDto> getAllCourseByAuthorId(Long id, String username) {
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, Arrays.asList(TEACHER, ADMIN))){
            throw new UserHasNotPermissionException("Only ADMIN & TEACHER can get corresponding courses list!");
        }
        List<Course> courseList = courseRepository.findByCreatedBy(id);
        return courseList.stream()
                .map(CourseMapper::mapToDto)
                .toList();
    }

    @Override
    public List<CourseDto> getAllCourse(String username) {
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, List.of(ADMIN))){
            throw new UserHasNotPermissionException("Only ADMIN can get all course list!");
        }
        List<Course> courseList = courseRepository.findAll();
        return courseList.stream()
                .map(CourseMapper::mapToDto)
                .toList();
    }

    @Override
    public CourseDto updateCourse(Long courseId, CourseDto updatedCourseDto, String username) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        validateCourseModificationPermission(existingCourse, username);

        if (updatedCourseDto.getTitle() != null)
            existingCourse.setTitle(updatedCourseDto.getTitle());
        if (updatedCourseDto.getCode() != null)
            existingCourse.setCode(updatedCourseDto.getCode());
        if (updatedCourseDto.getDescription() != null)
            existingCourse.setDescription(updatedCourseDto.getDescription());
        if (updatedCourseDto.getStatus() != null)
            existingCourse.setStatus(updatedCourseDto.getStatus());

        courseRepository.save(existingCourse);
        return CourseMapper.mapToDto(existingCourse);
    }

    @Override
    public CourseDto assignTaskInCourse(Long courseId, Long taskId, String username) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        validateCourseModificationPermission(course, username);

        TaskDto taskDto = taskService.getTaskById(taskId, username);
        if(taskDto.getStatus().equals(TaskStatus.CLOSED)){
            throw new UserHasNotPermissionException("CLOSED task can't be added! You should OPEN this first.");
        }
        if (course.getTaskIds().contains(taskId)) {
            throw new AlreadyExistsException("Task is already assigned to this course.");
        }
        course.getTaskIds().add(taskId);

        courseRepository.save(course);
        return CourseMapper.mapToDto(course);
    }

    @Override
    public CourseDto removeTaskFromCourse(Long courseId, Long taskId, String username) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        validateCourseModificationPermission(course, username);

        if (!course.getTaskIds().remove(taskId)) {
            throw new ResourceNotFoundException("Task not found in the course!");
        }
        courseRepository.save(course);
        return CourseMapper.mapToDto(course);
    }

    @Override
    public List<TaskDto> getAllTaskFromCourse(Long courseId, String username) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        validateCourseModificationPermission(course, username);

        List<Long> courseIds = new ArrayList<>(course.getTaskIds());
        return taskService.getTasksByIds(courseIds, username);
    }

    @Override
    public void deleteCourse(Long courseId, String username) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        validateCourseModificationPermission(existingCourse, username);

        courseRepository.deleteById(courseId);
    }

    private void validateCourseModificationPermission(Course course, String username) {
        UserDto userDto = userService.getUserProfile(username);
        boolean isAdmin = validateRole(userDto, List.of(ADMIN));
        boolean isTeacher = validateRole(userDto, List.of(TEACHER));

        if (!isAdmin && (!isTeacher || !course.getCreatedBy().equals(userDto.getId()))) {
            throw new UserHasNotPermissionException("You do not have permission to modify this course.");
        }
    }
}