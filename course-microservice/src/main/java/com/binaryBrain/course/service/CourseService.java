package com.binaryBrain.course.service;

import com.binaryBrain.course.model.Course;

import java.util.List;


public interface CourseService {

    Course createCourse(Course course, String jwt);
    Course getCourseByCourseId(Long id, String jwt);
    List<Course> getCoursesbyIds(List<Long> courseIds, String jwt);
    List<Course> getAllCourseByAuthorId(Long id, String jwt);

    List<Course> getAllCourse(String jwt);

    Course updateCourse(Long courseId, Course course, String jwt);

    void deleteCourse(Long courseId, String jwt);

}
