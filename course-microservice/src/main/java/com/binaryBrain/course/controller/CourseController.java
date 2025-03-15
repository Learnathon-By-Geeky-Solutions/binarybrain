package com.binaryBrain.course.controller;

import com.binaryBrain.course.dto.CourseDto;
import com.binaryBrain.course.dto.TaskDto;
import com.binaryBrain.course.dto.UserDto;
import com.binaryBrain.course.mapper.CourseMapper;
import com.binaryBrain.course.model.Course;
import com.binaryBrain.course.service.CourseService;
import com.binaryBrain.course.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/private/course")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto,
                                                  @RequestHeader("X-User-Username") String username) {
        Course course = CourseMapper.mapToCourse(courseDto);
        Course createdCourse = courseService.createCourse(course, username);
        CourseDto createdCourseDto = CourseMapper.mapToDto(createdCourse);
        return new ResponseEntity<>(createdCourseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id,
                                                   @RequestHeader("X-User-Username") String username) {
        UserDto userDto = userService.getUserProfile(username);
        Course course = courseService.getCourseByCourseId(id, username);
        CourseDto courseDto = CourseMapper.mapToDto(course);
        return ResponseEntity.ok(courseDto);
    }

    @GetMapping("/by-ids")
    public ResponseEntity<List<Course>> getCoursesByIds(@RequestParam List<Long> courseIds,
                                                        @RequestHeader("X-User-Username") String username){
        List<Course> courseList = courseService.getCoursesbyIds(courseIds, username);
        return ResponseEntity.ok(courseList);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Course>> getAllCoursesByAuthorId(@PathVariable Long authorId,
                                                                @RequestHeader("X-User-Username") String username) {
        List<Course> courses = courseService.getAllCourseByAuthorId(authorId, username);
        return ResponseEntity.ok(courses);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(@RequestHeader("X-User-Username") String username) {
        List<Course> courses = courseService.getAllCourse(username);
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{courseId}/add-task/{taskId}")
    public ResponseEntity<Course> assignTaskInCourse(@PathVariable Long courseId,
                                                         @PathVariable Long taskId,
                                                         @RequestHeader("X-User-Username") String username){

        Course course = courseService.assignTaskInCourse(courseId, taskId, username);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    @PutMapping("/{courseId}/remove-task/{taskId}")
    public ResponseEntity<Course> removeTaskFromCourse(@PathVariable Long courseId,
                                                     @PathVariable Long taskId,
                                                     @RequestHeader("X-User-Username") String username){

        Course course = courseService.removeTaskFromCourse(courseId, taskId, username);
        return new ResponseEntity<>(course, HttpStatus.OK);
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
        Course course = CourseMapper.mapToCourse(courseDto);
        Course updatedCourse = courseService.updateCourse(id, course, username);
        CourseDto updatedCourseDto = CourseMapper.mapToDto(updatedCourse);
        return ResponseEntity.ok(updatedCourseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id,
                                             @RequestHeader("X-User-Username") String username) {
        courseService.deleteCourse(id, username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}