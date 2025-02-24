package com.binaryBrain.course.controller;

import com.binaryBrain.course.dto.CourseDto;
import com.binaryBrain.course.mapper.CourseMapper;
import com.binaryBrain.course.model.Course;
import com.binaryBrain.course.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/private/course")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto,
                                                  @RequestHeader("Authorization") String jwt) {
        Course course = CourseMapper.mapToCourse(courseDto);
        Course createdCourse = courseService.createCourse(course, jwt);
        CourseDto createdCourseDto = CourseMapper.mapToDto(createdCourse);
        return new ResponseEntity<>(createdCourseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String jwt) {
        Course course = courseService.getCourseByCourseId(id, jwt);
        CourseDto courseDto = CourseMapper.mapToDto(course);
        return ResponseEntity.ok(courseDto);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Course>> getAllCoursesByAuthorId(@PathVariable Long authorId,
                                                                @RequestHeader("Authorization") String jwt) {
        List<Course> courses = courseService.getAllCourseByAuthorId(authorId, jwt);
        return ResponseEntity.ok(courses);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(@RequestHeader("Authorization") String jwt) {
        List<Course> courses = courseService.getAllCourse(jwt);
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long id,
                                                  @Valid @RequestBody CourseDto courseDto,
                                                  @RequestHeader("Authorization") String jwt) {
        Course course = CourseMapper.mapToCourse(courseDto);
        Course updatedCourse = courseService.updateCourse(id, course, jwt);
        CourseDto updatedCourseDto = CourseMapper.mapToDto(updatedCourse);
        return ResponseEntity.ok(updatedCourseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id,
                                             @RequestHeader("Authorization") String jwt) {
        courseService.deleteCourse(id, jwt);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
