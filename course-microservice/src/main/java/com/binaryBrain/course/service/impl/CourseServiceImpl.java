package com.binaryBrain.course.service.impl;

import com.binaryBrain.course.dto.*;
import com.binaryBrain.course.model.Course;
import com.binaryBrain.course.repo.CourseRepository;
import com.binaryBrain.course.service.CourseService;
import com.binaryBrain.course.service.TaskService;
import com.binaryBrain.course.service.UserService;
import com.binaryBrain.exception.ResourceNotFoundException;
import com.binaryBrain.exception.UserHasNotPermissionException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
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
    public Course createCourse(Course course, String username) {
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, Arrays.asList("TEACHER", "ADMIN"))){
            throw new UserHasNotPermissionException("Only ADMIN & TEACHER can create course!");
        }
        Long teacherId = userDto.getId();
        course.setCreatedBy(teacherId);
        course.setStatus(CourseStatus.OPEN);
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseByCourseId(Long id,  String username) {
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, Arrays.asList("TEACHER", "ADMIN"))){
            throw new UserHasNotPermissionException("Only ADMIN & TEACHER can manage course!");
        }
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    @Override
    public List<Course> getCoursesbyIds(List<Long> courseIds, String username) {
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, Arrays.asList("TEACHER", "ADMIN"))){
            throw new UserHasNotPermissionException("Only ADMIN & TEACHER can manage course!");
        }
        return courseRepository.findByIdIn(courseIds);
    }

    @Override
    public List<Course> getAllCourseByAuthorId(Long id, String username) {
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, Arrays.asList("TEACHER", "ADMIN"))){
            throw new UserHasNotPermissionException("Only ADMIN & TEACHER can manage course!");
        }
        return courseRepository.findByCreatedBy(id);
    }

    @Override
    public List<Course> getAllCourse(String username) {
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, List.of("ADMIN", "TEACHER"))){
            throw new UserHasNotPermissionException("Only ADMIN can get all course list!");
        }
        return courseRepository.findAll();
    }

    @Override
    public Course updateCourse(Long courseId, Course updatedCourse, String username) {
        Course existingCourse = getCourseByCourseId(courseId, username);
        validateCourseModificationPermission(existingCourse, username);

        if (updatedCourse.getTitle() != null)
            existingCourse.setTitle(updatedCourse.getTitle());
        if (updatedCourse.getCode() != null)
            existingCourse.setCode(updatedCourse.getCode());
        if (updatedCourse.getDescription() != null)
            existingCourse.setDescription(updatedCourse.getDescription());
        if (updatedCourse.getStatus() != null)
            existingCourse.setStatus(updatedCourse.getStatus());

        return courseRepository.save(existingCourse);
    }

    @Override
    public Course assignTaskInCourse(Long courseId, Long taskId, String username) {
        Course course = getCourseByCourseId(courseId, username);
        validateCourseModificationPermission(course, username);

        TaskDto taskDto = taskService.getTaskById(taskId, username);
        if(taskDto.getStatus().equals(TaskStatus.CLOSED)){
            throw new UserHasNotPermissionException("CLOSED task can't be added! You should OPEN this first.");
        }
        if (course.getTaskIds().contains(taskId)) {
            throw new RuntimeException("Task is already assigned to this course.");
        }
        course.getTaskIds().add(taskId);

        return courseRepository.save(course);
    }

    @Override
    public Course removeTaskFromCourse(Long courseId, Long taskId, String username) {
        Course course = getCourseByCourseId(courseId, username);
        validateCourseModificationPermission(course, username);

        if (!course.getTaskIds().remove(taskId)) {
            throw new ResourceNotFoundException("Task not found in the course!");
        }
        return courseRepository.save(course);
    }

    @Override
    public List<TaskDto> getAllTaskFromCourse(Long courseId, String username) {
        Course course = getCourseByCourseId(courseId, username);
        validateCourseModificationPermission(course, username);

        List<Long> courseIds = new ArrayList<>(course.getTaskIds());

        return taskService.getTasksByIds(courseIds, username);
    }

    @Override
    public void deleteCourse(Long courseId, String username) {
        Course existingCourse = getCourseByCourseId(courseId, username);
        validateCourseModificationPermission(existingCourse, username);

        courseRepository.deleteById(courseId);
    }

    private void validateCourseModificationPermission(Course course, String username) {
        UserDto userDto = userService.getUserProfile(username);
        boolean isAdmin = validateRole(userDto, List.of("ADMIN"));
        boolean isTeacher = validateRole(userDto, List.of("TEACHER"));

        if (!isAdmin && (!isTeacher || !course.getCreatedBy().equals(userDto.getId()))) {
            throw new UserHasNotPermissionException("You do not have permission to modify this course.");
        }
    }
}