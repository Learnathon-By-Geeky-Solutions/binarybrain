package com.binarybrain.classroom.service.impl;

import com.binarybrain.classroom.dto.*;
import com.binarybrain.classroom.model.Classroom;
import com.binarybrain.classroom.repo.ClassroomRepository;
import com.binarybrain.classroom.service.*;
import com.binarybrain.exception.*;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ClassroomserviceImpl implements ClassroomService {
    private static final String ADMIN = "ADMIN";
    private static final String TEACHER = "TEACHER";
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
        if (!validateRole(userDto, Arrays.asList(TEACHER, ADMIN))){
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
        if (!validateRole(userDto, Arrays.asList(TEACHER, ADMIN))){
            throw new UserHasNotPermissionException("Only teacher or admin can manage classroom!");
        }
        return classroomRepository.findById(id).orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));
    }

    @Override
    public List<Classroom> getAllClassroomByTeacherId(Long id, String username) {
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, Arrays.asList(TEACHER, ADMIN))){
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
            if (classroom.getStudentIds().contains(studentId)) {
                throw new AlreadyExistsException("Student is already in the classroom!");
            }
            Set<Long> studentIds = new HashSet<>(Set.copyOf(classroom.getStudentIds()));
            studentIds.add(studentId);
            classroom.setStudentIds(studentIds);
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

            if (!classroom.getStudentIds().contains(studentId)) {
                throw new ResourceNotFoundException("Student not found in the classroom!");
            }
            Set<Long> studentIds = new HashSet<>(Set.copyOf(classroom.getStudentIds()));
            studentIds.remove(studentId);
            classroom.setStudentIds(studentIds);
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

        courseService.getCourseById(courseId, username);
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
        boolean isAdmin = validateRole(userDto, List.of(ADMIN));
        boolean isTeacher = validateRole(userDto, List.of(TEACHER));

        if (!isAdmin && (!isTeacher || !classroom.getTeacherId().equals(userDto.getId()))) {
            throw new UserHasNotPermissionException("You do not have permission to modify this course.");
        }
    }
}