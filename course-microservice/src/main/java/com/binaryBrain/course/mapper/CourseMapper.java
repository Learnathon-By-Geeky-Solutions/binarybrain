package com.binaryBrain.course.mapper;

import com.binaryBrain.course.dto.CourseDto;
import com.binaryBrain.course.model.Course;

import java.util.HashSet;

public class CourseMapper {
    private CourseMapper()  {
        throw new RuntimeException("This is a Utility class and can't be instantiated!");
    }

    public static Course mapToCourse(CourseDto courseDto) {
        Course course = new Course();
        course.setId(courseDto.getId());
        course.setTitle(courseDto.getTitle());
        course.setCode(courseDto.getCode());
        course.setDescription(courseDto.getDescription());
        course.setStatus(courseDto.getStatus());
        course.setCreatedBy(courseDto.getCreatedBy());
        course.setTaskIds(courseDto.getTaskIds() != null ? courseDto.getTaskIds() : new HashSet<>());
        return course;
    }

    public static CourseDto mapToDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setTitle(course.getTitle());
        courseDto.setCode(course.getCode());
        courseDto.setDescription(course.getDescription());
        courseDto.setStatus(course.getStatus());
        courseDto.setCreatedBy(course.getCreatedBy());
        courseDto.setTaskIds(course.getTaskIds() != null ? course.getTaskIds() : new HashSet<>());
        return courseDto;
    }
}
