package com.binaryBrain.course.service;

import com.binaryBrain.course.dto.UserDto;
import com.binaryBrain.course.model.Course;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;


public interface CourseService {

    Course createCourse(Course course, String username);
    Course getCourseByCourseId(Long id, String username);
    List<Course> getCoursesbyIds(List<Long> courseIds, String username);
    List<Course> getAllCourseByAuthorId(Long id, String username);

    List<Course> getAllCourse(String username);

    Course updateCourse(Long courseId, Course course, String username);

    void deleteCourse(Long courseId, String username);

}
