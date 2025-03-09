package com.binaryBrain.course.service;

import com.binaryBrain.course.dto.CourseDto;
import com.binaryBrain.course.dto.TaskDto;
import com.binaryBrain.course.model.Course;

import java.util.List;


public interface CourseService {

    Course createCourse(Course course, String username);
    Course getCourseByCourseId(Long id, String username);
    List<Course> getCoursesbyIds(List<Long> courseIds, String username);
    List<Course> getAllCourseByAuthorId(Long id, String username);
    List<Course> getAllCourse(String username);
    Course updateCourse(Long courseId, Course course, String username);
    Course assignTaskInCourse(Long courseId, Long taskId, String username);
    Course removeTaskFromCourse(Long courseId, Long taskId, String username);
    List<TaskDto> getAllTaskFromCourse(Long courseId, String username);
    void deleteCourse(Long courseId, String username);

}
