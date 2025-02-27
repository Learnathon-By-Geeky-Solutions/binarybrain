package com.binaryBrain.classroom_microservice.controller;

import com.binaryBrain.classroom_microservice.dto.CourseDto;
import com.binaryBrain.classroom_microservice.model.Classroom;
import com.binaryBrain.classroom_microservice.service.ClassroomService;
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
                                                     @RequestHeader("Authorization") String jwt){


        Classroom createdClassroom = classroomService.createClassroom(classroom, jwt);

        return new ResponseEntity<>(createdClassroom, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long id,
                                                      @RequestHeader("Authorization") String jwt){

        Classroom classroom = classroomService.getClassroomById(id, jwt);

        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @GetMapping("/teacher/{id}")
    public ResponseEntity<List<Classroom>> getAllClassroomByTeacherId(@PathVariable Long id,
                                                 @RequestHeader("Authorization") String jwt){


        List<Classroom> classroomList = classroomService.getAllClassroomByTeacherId(id, jwt);

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
                                                           @RequestHeader("Authorization") String jwt){

        Classroom classroom = classroomService.addStudentInClassroom(classroomId, studentId, jwt);
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @DeleteMapping("/{classroomId}/remove-student/{studentId}")
    ResponseEntity<Classroom> removeStudentFromClassroomById(@PathVariable Long classroomId,
                                                             @PathVariable Long studentId,
                                                             @RequestHeader("Authorization") String jwt){
        Classroom classroom = classroomService.removeStudentFromClassroomById(classroomId, studentId, jwt);
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id,
                                                @RequestHeader("Authorization") String jwt){
        classroomService.deleteClassroom(id, jwt);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{classroomId}/add-course/{courseId}")
    public ResponseEntity<Classroom> addCourseToClassroom(@PathVariable Long classroomId,
                                                       @PathVariable Long courseId,
                                                       @RequestHeader("Authorization") String jwt) {
        Classroom classroom = classroomService.addCourseToClassroom(classroomId, courseId, jwt);
        return new ResponseEntity<>(classroom, HttpStatus.OK);
    }

    @GetMapping("/{classroomId}/courses")
    public ResponseEntity<List<CourseDto>> getAllCourseInClassroom(@PathVariable Long classroomId,
                                                                   @RequestHeader("Authorization") String jwt) {
        List<CourseDto> courseDtoList = classroomService.getAllCourseInClassroom(classroomId,jwt);
        return new ResponseEntity<>(courseDtoList, HttpStatus.OK);
    }

}