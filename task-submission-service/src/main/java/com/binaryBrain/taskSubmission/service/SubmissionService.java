package com.binaryBrain.taskSubmission.service;

import com.binaryBrain.taskSubmission.dto.SubmissionDto;
import com.binaryBrain.taskSubmission.model.SubmissionStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubmissionService {
    /**
     * Submit a new task with optional file attachment and GitHub link
     * @param taskId The ID of the task being submitted
     * @param file The file attachment for the submission (can be null)
     * @param githubLink GitHub repository link (can be null)
     * @param username The username of the submitter
     * @return SubmissionDto containing the submission details
     */
    SubmissionDto submitTask(Long taskId, MultipartFile file, String githubLink, String username);
    /**
     * Retrieve a submission by its ID
     * @param submissionId The ID of the submission to retrieve
     * @return SubmissionDto containing the submission details
     */
    SubmissionDto getSubmissionById(Long submissionId);
    List<SubmissionDto> getAllSubmissionFromTask(Long taskId);
    /**
     * Find a submission by task for with specific user
     * @param taskId The ID of the task
     * @param username The username of the submitter
     * @return SubmissionDto containing the submission details
     */
    SubmissionDto getSubmissionByTaskIdAndUsername(Long taskId, String username);

    /**
     * Update an existing submission for a specific task
     * @param submissionId The ID of the submission that is reviewd by teacher
     * @param status It contains the flag for ACCEPT / REJECT Submission
     * @param username The username of the reviewer(Teacher)
     * @return SubmissionDto containing the updated submission details
     */
    SubmissionDto acceptOrRejectSubmission(Long submissionId, SubmissionStatus status, String username);

    /**
     * Update an existing submission for a specific task
     * @param taskId The ID of the task
     * @param file Updated file attachment (can be null to keep existing)
     * @param githubLink Updated GitHub link (can be null to keep existing)
     * @param username The username of the submitter
     * @return SubmissionDto containing the updated submission details
     */
    SubmissionDto updateSubmissionByTaskId(Long taskId, MultipartFile file, String githubLink, String username);
    /**
     * Delete a file attachment from a submission
     * @param taskId The ID of the task
     * @param username The username of the submitter
     * @return SubmissionDto containing the updated submission details (without file)
     */
    SubmissionDto deleteFileByTaskId(Long taskId, String username);
}