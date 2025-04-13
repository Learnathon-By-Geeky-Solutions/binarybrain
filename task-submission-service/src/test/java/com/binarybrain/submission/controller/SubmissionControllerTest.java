package com.binarybrain.submission.controller;

import com.binarybrain.submission.dto.RoleDto;
import com.binarybrain.submission.dto.TaskDto;
import com.binarybrain.submission.dto.UserDto;
import com.binarybrain.submission.model.Submission;
import com.binarybrain.submission.model.SubmissionStatus;
import com.binarybrain.submission.repository.SubmissionRepo;
import com.binarybrain.submission.service.FileHandlerService;
import com.binarybrain.submission.service.TaskService;
import com.binarybrain.submission.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the SubmissionController class.
 * Tests various endpoints related to submission handling including creation,
 * retrieval, updating, and deletion of submissions.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private SubmissionRepo submissionRepo;

    @MockitoBean
    private FileHandlerService fileHandlerService;

    /**
     * Creates a Submission object with specified parameters.
     *
     * @param id              the submission ID
     * @param taskId          the associated task ID
     * @param link            the GitHub link for the submission
     * @param username        the username of the submitter
     * @param status          the submission status
     * @return a configured Submission object
     */
    private Submission createSubmission(Long id, Long taskId, String link, String username, SubmissionStatus status) {
        Submission submission = new Submission();
        submission.setId(id);
        submission.setTaskId(taskId);
        submission.setGithubLink(link);
        submission.setSubmittedBy(username);
        submission.setSubmissionStatus(status);
        return submission;
    }

    /**
     * Creates a TaskDto object with specified parameters.
     *
     * @return a configured TaskDto object
     */
    private TaskDto createTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setStatus("OPEN");
        taskDto.setDeadline(LocalDateTime.now().plusDays(1));
        taskDto.setDescription("Task 2 description");
        taskDto.setTitle("Task 2");
        return taskDto;
    }

    /**
     * Sets up mock data and behavior before each test execution.
     */
    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto();
        userDto.setUsername("moinul");
        userDto.setFirstName("Moinul");
        userDto.setLastName("Islam");
        userDto.setRoles(new HashSet<>());

        RoleDto adminRole = new RoleDto();
        adminRole.setId(1L);
        adminRole.setName("ADMIN");
        userDto.getRoles().add(adminRole);

        RoleDto studentRole = new RoleDto();
        studentRole.setId(2L);
        studentRole.setName("STUDENT");
        userDto.getRoles().add(studentRole);

        RoleDto teacherRole = new RoleDto();
        teacherRole.setId(3L);
        teacherRole.setName("TEACHER");
        userDto.getRoles().add(teacherRole);

        when(userService.getUserProfile("moinul")).thenReturn(userDto);
        when(submissionRepo.save(any())).thenReturn(null);
    }

    /**
     * Tests the submission of a task with a file and GitHub link.
     *
     * @throws Exception if the test execution fails
     */
    @Test
    void testSubmitTask() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Test content".getBytes());

        when(taskService.getTaskById(2L, "moinul")).thenReturn(createTask());

        mockMvc.perform(multipart("/api/v1/private/submission/2/submit")
                        .file(file)
                        .param("githubLink", "https://github.com/example/repo")
                        .header("X-User-Username", "moinul"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").value(2L))
                .andExpect(jsonPath("$.githubLink").value("https://github.com/example/repo"))
                .andExpect(jsonPath("$.submissionStatus").value("PENDING"));
    }

    @Test
    void testSubmitTaskWithEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/v1/private/submission/2/submit")
                        .file(emptyFile)
                        .header("X-User-Username", "moinul"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSubmitTaskWithNullFile() throws Exception {
        mockMvc.perform(multipart("/api/v1/private/submission/2/submit")
                        .param("githubLink", "https://github.com/example/repo")
                        .header("X-User-Username", "moinul"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests retrieval of a submission by its ID.
     *
     * @throws Exception if the test execution fails
     */
    @Test
    void testGetSubmissionById() throws Exception {
        Submission submission = createSubmission(1L, 1L, "https://iishanto.com", "moinul", SubmissionStatus.PENDING);
        when(submissionRepo.findById(1L)).thenReturn(Optional.of(submission));

        mockMvc.perform(get("/api/v1/private/submission/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.taskId").value(1L))
                .andExpect(jsonPath("$.submissionStatus").value("PENDING"));
    }

    /**
     * Tests retrieval of all submissions for a specific task.
     *
     * @throws Exception if the test execution fails
     */
    @Test
    void testGetAllSubmissionsFromTask() throws Exception {
        Submission submission = createSubmission(1L, 1L, "https://iishanto.com", "moinul", SubmissionStatus.PENDING);
        when(submissionRepo.findByTaskId(1L)).thenReturn(java.util.List.of(submission));

        mockMvc.perform(get("/api/v1/private/submission/1/from-task"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].taskId").value(1L))
                .andExpect(jsonPath("$[0].submissionStatus").value("PENDING"));
    }

    /**
     * Tests retrieval of a submission by task ID and username.
     *
     * @throws Exception if the test execution fails
     */
    @Test
    void testGetSubmissionByTaskIdAndUsername() throws Exception {
        Submission submission = createSubmission(1L, 1L, "https://iishanto.com", "moinul", SubmissionStatus.PENDING);
        when(submissionRepo.findByTaskIdAndSubmittedBy(1L, "moinul")).thenReturn(Optional.of(submission));

        mockMvc.perform(get("/api/v1/private/submission/1/user-submission")
                        .header("X-User-Username", "moinul"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.taskId").value(1L))
                .andExpect(jsonPath("$.submissionStatus").value("PENDING"));
    }

    /**
     * Tests the file download functionality.
     *
     * @throws Exception if the test execution fails
     */
    @Test
    void testDownloadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Test content".getBytes());
        when(fileHandlerService.downloadFile("test.pdf")).thenReturn(file.getBytes());

        mockMvc.perform(get("/api/v1/private/submission/file/test.pdf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    void testDownloadPdfFile() throws Exception {
        byte[] fileContent = "PDF content".getBytes();
        when(fileHandlerService.downloadFile("test.pdf")).thenReturn(fileContent);

        mockMvc.perform(get("/api/v1/private/submission/file/test.pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"test.pdf\""))
                .andExpect(content().bytes(fileContent));
    }

    @Test
    void testDownloadPngFile() throws Exception {
        byte[] fileContent = "PNG content".getBytes();
        when(fileHandlerService.downloadFile("test.png")).thenReturn(fileContent);

        mockMvc.perform(get("/api/v1/private/submission/file/test.png"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"test.png\""))
                .andExpect(content().bytes(fileContent));
    }

    @Test
    void testDownloadJpgFile() throws Exception {
        byte[] fileContent = "JPG content".getBytes();
        when(fileHandlerService.downloadFile("test.jpg")).thenReturn(fileContent);

        mockMvc.perform(get("/api/v1/private/submission/file/test.jpg"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"test.jpg\""))
                .andExpect(content().bytes(fileContent));
    }

    @Test
    void testDownloadJpegFile() throws Exception {
        byte[] fileContent = "JPEG content".getBytes();
        when(fileHandlerService.downloadFile("test.jpeg")).thenReturn(fileContent);

        mockMvc.perform(get("/api/v1/private/submission/file/test.jpeg"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"test.jpeg\""))
                .andExpect(content().bytes(fileContent));
    }

    @Test
    void testDownloadUnknownFileType() throws Exception {
        byte[] fileContent = "Unknown content".getBytes();
        when(fileHandlerService.downloadFile("test.unknown")).thenReturn(fileContent);

        mockMvc.perform(get("/api/v1/private/submission/file/test.unknown"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"test.unknown\""))
                .andExpect(content().bytes(fileContent));
    }

    /**
     * Tests accepting or rejecting a submission.
     *
     * @throws Exception if the test execution fails
     */
    @Test
    void testAcceptOrRejectSubmission() throws Exception {
        Submission submission = createSubmission(1L, 1L, "https://iishanto.com", "moinul", SubmissionStatus.PENDING);
        when(submissionRepo.findById(1L)).thenReturn(Optional.of(submission));

        mockMvc.perform(put("/api/v1/private/submission/review/1")
                        .param("status", "ACCEPTED")
                        .header("X-User-Username", "moinul"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.submissionStatus").value("ACCEPTED"));
    }

    /**
     * Tests updating a submission by task ID.
     *
     * @throws Exception if the test execution fails
     */
    @Test
    void testUpdateSubmissionByTaskId() throws Exception {
        Submission submission = createSubmission(1L, 1L, "https://iishanto.com", "moinul", SubmissionStatus.PENDING);
        when(submissionRepo.findByTaskIdAndSubmittedBy(1L, "moinul")).thenReturn(Optional.of(submission));

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Test content".getBytes());
        when(fileHandlerService.uploadFile(file)).thenReturn("test.pdf");
        when(submissionRepo.save(any())).thenReturn(submission);

        mockMvc.perform(multipart("/api/v1/private/submission/1")
                        .file(file)
                        .param("githubLink", "https://github.com/example/repo")
                        .header("X-User-Username", "moinul")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1L))
                .andExpect(jsonPath("$.githubLink").value("https://github.com/example/repo"))
                .andExpect(jsonPath("$.submissionStatus").value("PENDING"));
    }

    /**
     * Tests deletion of a file by task ID.
     *
     * @throws Exception if the test execution fails
     */
    @Test
    void testDeleteFileByTaskId() throws Exception {
        doNothing().when(fileHandlerService).deleteFile(any());
        Submission submission = createSubmission(1L, 1L, "https://iishanto.com", "moinul", SubmissionStatus.PENDING);
        when(submissionRepo.findByTaskIdAndSubmittedBy(1L, "moinul")).thenReturn(Optional.of(submission));

        mockMvc.perform(delete("/api/v1/private/submission/1")
                        .header("X-User-Username", "moinul"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fileUrl").doesNotExist());
    }
}