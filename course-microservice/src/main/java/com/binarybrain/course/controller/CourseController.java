package com.binarybrain.course.controller;

import com.binarybrain.course.dto.CourseDto;
import com.binarybrain.course.dto.TaskDto;
import com.binarybrain.course.model.Course;
import com.binarybrain.course.service.CourseService;
import com.binarybrain.exception.ErrorDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
            summary = "Create new Course",
            tags = {"01 - Creation"},
            description = "Only TEACHER and ADMIN can create Course.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Course created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to create course! User with role ADMIN, TEACHER can have permission to create Course.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto,
                                                  @Parameter(hidden = true) @RequestHeader("X-User-Username") String username) {
        CourseDto createdCourseDto = courseService.createCourse(courseDto, username);
        return new ResponseEntity<>(createdCourseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Search Course by id",
            tags = {"02 - Search Course"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Course retrieved successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Course not found with given id!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id,
                                                   @Parameter(hidden = true) @RequestHeader("X-User-Username") String username) {
        CourseDto courseDto = courseService.getCourseByCourseId(id, username);
        return new ResponseEntity<>(courseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Search Courses with list of courseId",
            tags = {"02 - Search Course"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/by-ids")
    public ResponseEntity<List<CourseDto>> getCoursesByIds(@RequestParam List<Long> courseIds,
                                                           @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        List<CourseDto> courseDtoList = courseService.getCoursesbyIds(courseIds, username);
        return ResponseEntity.ok(courseDtoList);
    }

    @Operation(
            summary = "Search all course  by Author id",
            tags = {"02 - Search Course"},
            description = "Returns a list of course assigned to a specific teacher, filtered by teacher ID and username in the request header. One Teacher can't search for another teacher course!",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved course list",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to search course! User with role ADMIN, TEACHER can have permission to search Courses.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Teacher not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<CourseDto>> getAllCoursesByAuthorId(@PathVariable Long authorId,
                                                                   @Parameter(hidden = true) @RequestHeader("X-User-Username") String username) {
        List<CourseDto> courseDtoList = courseService.getAllCourseByAuthorId(authorId, username);
        return ResponseEntity.ok(courseDtoList);
    }

    @Operation(
            summary = "Search all course",
            tags = {"02 - Search Course"},
            description = "Returns list of all courses. Only ADMIN have permission to do this.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved all courses.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "Only ADMIN have the permission to search all Courses.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses(@Parameter(hidden = true) @RequestHeader("X-User-Username") String username) {
        List<CourseDto> courseDtoList = courseService.getAllCourse(username);
        return ResponseEntity.ok(courseDtoList);
    }

    @Operation(
            summary = "Assign task in a course",
            tags = {"04 - Manage Course"},
            description = "Admin or Teacher can assign task in corresponding course.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully assigned task in course.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "Course is not OPEN or user don't have permission to assign task in another teacher's course!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Course or task not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PutMapping("/{courseId}/add-task/{taskId}")
    public ResponseEntity<CourseDto> assignTaskInCourse(@PathVariable Long courseId,
                                                        @PathVariable Long taskId,
                                                        @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){

        CourseDto courseDto = courseService.assignTaskInCourse(courseId, taskId, username);
        return new ResponseEntity<>(courseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Remove task from a course",
            tags = {"04 - Manage Course"},
            description = "Teacher can remove task from their courses with task id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully removed task from course.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to remove task from another teacher's course!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Task or Course not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PutMapping("/{courseId}/remove-task/{taskId}")
    public ResponseEntity<CourseDto> removeTaskFromCourse(@PathVariable Long courseId,
                                                          @PathVariable Long taskId,
                                                          @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){

        CourseDto courseDto = courseService.removeTaskFromCourse(courseId, taskId, username);
        return new ResponseEntity<>(courseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve all task from a course",
            tags = {"02 - Search Course"},
            description = "This will response the list of tasks which are assign in a course with request courseId.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved task list from course",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/{courseId}/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasksFromCourse(@PathVariable Long courseId,
                                                               @Parameter(hidden = true) @RequestHeader("X-User-Username") String username) {
        List<TaskDto> taskDtoList = courseService.getAllTaskFromCourse(courseId,username);
        return new ResponseEntity<>(taskDtoList, HttpStatus.OK);
    }

    @Operation(
            summary = "Modify a existing course",
            tags = {"04 - Manage Course"},
            description = "Admin or Course Teacher can upgrade course information e.g. Change courseStatus from OPEN to CLOSED or vice versa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Course modification successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Course.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to modify another teacher's course!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long id,
                                                  @Valid @RequestBody CourseDto courseDto,
                                                  @Parameter(hidden = true) @RequestHeader("X-User-Username") String username) {
        CourseDto updatedCourseDto = courseService.updateCourse(id, courseDto, username);
        return ResponseEntity.ok(updatedCourseDto);
    }

    @Operation(
            summary = "Delete course",
            tags = {"05 - Delete Course"},
            description = "Admin or corresponding Teacher can delete course.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted course."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to delete another teacher's course!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Course not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id,
                                             @Parameter(hidden = true) @RequestHeader("X-User-Username") String username) {
        courseService.deleteCourse(id, username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}