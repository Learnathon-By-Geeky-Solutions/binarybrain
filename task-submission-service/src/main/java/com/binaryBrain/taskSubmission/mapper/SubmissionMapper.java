package com.binaryBrain.taskSubmission.mapper;

import com.binaryBrain.taskSubmission.dto.SubmissionDto;
import com.binaryBrain.taskSubmission.model.Submission;

public class SubmissionMapper {
    private SubmissionMapper(){
        throw new RuntimeException("This is a Utility class and can't be instantiated!");
    }

    public static SubmissionDto toSubmissionDto(Submission submission){
        SubmissionDto submissionDto = new SubmissionDto();
        submissionDto.setId(submission.getId());
        submissionDto.setTaskId(submission.getTaskId());
        submissionDto.setStudentId(submission.getStudentId());
        submissionDto.setSubmittedBy(submission.getSubmittedBy());
        submissionDto.setFileUrl(submission.getFileUrl());
        submissionDto.setGithubLink(submission.getGithubLink());
        submissionDto.setSubmissionTime(submission.getSubmissionTime());
        submissionDto.setSubmissionStatus(submission.getSubmissionStatus());
        submissionDto.setSubmissionType(submission.getSubmissionType());
        return submissionDto;
    }

    public static Submission toSubmission(SubmissionDto submissionDto) {
        Submission submission = new Submission();
        submission.setId(submissionDto.getId());
        submission.setTaskId(submissionDto.getTaskId());
        submission.setStudentId(submissionDto.getStudentId());
        submission.setSubmittedBy(submissionDto.getSubmittedBy());
        submission.setFileUrl(submissionDto.getFileUrl());
        submission.setGithubLink(submissionDto.getGithubLink());
        submission.setSubmissionTime(submissionDto.getSubmissionTime());
        submission.setSubmissionStatus(submissionDto.getSubmissionStatus());
        submission.setSubmissionType(submissionDto.getSubmissionType());
        return submission;
    }
}
