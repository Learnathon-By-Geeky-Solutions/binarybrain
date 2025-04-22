package com.binarybrain.submission.service.impl;

import com.binarybrain.exception.*;
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

    // Helper to validate user role
    private boolean validateRole(UserDto userDto, List<String> targetRoles) {
        return userDto.getRoles().stream()
                .map(RoleDto::getName)
                .anyMatch(targetRoles::contains);
    }

    // Helper to check if the task is open for submission
    private TaskDto validateTaskForSubmission(Long taskId, String username) {
        TaskDto taskDto = taskService.getTaskById(taskId, username);
        if ("CLOSED".equals(taskDto.getStatus())) {
            throw new ResourceNotFoundException("Task is not OPEN for submission!");
        }
        return taskDto;
    }

    // Helper to check if the user has already submitted the task
    private void checkIfAlreadySubmitted(Long taskId, String username) {
        Optional<Submission> existingSubmission = submissionRepo.findByTaskIdAndSubmittedBy(taskId, username);
        if (existingSubmission.isPresent()) {
            throw new AlreadyExistsException("You have already submitted this task. Multiple submissions are not allowed!");
        }
    }

    // Helper to upload file
    private String uploadFile(MultipartFile file) {
        return fileHandlerService.uploadFile(file);
    }

    // Helper to set submission status based on task deadline
    private SubmissionType getSubmissionType(TaskDto taskDto) {
        return taskDto.getDeadline().isAfter(LocalDateTime.now()) ? SubmissionType.IN_TIME : SubmissionType.LATE;
    }

    // Main method for submitting task
    @Override
    public SubmissionDto submitTask(Long taskId, MultipartFile file, String githubLink, String username) {
        TaskDto taskDto = validateTaskForSubmission(taskId, username);
        UserDto userDto = userService.getUserProfile(username);

        if (!validateRole(userDto, List.of("STUDENT"))) {
            throw new UserHasNotPermissionException("Only STUDENT can submit task!");
        }

        checkIfAlreadySubmitted(taskId, username);

        String fileName = uploadFile(file);
        Submission submission = new Submission();
        submission.setTaskId(taskId);
        submission.setStudentId(userDto.getId());
        submission.setSubmittedBy(username);
        submission.setFileUrl(fileName);
        submission.setGithubLink(githubLink);
        submission.setSubmissionType(getSubmissionType(taskDto));
        submission.setSubmissionStatus(SubmissionStatus.PENDING);
        submission.setSubmissionTime(LocalDateTime.now());

        submissionRepo.save(submission);
        return SubmissionMapper.toSubmissionDto(submission);
    }

    // Get a submission by ID
    @Override
    public SubmissionDto getSubmissionById(Long submissionId) {
        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));
        return SubmissionMapper.toSubmissionDto(submission);
    }

    // Get all submissions for a given task
    @Override
    public List<SubmissionDto> getAllSubmissionFromTask(Long taskId) {
        List<Submission> submissionList = submissionRepo.findByTaskId(taskId);
        return submissionList.stream()
                .map(SubmissionMapper::toSubmissionDto)
                .toList();
    }

    // Get submission by task ID and student username
    @Override
    public SubmissionDto getSubmissionByTaskIdAndUsername(Long taskId, String username) {
        Submission submission = submissionRepo.findByTaskIdAndSubmittedBy(taskId, username)
                .orElseThrow(() -> new ResourceNotFoundException("No submission found for task id " + taskId + " by student " + username));
        return SubmissionMapper.toSubmissionDto(submission);
    }

    // Accept or reject a submission by teacher
    @Override
    public SubmissionDto acceptOrRejectSubmission(Long submissionId, SubmissionStatus status, String username) {
        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));
        UserDto userDto = userService.getUserProfile(username);

        if (!validateRole(userDto, List.of("TEACHER"))) {
            throw new UserHasNotPermissionException("Only TEACHER can accept/reject submission!");
        }

        submission.setSubmissionStatus(status);
        submissionRepo.save(submission);
        return SubmissionMapper.toSubmissionDto(submission);
    }

    // Update a submission by task ID
    @Override
    public SubmissionDto updateSubmissionByTaskId(Long taskId, MultipartFile file, String githubLink, String username) {
        Submission submission = SubmissionMapper.toSubmission(getSubmissionByTaskIdAndUsername(taskId, username));
        deleteFileByTaskId(taskId, username);

        String fileName = uploadFile(file);
        submission.setFileUrl(fileName);
        if (githubLink != null) {
            submission.setGithubLink(githubLink);
        }

        submissionRepo.save(submission);
        return SubmissionMapper.toSubmissionDto(submission);
    }

    // Delete the file of a submission by task ID
    @Override
    public SubmissionDto deleteFileByTaskId(Long taskId, String username) {
        Submission submission = SubmissionMapper.toSubmission(getSubmissionByTaskIdAndUsername(taskId, username));
        String fileName = submission.getFileUrl();
        fileHandlerService.deleteFile(fileName);
        submission.setFileUrl(null);
        submissionRepo.save(submission);
        return SubmissionMapper.toSubmissionDto(submission);
    }
}
