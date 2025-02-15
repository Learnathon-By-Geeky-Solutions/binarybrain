package com.binaryBrain.classroom_microservice.controller;

import com.binaryBrain.classroom_microservice.dto.UserDto;
import com.binaryBrain.classroom_microservice.model.Classroom;
import com.binaryBrain.classroom_microservice.service.ClassroomService;
import com.binaryBrain.classroom_microservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/private/classroom")
public class ClassroomController {

    private final ClassroomService classroomService;
    private final UserService userService;

    public ClassroomController(ClassroomService classroomService, UserService userService) {
        this.classroomService = classroomService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom,
                                                     @RequestHeader("Authorization") String jwt){
        UserDto userDto = userService.getUserProfile(jwt);

        Classroom createdClassroom = classroomService.createClassroom(classroom, userDto);

        return new ResponseEntity<>(createdClassroom, HttpStatus.CREATED);
    }
}
