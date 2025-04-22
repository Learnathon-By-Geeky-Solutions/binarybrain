package com.binarybrain.classroom.controller;

import com.binarybrain.classroom.dto.CourseDto;
import com.binarybrain.classroom.model.Classroom;
import com.binarybrain.classroom.service.ClassroomService;
import com.binarybrain.exception.ErrorDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private/classroom")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @Operation(
            summary = "Create new Classroom",
            tags = {"01 - Creation"},
            description = "Only TEACHER and ADMIN can create Classroom.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Classroom created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Classroom.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to create classroom! User with role ADMIN, TEACHER can have permission to create Classroom.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom,
                                                     @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){


        Classroom createdClassroom = classroomService.createClassroom(classroom, username);

        return new ResponseEntity<>(createdClassroom, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Search Classroom by id",
            tags = {"02 - Search Classroom"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Classroom found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Classroom.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Classroom not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to search classroom! User with role ADMIN, TEACHER can have permission to search Classroom.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long id,
                                                      @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){

        Classroom classroom = classroomService.getClassroomById(id, username);

        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @Operation(
            summary = "Search all classroom  by Author id",
            tags = {"02 - Search Classroom"},
            description = "Returns a list of classrooms assigned to a specific teacher, filtered by teacher ID and username in the request header. One Teacher can't search for another teacher classroom!",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved classroom list",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Classroom.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to search classroom! User with role ADMIN, TEACHER can have permission to search Classroom.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Teacher not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/teacher/{id}")
    public ResponseEntity<List<Classroom>> getAllClassroomByTeacherId(@PathVariable Long id,
                                                                      @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){

        List<Classroom> classroomList = classroomService.getAllClassroomByTeacherId(id, username);

        return new ResponseEntity<>(classroomList, HttpStatus.OK);
    }

    @Operation(
            summary = "Search all classroom  by Student id",
            tags = {"02 - Search Classroom"},
            description = "Returns a list of classrooms in which the student is added.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved classroom list",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Classroom.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Student not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<Classroom>> getClassroomsByStudentId(@PathVariable Long studentId){
        List<Classroom> classroomList = classroomService.getClassroomsByStudentId(studentId);
        return new ResponseEntity<>(classroomList, HttpStatus.OK);
    }

    @Operation(
            summary = "Add student in a classroom",
            tags = {"03 - Manage students"},
            description = "Teacher can add student in their classroom with student id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully added student in classroom.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Classroom.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to add student in another teacher's classroom!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Classroom or Student not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "500", description = "Course or Student not exist with given id.")
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PutMapping("/{classroomId}/add-student/{studentId}")
    public ResponseEntity<Classroom> addStudentInClassroom(@PathVariable Long classroomId,
                                                           @PathVariable Long studentId,
                                                           @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){

        Classroom classroom = classroomService.addStudentInClassroom(classroomId, studentId, username);
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @Operation(
            summary = "Remove student from a classroom",
            tags = {"03 - Manage students"},
            description = "Teacher can remove student from their classroom with student id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully removed student from classroom.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Classroom.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to remove student from another teacher's classroom!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Classroom or Student not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @DeleteMapping("/{classroomId}/remove-student/{studentId}")
    ResponseEntity<Classroom> removeStudentFromClassroomById(@PathVariable Long classroomId,
                                                             @PathVariable Long studentId,
                                                             @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        Classroom classroom = classroomService.removeStudentFromClassroomById(classroomId, studentId, username);
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete classroom",
            tags = {"05 - Delete Classroom"},
            description = "Admin or corresponding Teacher can delete classroom.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted classroom.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Classroom.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to delete another teacher's classroom!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Classroom not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id,
                                                @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        classroomService.deleteClassroom(id, username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Assign course in a classroom",
            tags = {"04 - Manage courses"},
            description = "Admin or Teacher can assign course in their classroom with course id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully assigned course in classroom.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Classroom.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to assign course in another teacher's classroom!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Classroom or course not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PutMapping("/{classroomId}/add-course/{courseId}")
    public ResponseEntity<Classroom> addCourseToClassroom(@PathVariable Long classroomId,
                                                          @PathVariable Long courseId,
                                                          @Parameter(hidden = true) @RequestHeader("X-User-Username") String username) {
        Classroom classroom = classroomService.addCourseToClassroom(classroomId, courseId, username);
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @Operation(
            summary = "Remove course from a classroom",
            tags = {"04 - Manage courses"},
            description = "Teacher can removed course from their classroom with course id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully removed course from classroom.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Classroom.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to remove course from another teacher's classroom!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Classroom or Course not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @DeleteMapping("/{classroomId}/remove-course/{courseId}")
    ResponseEntity<Classroom> removeCourseFromClassroomById(@PathVariable Long classroomId,
                                                             @PathVariable Long courseId,
                                                            @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        Classroom classroom = classroomService.removeCourseFromClassroomById(classroomId, courseId, username);
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieve all classroom",
            tags = {"02 - Search Classroom"},
            description = "Only ADMIN can see all classroom list.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved classroom list",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Classroom.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "403", description = "You don't have permission to search classroom! Only ADMIN can do this.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/{classroomId}/courses")
    public ResponseEntity<List<CourseDto>> getAllCourseInClassroom(@PathVariable Long classroomId,
                                                                   @Parameter(hidden = true) @RequestHeader("X-User-Username") String username) {
        List<CourseDto> courseDtoList = classroomService.getAllCourseInClassroom(classroomId,username);
        return new ResponseEntity<>(courseDtoList, HttpStatus.OK);
    }

}