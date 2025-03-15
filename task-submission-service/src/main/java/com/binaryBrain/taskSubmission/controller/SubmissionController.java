package com.binaryBrain.taskSubmission.controller;

import com.binaryBrain.taskSubmission.dto.SubmissionDto;
import com.binaryBrain.taskSubmission.model.SubmissionStatus;
import com.binaryBrain.taskSubmission.service.FileHandlerService;
import com.binaryBrain.taskSubmission.service.SubmissionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/submission")
public class SubmissionController {
    private final SubmissionService submissionService;
    private final FileHandlerService fileHandlerService;

    public SubmissionController(SubmissionService submissionService, FileHandlerService fileHandlerService) {
        this.submissionService = submissionService;
        this.fileHandlerService = fileHandlerService;
    }

    @PostMapping("/{taskId}/submit")
    public ResponseEntity<SubmissionDto> submitTask(@PathVariable Long taskId,
                                                    @RequestParam("file") MultipartFile file,
                                                    @RequestParam(required = false) String githubLink,
                                                    @RequestHeader("X-User-Username") String username){
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        SubmissionDto submissionDto = submissionService.submitTask(taskId, file, githubLink, username);
        return new ResponseEntity<>(submissionDto, HttpStatus.CREATED);
    }

    @GetMapping("{submissionId}")
    public ResponseEntity<SubmissionDto> getSubmissionById(@PathVariable Long submissionId){
        SubmissionDto submissionDto = submissionService.getSubmissionById(submissionId);
        return new ResponseEntity<>(submissionDto, HttpStatus.OK);
    }

    @GetMapping("/{taskId}/from-task")
    public ResponseEntity<List<SubmissionDto>> getAllSubmissionFromTask(@PathVariable Long taskId){
        List<SubmissionDto> submissionDtoList = submissionService.getAllSubmissionFromTask(taskId);
        return new ResponseEntity<>(submissionDtoList, HttpStatus.OK);
    }

    @GetMapping("{taskId}/user-submission")
    public ResponseEntity<SubmissionDto> getSubmissionByTaskIdAndUsername(@PathVariable Long taskId,
                                                                          @RequestHeader("X-User-Username") String username){
        SubmissionDto submissionDto = submissionService.getSubmissionByTaskIdAndUsername(taskId, username);
        return new ResponseEntity<>(submissionDto, HttpStatus.OK);
    }

    @GetMapping("/file/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName){
        byte[] fileData = fileHandlerService.downloadFile(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(determineMediaType(fileName));
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }

    @PutMapping("/review/{submissionId}")
    public ResponseEntity<SubmissionDto> acceptOrRejectSubmission(@PathVariable Long submissionId,
                                                                  @RequestParam SubmissionStatus status,
                                                                  @RequestHeader("X-User-Username") String username){
        SubmissionDto submissionDto = submissionService.acceptOrRejectSubmission(submissionId, status, username);
        return new ResponseEntity<>(submissionDto, HttpStatus.OK);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<SubmissionDto> updateSubmissionByTaskId(@PathVariable Long taskId,
                                                                  @RequestParam MultipartFile file,
                                                                  @RequestParam(required = false) String githubLink,
                                                                  @RequestHeader("X-User-Username") String username){
        SubmissionDto submissionDto = submissionService.updateSubmissionByTaskId(taskId, file, githubLink, username);
        return new ResponseEntity<>(submissionDto, HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<SubmissionDto> deleteFileByTaskId(@PathVariable Long taskId,
                                                            @RequestHeader("X-User-Username") String username){
        SubmissionDto submissionDto = submissionService.deleteFileByTaskId(taskId, username);
        return new ResponseEntity<>(submissionDto, HttpStatus.OK);
    }

    private MediaType determineMediaType(String fileName) {
        if (fileName.endsWith(".pdf")) return MediaType.APPLICATION_PDF;
        if (fileName.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        return MediaType.APPLICATION_OCTET_STREAM; // Default for other file types
    }
}
