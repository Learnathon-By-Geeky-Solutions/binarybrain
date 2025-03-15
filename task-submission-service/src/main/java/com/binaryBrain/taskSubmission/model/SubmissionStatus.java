package com.binaryBrain.taskSubmission.model;

public enum SubmissionStatus {
    /**
     * Initial state when submission is received but not yet evaluated
     */
    PENDING,
    /**
     * State when submission has been reviewed and approved
     */
    ACCEPTED,
    /**
     * State when submission has been reviewed and not approved
     */
    REJECTED
}
