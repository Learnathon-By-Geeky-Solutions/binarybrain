package com.binarybrain.submission.controller;

import com.binarybrain.exception.ErrorDetails;
import com.binarybrain.submission.dto.SubmissionDto;
import com.binarybrain.submission.model.Submission;
import com.binarybrain.submission.model.SubmissionStatus;
import com.binarybrain.submission.service.FileHandlerService;
import com.binarybrain.submission.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private/submission")
public class SubmissionController {
    private final SubmissionService submissionService;
    private final FileHandlerService fileHandlerService;

    public SubmissionController(SubmissionService submissionService, FileHandlerService fileHandlerService) {
        this.submissionService = submissionService;
        this.fileHandlerService = fileHandlerService;
    }

    @Operation(
            summary = "Submit a task",
            tags = {"01 - Submit"},
            description = "Submit a task with a file and optional GitHub link",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Submission successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Submission.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "400", description = "File is required!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PostMapping(value = "/{taskId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionDto> submitTask(@PathVariable Long taskId,
                                                    @Parameter(description = "Upload File(MAX 7MB)", required = true,
                                                        content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                    @RequestParam("file") MultipartFile file,
                                                    @RequestParam(required = false) String githubLink,
                                                    @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        SubmissionDto submissionDto = submissionService.submitTask(taskId, file, githubLink, username);
        return new ResponseEntity<>(submissionDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Search Submission by id",
            tags = {"02 - Search Submission"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Submission fetched successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Submission.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Submission not found with given id!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("{submissionId}")
    public ResponseEntity<SubmissionDto> getSubmissionById(@PathVariable Long submissionId){
        SubmissionDto submissionDto = submissionService.getSubmissionById(submissionId);
        return new ResponseEntity<>(submissionDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all submissions for a task",
            tags = {"02 - Search Submission"},
            description = "This will response a list of submission which are submitted in a task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved submissions from a task",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Submission.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/{taskId}/from-task")
    public ResponseEntity<List<SubmissionDto>> getAllSubmissionFromTask(@PathVariable Long taskId){
        List<SubmissionDto> submissionDtoList = submissionService.getAllSubmissionFromTask(taskId);
        return new ResponseEntity<>(submissionDtoList, HttpStatus.OK);
    }

    @Operation(
            summary = "Retrieved user's submission for a task",
            tags = {"02 - Search Submission"},
            description = "This will response the submission of currently authenticated user for a specific task.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved specific user's submission for a specific task",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Submission.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("{taskId}/user-submission")
    public ResponseEntity<SubmissionDto> getSubmissionByTaskIdAndUsername(@PathVariable Long taskId,
                                                                          @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        SubmissionDto submissionDto = submissionService.getSubmissionByTaskIdAndUsername(taskId, username);
        return new ResponseEntity<>(submissionDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Download submitted file",
            tags = {"03 - Manage File"},
            description = "Download a file submitted by the user by its file name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved specific user's submission for a specific task",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Submission.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "File not found with given name.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/file/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName){
        byte[] fileData = fileHandlerService.downloadFile(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(determineMediaType(fileName));
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Review submission",
            tags = {"04 - Review Submission"},
            description = "Course Teacher can review student's submission and give feedback. Teacher can give feedback status as type `ACCEPTED` or `REJECTED`.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Submission reviewed successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Submission.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PutMapping("/review/{submissionId}")
    public ResponseEntity<SubmissionDto> acceptOrRejectSubmission(@PathVariable Long submissionId,
                                                                  @RequestParam SubmissionStatus status,
                                                                  @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        SubmissionDto submissionDto = submissionService.acceptOrRejectSubmission(submissionId, status, username);
        return new ResponseEntity<>(submissionDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Update submission",
            tags = {"03 - Manage File"},
            description = "Student can update their existing submission for a specific task with `taskId`.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Submission updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Submission.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Course not found with given id.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PutMapping(value = "/{taskId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionDto> updateSubmissionByTaskId(@PathVariable Long taskId,
                                                                  @Parameter(description = "Upload File", required = true,
                                                                          content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
                                                                  @RequestParam MultipartFile file,
                                                                  @RequestParam(required = false) String githubLink,
                                                                  @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        SubmissionDto submissionDto = submissionService.updateSubmissionByTaskId(taskId, file, githubLink, username);
        return new ResponseEntity<>(submissionDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a submission file",
            tags = {"03 - Manage File"},
            description = "\"Delete a file associated with a submission for a specific task with `taskId`",
            responses = {
                    @ApiResponse(responseCode = "204", description = "File deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or Expired JWT token.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "404", description = "Submission not found.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @DeleteMapping("/{taskId}")
    public ResponseEntity<SubmissionDto> deleteFileByTaskId(@PathVariable Long taskId,
                                                            @Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
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
