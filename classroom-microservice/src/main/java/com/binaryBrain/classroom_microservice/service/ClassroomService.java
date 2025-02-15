package com.binaryBrain.classroom_microservice.service;

import com.binaryBrain.classroom_microservice.dto.UserDto;
import com.binaryBrain.classroom_microservice.model.Classroom;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface ClassroomService {
    Classroom createClassroom(Classroom classroom, UserDto userDto);

}
