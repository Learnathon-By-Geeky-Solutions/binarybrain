package com.binaryBrain.classroom.controller;

import com.binaryBrain.classroom.dto.CourseDto;
import com.binaryBrain.classroom.model.Classroom;
import com.binaryBrain.classroom.service.ClassroomService;
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

    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom,
                                                     @RequestHeader("X-User-Username") String username){


        Classroom createdClassroom = classroomService.createClassroom(classroom, username);

        return new ResponseEntity<>(createdClassroom, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long id,
                                                      @RequestHeader("X-User-Username") String username){

        Classroom classroom = classroomService.getClassroomById(id, username);

        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @GetMapping("/teacher/{id}")
    public ResponseEntity<List<Classroom>> getAllClassroomByTeacherId(@PathVariable Long id,
                                                                      @RequestHeader("X-User-Username") String username){


        List<Classroom> classroomList = classroomService.getAllClassroomByTeacherId(id, username);

        return new ResponseEntity<>(classroomList, HttpStatus.OK);
    }

    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<Classroom>> getClassroomsByStudentId(@PathVariable Long studentId){
        List<Classroom> classroomList = classroomService.getClassroomsByStudentId(studentId);
        return new ResponseEntity<>(classroomList, HttpStatus.OK);
    }

    @PutMapping("/{classroomId}/add-student/{studentId}")
    public ResponseEntity<Classroom> addStudentInClassroom(@PathVariable Long classroomId,
                                                           @PathVariable Long studentId,
                                                           @RequestHeader("X-User-Username") String username){

        Classroom classroom = classroomService.addStudentInClassroom(classroomId, studentId, username);
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @DeleteMapping("/{classroomId}/remove-student/{studentId}")
    ResponseEntity<Classroom> removeStudentFromClassroomById(@PathVariable Long classroomId,
                                                             @PathVariable Long studentId,
                                                             @RequestHeader("X-User-Username") String username){
        Classroom classroom = classroomService.removeStudentFromClassroomById(classroomId, studentId, username);
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id,
                                                @RequestHeader("X-User-Username") String username){
        classroomService.deleteClassroom(id, username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{classroomId}/add-course/{courseId}")
    public ResponseEntity<Classroom> addCourseToClassroom(@PathVariable Long classroomId,
                                                       @PathVariable Long courseId,
                                                          @RequestHeader("X-User-Username") String username) {
        Classroom classroom = classroomService.addCourseToClassroom(classroomId, courseId, username);
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @GetMapping("/{classroomId}/courses")
    public ResponseEntity<List<CourseDto>> getAllCourseInClassroom(@PathVariable Long classroomId,
                                                                   @RequestHeader("X-User-Username") String username) {
        List<CourseDto> courseDtoList = classroomService.getAllCourseInClassroom(classroomId,username);
        return new ResponseEntity<>(courseDtoList, HttpStatus.OK);
    }

}