package com.binaryBrain.taskSubmission.repository;

import com.binaryBrain.taskSubmission.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    List<Submission> findByTaskId(Long taskId);
    Optional<Submission> findByTaskIdAndSubmittedBy(Long taskId, String username);

}
