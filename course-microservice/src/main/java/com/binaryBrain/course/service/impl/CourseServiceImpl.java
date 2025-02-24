package com.binaryBrain.course.service.impl;

import com.binaryBrain.course.dto.CourseStatus;
import com.binaryBrain.course.dto.RoleDto;
import com.binaryBrain.course.dto.UserDto;
import com.binaryBrain.course.exception.ResourseNotFoundException;
import com.binaryBrain.course.exception.UserHasNotPermissionException;
import com.binaryBrain.course.model.Course;
import com.binaryBrain.course.repo.CourseRepository;
import com.binaryBrain.course.service.CourseService;
import com.binaryBrain.course.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserService userService;

    public CourseServiceImpl(CourseRepository courseRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    private boolean validateRole(UserDto userDto, List<String> targetRoles) {
        return userDto.getRoles()
                .stream()
                .map(RoleDto::getName)
                .anyMatch(targetRoles::contains);
    }

    @Override
    public Course createCourse(Course course, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, Arrays.asList("TEACHER", "ADMIN"))){
            throw new UserHasNotPermissionException("Only ADMIN & TEACHER can create course!");
        }
        Long teacherId = userDto.getId();
        course.setCreatedBy(teacherId);
        course.setStatus(CourseStatus.OPEN);
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseByCourseId(Long id, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, Arrays.asList("TEACHER", "ADMIN"))){
            throw new UserHasNotPermissionException("Only ADMIN & TEACHER can manage course!");
        }
        return courseRepository.findById(id).orElseThrow(() -> new ResourseNotFoundException("Course not found with id: " + id));
    }

    @Override
    public List<Course> getAllCourseByAuthorId(Long id, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, Arrays.asList("TEACHER", "ADMIN"))){
            throw new UserHasNotPermissionException("Only ADMIN & TEACHER can manage course!");
        }
        return courseRepository.findByCreatedBy(id);
    }

    @Override
    public List<Course> getAllCourse(String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, List.of("ADMIN"))){
            throw new UserHasNotPermissionException("Only ADMIN can get all course list!");
        }
        return courseRepository.findAll();
    }

    @Override
    public Course updateCourse(Long courseId, Course updatedCourse, String jwt) {
        Course existingCourse = getCourseByCourseId(courseId, jwt);
        validateCourseModificationPermission(existingCourse, jwt);

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
    public void deleteCourse(Long courseId, String jwt) {
        Course existingCourse = getCourseByCourseId(courseId, jwt);
        validateCourseModificationPermission(existingCourse, jwt);

        courseRepository.deleteById(courseId);
    }

    private void validateCourseModificationPermission(Course course, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        boolean isAdmin = validateRole(userDto, List.of("ADMIN"));
        boolean isTeacher = validateRole(userDto, List.of("TEACHER"));

        if (!isAdmin && (!isTeacher || !course.getCreatedBy().equals(userDto.getId()))) {
            throw new UserHasNotPermissionException("You do not have permission to modify this course.");
        }
    }
}
