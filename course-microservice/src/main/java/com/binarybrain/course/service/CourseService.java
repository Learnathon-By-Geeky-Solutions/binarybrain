package com.binarybrain.course.service;

import com.binarybrain.course.dto.CourseDto;
import com.binarybrain.course.dto.TaskDto;

import java.util.List;

public interface CourseService {

    CourseDto createCourse(CourseDto courseDto, String username);
    CourseDto getCourseByCourseId(Long id, String username);
    List<CourseDto> getCoursesbyIds(List<Long> courseIds, String username);
    List<CourseDto> getAllCourseByAuthorId(Long authorId, String username);
    List<CourseDto> getAllCourse(String username);
    CourseDto updateCourse(Long courseId, CourseDto courseDto, String username);
    CourseDto assignTaskInCourse(Long courseId, Long taskId, String username);
    CourseDto removeTaskFromCourse(Long courseId, Long taskId, String username);
    List<TaskDto> getAllTaskFromCourse(Long courseId, String username);
    void deleteCourse(Long courseId, String username);
}
