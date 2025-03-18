package com.binarybrain.submission.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long taskId;
    private Long studentId;
    private String submittedBy;
    private LocalDateTime submissionTime;
    private String fileUrl;
    private String githubLink;
    @Enumerated(EnumType.STRING)
    private SubmissionStatus submissionStatus;
    @Enumerated(EnumType.STRING)
    private SubmissionType submissionType;
}