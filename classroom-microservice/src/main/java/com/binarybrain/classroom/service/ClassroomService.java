package com.binarybrain.classroom.service;

import com.binarybrain.classroom.dto.CourseDto;
import com.binarybrain.classroom.model.Classroom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassroomService {

    Classroom createClassroom(Classroom classroom, String username);

    Classroom getClassroomById(Long id, String username);
    List<Classroom> getAllClassroomByTeacherId(Long id, String username);
    void deleteClassroom(Long id, String username);
    Classroom addStudentInClassroom(Long classroomId, Long studentId, String username);
    Classroom removeStudentFromClassroomById(Long classroomId, Long studentId, String username);
    List<Classroom> getClassroomsByStudentId(Long studentId);
    Classroom addCourseToClassroom(Long classroomId, Long courseId, String username);
    List<CourseDto> getAllCourseInClassroom(Long classroomId, String username);
}
