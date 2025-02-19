package com.binaryBrain.classroom_microservice.service.impl;

import com.binaryBrain.classroom_microservice.dto.RoleDto;
import com.binaryBrain.classroom_microservice.dto.UserDto;
import com.binaryBrain.classroom_microservice.model.Classroom;
import com.binaryBrain.classroom_microservice.repo.ClassroomRepository;
import com.binaryBrain.classroom_microservice.service.ClassroomService;
import com.binaryBrain.classroom_microservice.service.UserService;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
@Service
public class ClassroomserviceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final UserService userService;

    public ClassroomserviceImpl(ClassroomRepository classroomRepository, UserService userService) {
        this.classroomRepository = classroomRepository;
        this.userService = userService;
    }
    boolean validateRole(UserDto userDto, String targetRole){
        return userDto.getRoles()
                .stream()
                .map(RoleDto::getName)
                .anyMatch(role -> role.equals(targetRole));
    }
    @Override
    public Classroom createClassroom(Classroom classroom, String jwt) {

        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, "TEACHER")){
            throw new RuntimeException("Only teacher can create classroom!");
        }
        Long teacherId = userDto.getId();
        classroom.setStartDate(LocalDate.from(LocalDateTime.now()));
        classroom.setTeacherId(teacherId);
        return classroomRepository.save(classroom);
    }

    @Override
    public Classroom getClassroomById(Long id, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, "TEACHER")){
            throw new RuntimeException("Only teacher can manage classroom!");
        }
        return classroomRepository.findById(id).orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));
    }

    @Override
    public List<Classroom> getAllClassroomByTeacherId(Long id, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, "TEACHER")){
            throw new RuntimeException("Only teacher can manage classroom!");
        }
        return classroomRepository.findByTeacherId(id);
    }

    @Override
    public void deleteClassroom(Long id, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, "TEACHER")){
            throw new RuntimeException("Only teacher can delete classroom!");
        }
        Long loggedInTeacherId = userDto.getId();
        Classroom classroom = getClassroomById(id, jwt);
        Long classroomTeacherId = classroom.getTeacherId();

        if(!Objects.equals(loggedInTeacherId, classroomTeacherId)) {
            throw new RuntimeException("You can't delete another teacher's classroom");
        }
        classroomRepository.deleteById(id);
    }

    @Override
    public Classroom addStudentInClassroom(Long classroomId, Long studentId, String jwt) {

        try {
            UserDto userDto = userService.getUserProfile(jwt);
            if (!validateRole(userDto, "TEACHER")) {
                throw new RuntimeException("Only teacher can manage classroom!");
            }

            Classroom classroom = classroomRepository.findById(classroomId)
                    .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));

            UserDto student = userService.getUserProfileById(studentId, jwt);
            if (!validateRole(student, "STUDENT")) {
                throw new RuntimeException("Only students can be added to the classroom!");
            }

            Long loggedInTeacherId = userDto.getId();
            Long classroomTeacherId = classroom.getTeacherId();
            if(!Objects.equals(loggedInTeacherId, classroomTeacherId)) {
                throw new RuntimeException("You can't add student into another teacher's classroom!");
            }

            if (!classroom.getStudentIds().add(studentId)) {
                throw new RuntimeException("Student is already in the classroom!");
            }
            return classroomRepository.save(classroom);

        }catch (FeignException.BadRequest e){
            throw new RuntimeException("User not found with id: " + studentId);
        }

    }

    @Override
    public Classroom removeStudentFromClassroomById(Long classroomId, Long studentId, String jwt) {
        try {
            UserDto userDto = userService.getUserProfile(jwt);
            if (!validateRole(userDto, "TEACHER")) {
                throw new RuntimeException("Only teacher can manage classroom!");
            }

            Classroom classroom = classroomRepository.findById(classroomId)
                    .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));

            Long loggedInTeacherId = userDto.getId();
            Long classroomTeacherId = classroom.getTeacherId();
            if(!Objects.equals(loggedInTeacherId, classroomTeacherId)) {
                throw new RuntimeException("You can't remove a student from another teacher's classroom!");
            }

            if (!classroom.getStudentIds().remove(studentId)) {
                throw new RuntimeException("Student not found in the classroom!");
            }
            return classroomRepository.save(classroom);

        }catch (FeignException.BadRequest e){
            throw new RuntimeException("Student not found with id: " + studentId);
        }

    }

    @Override
    public List<Classroom> getClassroomsByStudentId(Long studentId) {
        return classroomRepository.findByStudentIdsContaining(studentId);
    }
}