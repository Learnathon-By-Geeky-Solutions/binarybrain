package com.binaryBrain.classroom_microservice.service.impl;

import com.binaryBrain.classroom_microservice.dto.RoleDto;
import com.binaryBrain.classroom_microservice.dto.UserDto;
import com.binaryBrain.classroom_microservice.exception.ResourseNotFoundException;
import com.binaryBrain.classroom_microservice.exception.UserHasNotPermissionException;
import com.binaryBrain.classroom_microservice.model.Classroom;
import com.binaryBrain.classroom_microservice.repo.ClassroomRepository;
import com.binaryBrain.classroom_microservice.service.ClassroomService;
import com.binaryBrain.classroom_microservice.service.UserService;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
@Service
public class ClassroomserviceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final UserService userService;

    public ClassroomserviceImpl(ClassroomRepository classroomRepository, UserService userService) {
        this.classroomRepository = classroomRepository;
        this.userService = userService;
    }
    boolean validateRole(UserDto userDto, List<String> targetRoles){
        return userDto.getRoles()
                .stream()
                .map(RoleDto::getName)
                .anyMatch(targetRoles::contains);
    }
    @Override
    public Classroom createClassroom(Classroom classroom, String jwt) {

        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, Arrays.asList("TEACHER", "ADMIN"))){
            throw new UserHasNotPermissionException("Only teacher or admin can create classroom!");
        }
        Long teacherId = userDto.getId();
        classroom.setStartDate(LocalDate.from(LocalDateTime.now()));
        classroom.setTeacherId(teacherId);
        return classroomRepository.save(classroom);
    }

    @Override
    public Classroom getClassroomById(Long id, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, Arrays.asList("TEACHER", "ADMIN"))){
            throw new UserHasNotPermissionException("Only teacher or admin can manage classroom!");
        }
        return classroomRepository.findById(id).orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));
    }

    @Override
    public List<Classroom> getAllClassroomByTeacherId(Long id, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        if (!validateRole(userDto, Arrays.asList("TEACHER", "ADMIN"))){
            throw new UserHasNotPermissionException("Only teacher or admin can manage classroom!");
        }
        return classroomRepository.findByTeacherId(id);
    }

    @Override
    public void deleteClassroom(Long id, String jwt) {
        Classroom existingClassroom = getClassroomById(id, jwt);
        validateClassroomModificationPermission(existingClassroom, jwt);

        classroomRepository.deleteById(id);
    }

    @Override
    public Classroom addStudentInClassroom(Long classroomId, Long studentId, String jwt) {

        try {
            Classroom classroom = getClassroomById(classroomId, jwt);
            validateClassroomModificationPermission(classroom, jwt);
            UserDto student = userService.getUserProfileById(studentId, jwt);

            if (!validateRole(student, List.of("STUDENT"))) {
                throw new UserHasNotPermissionException("Only students can be added to the classroom!");
            }
            if (!classroom.getStudentIds().add(studentId)) {
                throw new RuntimeException("Student is already in the classroom!");
            }
            return classroomRepository.save(classroom);

        }catch (FeignException.BadRequest e){
            throw new ResourseNotFoundException("User not found with id: " + studentId);
        }

    }

    @Override
    public Classroom removeStudentFromClassroomById(Long classroomId, Long studentId, String jwt) {
        try {
            Classroom classroom = getClassroomById(classroomId, jwt);
            validateClassroomModificationPermission(classroom, jwt);

            if (!classroom.getStudentIds().remove(studentId)) {
                throw new ResourseNotFoundException("Student not found in the classroom!");
            }
            return classroomRepository.save(classroom);

        }catch (FeignException.BadRequest e){
            throw new ResourseNotFoundException("Student not found with id: " + studentId);
        }
    }

    @Override
    public List<Classroom> getClassroomsByStudentId(Long studentId) {
        return classroomRepository.findByStudentIdsContaining(studentId);
    }

    private void validateClassroomModificationPermission(Classroom classroom, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        boolean isAdmin = validateRole(userDto, List.of("ADMIN"));
        boolean isTeacher = validateRole(userDto, List.of("TEACHER"));

        if (!isAdmin && (!isTeacher || !classroom.getTeacherId().equals(userDto.getId()))) {
            throw new UserHasNotPermissionException("You do not have permission to modify this course.");
        }
    }
}