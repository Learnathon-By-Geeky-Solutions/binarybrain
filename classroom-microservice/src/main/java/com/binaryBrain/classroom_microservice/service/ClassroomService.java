package com.binaryBrain.classroom_microservice.service;

import com.binaryBrain.classroom_microservice.model.Classroom;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface ClassroomService {

    Classroom createClassroom(Classroom classroom, String jwt);

    Classroom getClassroomById(Long id, String jwt);
    List<Classroom> getAllClassroomByTeacherId(Long id, String jwt);
    void deleteClassroom(Long id, String jwt);
    Classroom addStudentInClassroom(Long classroomId, Long studentId, String jwt);
    Classroom removeStudentFromClassroomById(Long classroomId, Long studentId, String jwt);
    List<Classroom> getClassroomsByStudentId(Long studentId);
    Classroom addCourseToClassroom(Long classroomId, Long courseId, String jwt);
    Set<Long> getAllCourseInClassroom(Long classroomId, String jwt);
}
