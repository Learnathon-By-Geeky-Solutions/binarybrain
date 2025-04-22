package com.binarybrain.submission.service;

import com.binarybrain.exception.*;
import com.binarybrain.submission.service.impl.FileHandlerServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class FileHandlerServiceImplTest {

    @InjectMocks
    private FileHandlerServiceImpl fileHandlerService;

    @Mock
    private MultipartFile multipartFile;

    private final String testUploadDir = "test-uploads";
    private final String testFileName = "test-file.pdf";
    private final String testFileContentType = "application/pdf";

    @BeforeEach
    void setUp() throws IOException {
        // Set up test directory
        ReflectionTestUtils.setField(fileHandlerService, "fileDirectory", testUploadDir);

        Path testDir = Paths.get(testUploadDir);
        if (!Files.exists(testDir)) {
            Files.createDirectories(testDir);
        }
        Files.write(testDir.resolve(testFileName), "test content".getBytes());

        String testUUID = "123e4567-e89b-12d3-a456-426614174000";
        UUID uuid = UUID.fromString(testUUID);
        try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
            mockedUUID.when(UUID::randomUUID).thenReturn(uuid);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up test directory
        FileSystemUtils.deleteRecursively(Paths.get(testUploadDir));
    }

    @Test
    void uploadFile_WithValidFile_ShouldUploadSuccessfully() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn(testFileName);
        when(multipartFile.getContentType()).thenReturn(testFileContentType);
        when(multipartFile.getBytes()).thenReturn("test content".getBytes());

        String result = fileHandlerService.uploadFile(multipartFile);

        assertNotNull(result);
        assertTrue(Files.exists(Paths.get(testUploadDir).resolve(result)));
    }

    @Test
    void uploadFile_WithInvalidFileType_ShouldThrowException() {
        when(multipartFile.getContentType()).thenReturn("application/exe");

        assertThrows(UnsupportedFileTypeException.class,
                () -> fileHandlerService.uploadFile(multipartFile));
    }

    @Test
    void uploadFile_WithDirectoryTraversal_ShouldThrowException() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("../malicious-file.xlsx");
        when(multipartFile.getContentType()).thenReturn(testFileContentType);
        when(multipartFile.getBytes()).thenReturn("malicious content".getBytes());

        assertThrows(UnsupportedFileTypeException.class,
                () -> fileHandlerService.uploadFile(multipartFile));
    }

    @Test
    void uploadFile_WhenDirectoryDoesNotExist_ShouldCreateDirectory() throws IOException {
        FileSystemUtils.deleteRecursively(Paths.get(testUploadDir));
        when(multipartFile.getOriginalFilename()).thenReturn(testFileName);
        when(multipartFile.getContentType()).thenReturn(testFileContentType);
        when(multipartFile.getBytes()).thenReturn("test content".getBytes());

        fileHandlerService.uploadFile(multipartFile);

        assertTrue(Files.exists(Paths.get(testUploadDir)));
    }

    @Test
    void downloadFile_WithValidFilename_ShouldReturnFileContent() {
        byte[] result = fileHandlerService.downloadFile(testFileName);

        assertArrayEquals("test content".getBytes(), result);
    }

    @Test
    void downloadFile_WithInvalidFilename_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> fileHandlerService.downloadFile("../invalid-file.pdf"));
    }

    @Test
    void downloadFile_WithNonexistentFile_ShouldThrowException() {
        assertThrows(ResourceNotFoundException.class,
                () -> fileHandlerService.downloadFile("nonexistent-file.pdf"));
    }

    @Test
    void deleteFile_WithValidFilename_ShouldDeleteFile() {
        fileHandlerService.deleteFile(testFileName);

        assertFalse(Files.exists(Paths.get(testUploadDir).resolve(testFileName)));
    }

    @Test
    void deleteFile_WithInvalidFilename_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> fileHandlerService.deleteFile("../invalid-file.pdf"));
    }

    @Test
    void deleteFile_WithNullFilename_ShouldDoNothing() {
        fileHandlerService.deleteFile(null);
    }

    @Test
    void deleteFile_WithNonexistentFile_ShouldNotThrowException() {
        fileHandlerService.deleteFile("nonexistent-file.pdf");

    }

    @Test
    void isValidFileType_WithAllowedTypes_ShouldReturnTrue() {
        assertTrue(fileHandlerService.isValidFileType(createMockFile("application/pdf")));
        assertTrue(fileHandlerService.isValidFileType(createMockFile("image/png")));
        assertTrue(fileHandlerService.isValidFileType(createMockFile("image/jpeg")));
        assertTrue(fileHandlerService.isValidFileType(createMockFile("image/jpg")));
    }

    @Test
    void isValidFileType_WithDisallowedTypes_ShouldReturnFalse() {
        assertFalse(fileHandlerService.isValidFileType(createMockFile("application/exe")));
        assertFalse(fileHandlerService.isValidFileType(createMockFile("text/plain")));
        assertFalse(fileHandlerService.isValidFileType(createMockFile("image/gif")));
    }

    private MultipartFile createMockFile(String contentType) {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(contentType);
        return file;
    }
}
