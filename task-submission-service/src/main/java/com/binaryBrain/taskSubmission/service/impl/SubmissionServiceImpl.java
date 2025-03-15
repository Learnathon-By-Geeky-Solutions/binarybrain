package com.binaryBrain.taskSubmission.service.impl;

import com.binaryBrain.exception.ResourceNotFoundException;
import com.binaryBrain.exception.UserHasNotPermissionException;
import com.binaryBrain.taskSubmission.dto.RoleDto;
import com.binaryBrain.taskSubmission.dto.SubmissionDto;
import com.binaryBrain.taskSubmission.dto.TaskDto;
import com.binaryBrain.taskSubmission.dto.UserDto;
import com.binaryBrain.taskSubmission.mapper.SubmissionMapper;
import com.binaryBrain.taskSubmission.model.Submission;
import com.binaryBrain.taskSubmission.model.SubmissionStatus;
import com.binaryBrain.taskSubmission.model.SubmissionType;
import com.binaryBrain.taskSubmission.repository.SubmissionRepo;
import com.binaryBrain.taskSubmission.service.FileHandlerService;
import com.binaryBrain.taskSubmission.service.SubmissionService;
import com.binaryBrain.taskSubmission.service.TaskService;
import com.binaryBrain.taskSubmission.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

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
        if("CLOSED".equals(taskDto.getStatus())){
            throw new ResourceNotFoundException("Task is not OPEN for submission!");
        }
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, List.of("STUDENT")))
            throw new UserHasNotPermissionException("Only STUDENT can submit task!");

        if(submissionRepo.findByTaskIdAndSubmittedBy(taskId, username).isPresent()){
            throw new UserHasNotPermissionException("You have already submitted this task. Multiple submissions are not allowed!");
        }

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
        SubmissionDto submissionDto = getSubmissionById(submissionId);
        UserDto userDto = userService.getUserProfile(username);
        if (!validateRole(userDto, List.of("TEACHER")))
            throw new UserHasNotPermissionException("Only TEACHER can submit/reject submission!");

        submissionDto.setSubmissionStatus(status);
        submissionRepo.save(SubmissionMapper.toSubmission(submissionDto));

        return submissionDto;
    }
    @Override
    public SubmissionDto updateSubmissionByTaskId(Long taskId, MultipartFile file, String githubLink, String username) {
        Submission submission = SubmissionMapper.toSubmission(getSubmissionByTaskIdAndUsername(taskId, username));
        deleteFileByTaskId(taskId, username);
        String fileName = fileHandlerService.uploadFile(file);

        submission.setFileUrl(fileName);
        if(githubLink!=null) submission.setGithubLink(githubLink);
        submissionRepo.save(submission);

        return SubmissionMapper.toSubmissionDto(submission);
    }
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
