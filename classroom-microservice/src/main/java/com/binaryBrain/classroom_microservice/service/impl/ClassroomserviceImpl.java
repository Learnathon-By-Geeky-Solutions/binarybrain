package com.binaryBrain.classroom_microservice.service.impl;

import com.binaryBrain.classroom_microservice.dto.UserDto;
import com.binaryBrain.classroom_microservice.model.Classroom;
import com.binaryBrain.classroom_microservice.repo.ClassroomRepository;
import com.binaryBrain.classroom_microservice.service.ClassroomService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class ClassroomserviceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;

    public ClassroomserviceImpl(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    @Override
    public Classroom createClassroom(Classroom classroom, UserDto userDto) {
        Set<String> roles = userDto.getRoles();
        Long teacherId = userDto.getId();
        if( !roles.contains("TEACHER")){
            throw new RuntimeException("Only teacher can create classroom!");
        }
        classroom.setStartDate(LocalDate.from(LocalDateTime.now()));
        classroom.setTeacherId(teacherId);
        return classroomRepository.save(classroom);
    }
}
