package com.binarybrain.submission.service.impl;

import com.binarybrain.exception.*;
import com.binarybrain.exception.global.GlobalExceptionHandler;
import com.binarybrain.submission.dto.*;
import com.binarybrain.submission.mapper.SubmissionMapper;
import com.binarybrain.submission.model.*;
import com.binarybrain.submission.repository.SubmissionRepo;
import com.binarybrain.submission.service.FileHandlerService;
import com.binarybrain.submission.service.SubmissionService;
import com.binarybrain.submission.service.TaskService;
import com.binarybrain.submission.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepo submissionRepo;
    private final TaskService taskService;
    private final UserService userService;
    private final FileHandlerService fileHandlerService;

    public SubmissionServiceImpl(SubmissionRepo submissionRepo, TaskService taskService, UserService userService, FileHandlerService fileHandlerService) {
        this.submissionRepo = submissionRepo;
        this.taskService = taskService;
        this.userService = userService;
        this.fileHandlerService = fileHandlerService;
    }
    private boolean validateRole(UserDto userDto, List<String> targetRoles) {
        return userDto.getRoles()
                .stream()
                .map(RoleDto::getName)
                .anyMatch(targetRoles::contains);
    }
    @Override
    public SubmissionDto submitTask(Long taskId, MultipartFile file, String githubLink, String username) {
        TaskDto taskDto = taskService.getTaskById(taskId,username);
        GlobalExceptionHandler.Thrower.throwIf("CLOSED".equals(taskDto.getStatus()),new ResourceNotFoundException("Task is not OPEN for submission!"));
        UserDto userDto = userService.getUserProfile(username);
        boolean isStudent = validateRole(userDto, List.of("STUDENT"));
        GlobalExceptionHandler.Thrower.throwIf(
                !isStudent,
                new UserHasNotPermissionException("Only STUDENT can submit task!"));
        GlobalExceptionHandler.Thrower.throwIf(
                submissionRepo.findByTaskIdAndSubmittedBy(taskId, username).isPresent(),
                new AlreadyExistsException("You have already submitted this task. Multiple submissions are not allowed!"));

        String fileName = fileHandlerService.uploadFile(file);

        Submission submission = new Submission();
        submission.setTaskId(taskId);
        submission.setStudentId(userDto.getId());
        submission.setSubmittedBy(username);
        submission.setFileUrl(fileName);
        submission.setGithubLink(githubLink);

        if(taskDto.getDeadline().isAfter(LocalDateTime.now()))
            submission.setSubmissionType(SubmissionType.IN_TIME);
        else submission.setSubmissionType(SubmissionType.LATE);

        submission.setSubmissionStatus(SubmissionStatus.PENDING);
        submission.setSubmissionTime(LocalDateTime.now());

        submissionRepo.save(submission);
        return SubmissionMapper.toSubmissionDto(submission);
    }
    @Override
    public SubmissionDto getSubmissionById(Long submissionId) {
        Submission submission = submissionRepo.findById(submissionId).orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));
        return SubmissionMapper.toSubmissionDto(submission);
    }

    @Override
    public List<SubmissionDto> getAllSubmissionFromTask(Long taskId) {
        List<Submission> submissionList = submissionRepo.findByTaskId(taskId);
        return submissionList.stream()
                .map(SubmissionMapper::toSubmissionDto)
                .toList();
    }

    @Override
    public SubmissionDto getSubmissionByTaskIdAndUsername(Long taskId, String username) {
        Submission submission = submissionRepo.findByTaskIdAndSubmittedBy(taskId, username)
                .orElseThrow( ()-> new ResourceNotFoundException("No submission found for task id "+ taskId + "by student " + username));

        return SubmissionMapper.toSubmissionDto(submission);
    }
    @Override
    public SubmissionDto acceptOrRejectSubmission(Long submissionId, SubmissionStatus status, String username){
        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));

        UserDto userDto = userService.getUserProfile(username);
        boolean isTeacher = validateRole(userDto, List.of("TEACHER"));
        GlobalExceptionHandler.Thrower.throwIf(
                !isTeacher,
                new UserHasNotPermissionException("Only TEACHER can accept/reject submission!"));

        submission.setSubmissionStatus(status);
        submissionRepo.save(submission);

        return SubmissionMapper.toSubmissionDto(submission);
    }
    @Override
    public SubmissionDto updateSubmissionByTaskId(Long taskId, MultipartFile file, String githubLink, String username) {
        Submission submission = SubmissionMapper.toSubmission(getSubmissionByTaskIdAndUsername(taskId, username));

        deleteFileByTaskId(taskId, username);
        String fileName = fileHandlerService.uploadFile(file);

        submission.setFileUrl(fileName);
        Optional.ofNullable(githubLink).ifPresent(submission::setGithubLink);
        submissionRepo.save(submission);

        return SubmissionMapper.toSubmissionDto(submission);
    }
    @Override
    public SubmissionDto deleteFileByTaskId(Long taskId, String username) {
        Submission submission = SubmissionMapper.toSubmission(getSubmissionByTaskIdAndUsername(taskId, username));
        UserDto userDto = userService.getUserProfile(username);
        GlobalExceptionHandler.Thrower.throwIf(
                !submission.getStudentId().equals(userDto.getId()),
                new UserHasNotPermissionException("You can't modify another student's submission!"));

        String fileName = submission.getFileUrl();
        fileHandlerService.deleteFile(fileName);
        submission.setFileUrl(null);
        submissionRepo.save(submission);
        return SubmissionMapper.toSubmissionDto(submission);
    }
}
