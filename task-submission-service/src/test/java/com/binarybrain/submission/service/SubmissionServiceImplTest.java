package com.binarybrain.submission.service;

import com.binarybrain.exception.*;
import com.binarybrain.submission.dto.*;
import com.binarybrain.submission.mapper.SubmissionMapper;
import com.binarybrain.submission.model.*;
import com.binarybrain.submission.repository.SubmissionRepo;
import com.binarybrain.submission.service.impl.SubmissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {

    @Mock
    private SubmissionRepo submissionRepo;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @Mock
    private FileHandlerService fileHandlerService;

    @InjectMocks
    private SubmissionServiceImpl submissionService;

    private UserDto student;
    private UserDto teacher;
    private TaskDto task;
    private Submission submission;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        student = createUserDto(1L, "student", "STUDENT");
        teacher = createUserDto(2L, "teacher", "TEACHER");

        task = new TaskDto();
        task.setStatus("OPEN");
        task.setDeadline(LocalDateTime.now().plusDays(1));

        SubmissionDto submissionDto = new SubmissionDto();
        submissionDto.setId(1L);
        submissionDto.setTaskId(1L);
        submissionDto.setStudentId(1L);
        submissionDto.setSubmittedBy("student");
        submissionDto.setFileUrl("test-file.pdf");
        submissionDto.setGithubLink("https://github.com/test");
        submissionDto.setSubmissionType(SubmissionType.IN_TIME);
        submissionDto.setSubmissionStatus(SubmissionStatus.PENDING);
        submissionDto.setSubmissionTime(LocalDateTime.now());

        submission = SubmissionMapper.toSubmission(submissionDto);

        file = mock(MultipartFile.class);
    }

    private UserDto createUserDto(Long id, String username, String role) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername(username);

        RoleDto roleDto = new RoleDto();
        roleDto.setName(role);
        userDto.setRoles(new HashSet<>(Collections.singletonList(roleDto)));

        return userDto;
    }

    @Test
    void submitTask_WhenValid_ShouldCreateSubmission()  {
        when(taskService.getTaskById(1L, "student")).thenReturn(task);
        when(userService.getUserProfile("student")).thenReturn(student);
        when(submissionRepo.findByTaskIdAndSubmittedBy(1L, "student")).thenReturn(Optional.empty());
        when(fileHandlerService.uploadFile(file)).thenReturn("uploaded-file.pdf");
        when(submissionRepo.save(any(Submission.class))).thenReturn(submission);

        SubmissionDto result = submissionService.submitTask(1L, file, "https://github.com/test", "student");

        assertNotNull(result);
        assertEquals(1L, result.getTaskId());
        assertEquals("student", result.getSubmittedBy());
        verify(submissionRepo).save(any(Submission.class));
    }

    @Test
    void submitTask_WhenTaskClosed_ShouldThrowException() {
        task.setStatus("CLOSED");
        when(taskService.getTaskById(1L, "student")).thenReturn(task);

        assertThrows(ResourceNotFoundException.class,
                () -> submissionService.submitTask(1L, file, "https://github.com/test", "student"));
    }

    @Test
    void submitTask_WhenUserNotStudent_ShouldThrowException() {
        when(taskService.getTaskById(1L, "teacher")).thenReturn(task);
        when(userService.getUserProfile("teacher")).thenReturn(teacher);

        assertThrows(UserHasNotPermissionException.class,
                () -> submissionService.submitTask(1L, file, "https://github.com/test", "teacher"));
    }

    @Test
    void submitTask_WhenAlreadySubmitted_ShouldThrowException() {
        when(taskService.getTaskById(1L, "student")).thenReturn(task);
        when(userService.getUserProfile("student")).thenReturn(student);
        when(submissionRepo.findByTaskIdAndSubmittedBy(1L, "student")).thenReturn(Optional.of(submission));

        assertThrows(AlreadyExistsException.class,
                () -> submissionService.submitTask(1L, file, "https://github.com/test", "student"));
    }

    @Test
    void submitTask_WhenLateSubmission_ShouldMarkAsLate() {
        task.setDeadline(LocalDateTime.now().minusDays(1));
        when(taskService.getTaskById(1L, "student")).thenReturn(task);
        when(userService.getUserProfile("student")).thenReturn(student);
        when(submissionRepo.findByTaskIdAndSubmittedBy(1L, "student")).thenReturn(Optional.empty());
        when(fileHandlerService.uploadFile(file)).thenReturn("uploaded-file.pdf");
        when(submissionRepo.save(any(Submission.class))).thenReturn(submission);

        SubmissionDto result = submissionService.submitTask(1L, file, "https://github.com/test", "student");

        assertEquals(SubmissionType.LATE, result.getSubmissionType());
    }

    @Test
    void getSubmissionById_ShouldReturnSubmission() {
        when(submissionRepo.findById(1L)).thenReturn(Optional.of(submission));

        SubmissionDto result = submissionService.getSubmissionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getSubmissionById_WhenNotFound_ShouldThrowException() {
        when(submissionRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> submissionService.getSubmissionById(1L));
    }

    @Test
    void getAllSubmissionFromTask_ShouldReturnSubmissions() {
        when(submissionRepo.findByTaskId(1L)).thenReturn(List.of(submission));

        List<SubmissionDto> result = submissionService.getAllSubmissionFromTask(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getSubmissionByTaskIdAndUsername_ShouldReturnSubmission() {
        when(submissionRepo.findByTaskIdAndSubmittedBy(1L, "student")).thenReturn(Optional.of(submission));

        SubmissionDto result = submissionService.getSubmissionByTaskIdAndUsername(1L, "student");

        assertNotNull(result);
        assertEquals("student", result.getSubmittedBy());
    }

    @Test
    void getSubmissionByTaskIdAndUsername_WhenNotFound_ShouldThrowException() {
        when(submissionRepo.findByTaskIdAndSubmittedBy(1L, "student")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> submissionService.getSubmissionByTaskIdAndUsername(1L, "student"));
    }

    // Accept/Reject Tests
    @Test
    void acceptOrRejectSubmission_WhenTeacher_ShouldUpdateStatus() {
        when(submissionRepo.findById(1L)).thenReturn(Optional.of(submission));
        when(userService.getUserProfile("teacher")).thenReturn(teacher);

        SubmissionDto result = submissionService.acceptOrRejectSubmission(1L, SubmissionStatus.ACCEPTED, "teacher");

        assertEquals(SubmissionStatus.ACCEPTED, result.getSubmissionStatus());
        verify(submissionRepo).save(any(Submission.class));
    }

    @Test
    void acceptOrRejectSubmission_WhenNotTeacher_ShouldThrowException() {
        when(submissionRepo.findById(1L)).thenReturn(Optional.of(submission));
        when(userService.getUserProfile("student")).thenReturn(student);

        assertThrows(UserHasNotPermissionException.class,
                () -> submissionService.acceptOrRejectSubmission(1L, SubmissionStatus.ACCEPTED, "student"));
    }

    @Test
    void updateSubmissionByTaskId_ShouldUpdateSubmission() {
        when(userService.getUserProfile("student")).thenReturn(student);
        when(submissionRepo.findByTaskIdAndSubmittedBy(1L, "student")).thenReturn(Optional.of(submission));
        when(fileHandlerService.uploadFile(file)).thenReturn("new-file.pdf");
        when(submissionRepo.save(any(Submission.class))).thenReturn(submission);

        SubmissionDto result = submissionService.updateSubmissionByTaskId(1L, file, "new-github-link", "student");

        assertEquals("new-file.pdf", result.getFileUrl());
        assertEquals("new-github-link", result.getGithubLink());
        verify(fileHandlerService).deleteFile("test-file.pdf");
    }

    @Test
    void deleteFileByTaskId_ShouldRemoveFile() {
        when(userService.getUserProfile("student")).thenReturn(student);
        when(submissionRepo.findByTaskIdAndSubmittedBy(1L, "student")).thenReturn(Optional.of(submission));
        when(submissionRepo.save(any(Submission.class))).thenReturn(submission);

        SubmissionDto result = submissionService.deleteFileByTaskId(1L, "student");

        assertNull(result.getFileUrl());
        verify(fileHandlerService).deleteFile("test-file.pdf");
    }
}
