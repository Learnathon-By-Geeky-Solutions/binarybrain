package com.binaryBrain.classroom.service.impl;

import com.binaryBrain.classroom.dto.CourseDto;
import com.binaryBrain.classroom.dto.RoleDto;
import com.binaryBrain.classroom.dto.UserDto;
import com.binaryBrain.classroom.model.Classroom;
import com.binaryBrain.classroom.repo.ClassroomRepository;
import com.binaryBrain.classroom.service.ClassroomService;
import com.binaryBrain.classroom.service.CourseService;
import com.binaryBrain.classroom.service.UserService;
import com.binaryBrain.exception.AlreadyExistsException;
import com.binaryBrain.exception.ResourceNotFoundException;
import com.binaryBrain.exception.UserHasNotPermissionException;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ClassroomserviceImpl implements ClassroomService {
    private static final String admin = "ADMIN";
    private static final String teacher = "TEACHER";
    private final ClassroomRepository classroomRepository;
    private final UserService userService;
    private final CourseService courseService;

    public ClassroomserviceImpl(ClassroomRepository classroomRepository, UserService userService, CourseService courseService) {
        this.classroomRepository = classroomRepository;
        this.userService = userService;
        this.courseService = courseService;
    }
    boolean validateRole(UserDto userDto, List<String> targetRoles){
        return userDto.getRoles()
                .stream()
                .map(RoleDto::getName)
                .anyMatch(targetRoles::contains);
    }
    @Override
    public Classroom createClassroom(Classroom classroom, String username) {

        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, Arrays.asList(teacher, admin))){
            throw new UserHasNotPermissionException("Only teacher or admin can create classroom!");
        }
        Long teacherId = userDto.getId();
        classroom.setStartDate(LocalDate.from(LocalDateTime.now()));
        classroom.setTeacherId(teacherId);
        return classroomRepository.save(classroom);
    }

    @Override
    public Classroom getClassroomById(Long id, String username) {
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, Arrays.asList(teacher, admin))){
            throw new UserHasNotPermissionException("Only teacher or admin can manage classroom!");
        }
        return classroomRepository.findById(id).orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));
    }

    @Override
    public List<Classroom> getAllClassroomByTeacherId(Long id, String username) {
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, Arrays.asList(teacher, admin))){
            throw new UserHasNotPermissionException("Only teacher or admin can manage classroom!");
        }
        return classroomRepository.findByTeacherId(id);
    }

    @Override
    public void deleteClassroom(Long id, String username) {
        Classroom existingClassroom = getClassroomById(id, username);
        validateClassroomModificationPermission(existingClassroom, username);

        classroomRepository.deleteById(id);
    }

    @Override
    public Classroom addStudentInClassroom(Long classroomId, Long studentId, String username) {

        try {
            Classroom classroom = getClassroomById(classroomId, username);
            validateClassroomModificationPermission(classroom, username);
            UserDto student = userService.getUserProfileById(studentId, username);

            if (!validateRole(student, List.of("STUDENT"))) {
                throw new UserHasNotPermissionException("Only students can be added to the classroom!");
            }
            if (!classroom.getStudentIds().add(studentId)) {
                throw new AlreadyExistsException("Student is already in the classroom!");
            }
            return classroomRepository.save(classroom);

        }catch (FeignException.BadRequest e){
            throw new ResourceNotFoundException("User not found with id: " + studentId);
        }

    }

    @Override
    public Classroom removeStudentFromClassroomById(Long classroomId, Long studentId, String username) {
        try {
            Classroom classroom = getClassroomById(classroomId, username);
            validateClassroomModificationPermission(classroom, username);

            if (!classroom.getStudentIds().remove(studentId)) {
                throw new ResourceNotFoundException("Student not found in the classroom!");
            }
            return classroomRepository.save(classroom);

        }catch (FeignException.BadRequest e){
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
    }

    @Override
    public List<Classroom> getClassroomsByStudentId(Long studentId) {
        return classroomRepository.findByStudentIdsContaining(studentId);
    }

    @Override
    public Classroom addCourseToClassroom(Long classroomId, Long courseId, String username) {
        Classroom classroom = getClassroomById(classroomId, username);
        validateClassroomModificationPermission(classroom, username);

        CourseDto courseDto = courseService.getCourseById(courseId, username);
        if (classroom.getCourseIds().contains(courseId)) {
            throw new AlreadyExistsException("Course is already assigned to this classroom.");
        }
        classroom.getCourseIds().add(courseId);

        return classroomRepository.save(classroom);
    }

    @Override
    public List<CourseDto> getAllCourseInClassroom(Long classroomId, String username) {
        Classroom classroom = getClassroomById(classroomId, username);
        validateClassroomModificationPermission(classroom, username);

        List<Long> courseIds = new ArrayList<>(classroom.getCourseIds());

        return courseService.getCoursesByIds(courseIds, username);
    }

    private void validateClassroomModificationPermission(Classroom classroom, String jwt) {
        UserDto userDto = userService.getUserProfile(jwt);
        boolean isAdmin = validateRole(userDto, List.of(admin));
        boolean isTeacher = validateRole(userDto, List.of(teacher));

        if (!isAdmin && (!isTeacher || !classroom.getTeacherId().equals(userDto.getId()))) {
            throw new UserHasNotPermissionException("You do not have permission to modify this course.");
        }
    }
}