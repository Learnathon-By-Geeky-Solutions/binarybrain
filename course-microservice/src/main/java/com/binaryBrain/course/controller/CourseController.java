package com.binarybrain.course.controller;

import com.binarybrain.course.dto.CourseDto;
import com.binarybrain.course.dto.TaskDto;
import com.binarybrain.course.service.CourseService;
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
                                                  @RequestHeader("X-User-Username") String username) {
        CourseDto createdCourseDto = courseService.createCourse(courseDto, username);
        return new ResponseEntity<>(createdCourseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id,
                                                   @RequestHeader("X-User-Username") String username) {
        CourseDto courseDto = courseService.getCourseByCourseId(id, username);
        return new ResponseEntity<>(courseDto, HttpStatus.OK);
    }

    @GetMapping("/by-ids")
    public ResponseEntity<List<CourseDto>> getCoursesByIds(@RequestParam List<Long> courseIds,
                                                        @RequestHeader("X-User-Username") String username){
        List<CourseDto> courseDtoList = courseService.getCoursesbyIds(courseIds, username);
        return ResponseEntity.ok(courseDtoList);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<CourseDto>> getAllCoursesByAuthorId(@PathVariable Long authorId,
                                                                @RequestHeader("X-User-Username") String username) {
        List<CourseDto> courseDtoList = courseService.getAllCourseByAuthorId(authorId, username);
        return ResponseEntity.ok(courseDtoList);
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses(@RequestHeader("X-User-Username") String username) {
        List<CourseDto> courseDtoList = courseService.getAllCourse(username);
        return ResponseEntity.ok(courseDtoList);
    }

    @PutMapping("/{courseId}/add-task/{taskId}")
    public ResponseEntity<CourseDto> assignTaskInCourse(@PathVariable Long courseId,
                                                         @PathVariable Long taskId,
                                                         @RequestHeader("X-User-Username") String username){

        CourseDto courseDto = courseService.assignTaskInCourse(courseId, taskId, username);
        return new ResponseEntity<>(courseDto, HttpStatus.OK);
    }

    @PutMapping("/{courseId}/remove-task/{taskId}")
    public ResponseEntity<CourseDto> removeTaskFromCourse(@PathVariable Long courseId,
                                                     @PathVariable Long taskId,
                                                     @RequestHeader("X-User-Username") String username){

        CourseDto courseDto = courseService.removeTaskFromCourse(courseId, taskId, username);
        return new ResponseEntity<>(courseDto, HttpStatus.OK);
    }

    @GetMapping("/{courseId}/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasksFromCourse(@PathVariable Long courseId,
                                                                   @RequestHeader("X-User-Username") String username) {
        List<TaskDto> taskDtoList = courseService.getAllTaskFromCourse(courseId,username);
        return new ResponseEntity<>(taskDtoList, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long id,
                                                  @Valid @RequestBody CourseDto courseDto,
                                                  @RequestHeader("X-User-Username") String username) {
        CourseDto updatedCourseDto = courseService.updateCourse(id, courseDto, username);
        return ResponseEntity.ok(updatedCourseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id,
                                             @RequestHeader("X-User-Username") String username) {
        courseService.deleteCourse(id, username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}