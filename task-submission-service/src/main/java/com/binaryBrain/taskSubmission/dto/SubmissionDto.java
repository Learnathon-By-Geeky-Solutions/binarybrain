package com.binaryBrain.taskSubmission.dto;

import com.binaryBrain.taskSubmission.model.SubmissionStatus;
import com.binaryBrain.taskSubmission.model.SubmissionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DATA TRANSFER OBJECT FOR TASK SUBMISSIONS.
 * FACILITATES DATA EXCHANGE BETWEEN CLIENT AND SERVER.
 */
@Data
public class SubmissionDto {
    private Long id;
    private Long taskId;
    private Long studentId;
    private String submittedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submissionTime;
    private String fileUrl;
    private String githubLink;
    private SubmissionStatus submissionStatus;
    private SubmissionType submissionType;
}
