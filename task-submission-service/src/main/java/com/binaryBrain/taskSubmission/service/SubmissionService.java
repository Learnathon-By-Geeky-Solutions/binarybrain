package com.binaryBrain.taskSubmission.service;

import com.binaryBrain.taskSubmission.dto.SubmissionDto;
import org.springframework.web.multipart.MultipartFile;

public interface SubmissionService {
    SubmissionDto submitTask(Long taskId, MultipartFile file, String githubLink, String username);
    SubmissionDto getSubmissionById(Long submissionId);
    SubmissionDto getSubmissionByTaskIdAndUsername(Long taskId, String username);
    SubmissionDto updateSubmissionByTaskId(Long taskId, MultipartFile file, String githubLink, String username);
    SubmissionDto deleteFileByTaskId(Long taskId, String username);
}