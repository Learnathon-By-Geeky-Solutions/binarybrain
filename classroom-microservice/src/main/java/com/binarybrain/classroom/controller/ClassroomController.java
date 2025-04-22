package com.binarybrain.classroom.controller;

import com.binarybrain.classroom.dto.CourseDto;
import com.binarybrain.classroom.model.Classroom;
import com.binarybrain.classroom.service.ClassroomService;
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

    // Helper method to get username from header and handle common exception
    private String getUsernameFromHeader(@RequestHeader("X-User-Username") String username) {
        return username.trim();
    }

    // Helper method to wrap in ResponseEntity
    private ResponseEntity<Classroom> classroomResponse(Classroom classroom) {
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    private ResponseEntity<List<Classroom>> classroomListResponse(List<Classroom> classrooms) {
        return new ResponseEntity<>(classrooms, HttpStatus.OK);
    }

    private ResponseEntity<List<CourseDto>> courseDtoListResponse(List<CourseDto> courseDtos) {
        return new ResponseEntity<>(courseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom,
                                                     @RequestHeader("X-User-Username") String username) {
        String user = getUsernameFromHeader(username);
        Classroom createdClassroom = classroomService.createClassroom(classroom, user);
        return new ResponseEntity<>(createdClassroom, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long id,
                                                      @RequestHeader("X-User-Username") String username) {
        String user = getUsernameFromHeader(username);
        Classroom classroom = classroomService.getClassroomById(id, user);
        return classroomResponse(classroom);
    }

    @GetMapping("/teacher/{id}")
    public ResponseEntity<List<Classroom>> getAllClassroomByTeacherId(@PathVariable Long id,
                                                                      @RequestHeader("X-User-Username") String username) {
        String user = getUsernameFromHeader(username);
        List<Classroom> classroomList = classroomService.getAllClassroomByTeacherId(id, user);
        return classroomListResponse(classroomList);
    }

    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<Classroom>> getClassroomsByStudentId(@PathVariable Long studentId) {
        List<Classroom> classroomList = classroomService.getClassroomsByStudentId(studentId);
        return classroomListResponse(classroomList);
    }

    @PutMapping("/{classroomId}/add-student/{studentId}")
    public ResponseEntity<Classroom> addStudentInClassroom(@PathVariable Long classroomId,
                                                           @PathVariable Long studentId,
                                                           @RequestHeader("X-User-Username") String username) {
        String user = getUsernameFromHeader(username);
        Classroom classroom = classroomService.addStudentInClassroom(classroomId, studentId, user);
        return classroomResponse(classroom);
    }

    @DeleteMapping("/{classroomId}/remove-student/{studentId}")
    public ResponseEntity<Classroom> removeStudentFromClassroomById(@PathVariable Long classroomId,
                                                                    @PathVariable Long studentId,
                                                                    @RequestHeader("X-User-Username") String username) {
        String user = getUsernameFromHeader(username);
        Classroom classroom = classroomService.removeStudentFromClassroomById(classroomId, studentId, user);
        return classroomResponse(classroom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id,
                                                @RequestHeader("X-User-Username") String username) {
        String user = getUsernameFromHeader(username);
        classroomService.deleteClassroom(id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{classroomId}/add-course/{courseId}")
    public ResponseEntity<Classroom> addCourseToClassroom(@PathVariable Long classroomId,
                                                          @PathVariable Long courseId,
                                                          @RequestHeader("X-User-Username") String username) {
        String user = getUsernameFromHeader(username);
        Classroom classroom = classroomService.addCourseToClassroom(classroomId, courseId, user);
        return classroomResponse(classroom);
    }

    @DeleteMapping("/{classroomId}/remove-course/{courseId}")
    public ResponseEntity<Classroom> removeCourseFromClassroomById(@PathVariable Long classroomId,
                                                                   @PathVariable Long courseId,
                                                                   @RequestHeader("X-User-Username") String username) {
        String user = getUsernameFromHeader(username);
        Classroom classroom = classroomService.removeCourseFromClassroomById(classroomId, courseId, user);
        return classroomResponse(classroom);
    }

    @GetMapping("/{classroomId}/courses")
    public ResponseEntity<List<CourseDto>> getAllCourseInClassroom(@PathVariable Long classroomId,
                                                                   @RequestHeader("X-User-Username") String username) {
        String user = getUsernameFromHeader(username);
        List<CourseDto> courseDtoList = classroomService.getAllCourseInClassroom(classroomId, user);
        return courseDtoListResponse(courseDtoList);
    }
}
