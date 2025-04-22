package com.binarybrain.submission.service.impl;

import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.exception.UnsupportedFileTypeException;
import com.binarybrain.submission.service.FileHandlerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileHandlerServiceImpl implements FileHandlerService {

    @Value("${file.upload-dir}")
    private String fileDirectory;

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    @Override
    public String uploadFile(MultipartFile file) {
        // Validate the file type before proceeding
        validateFileType(file);

        String fileName = generateFileName(file);
        Path filePath = resolveFilePath(fileName);

        ensureDirectoryExists(filePath);

        return writeFile(file, filePath, fileName);
    }

    @Override
    public byte[] downloadFile(String filename) {
        validateFilename(filename);

        Path filePath = resolveFilePath(filename);

        return readFile(filePath, filename);
    }

    @Override
    public void deleteFile(String fileName) {
        if (fileName == null) return;

        validateFilename(fileName);

        Path filePath = resolveFilePath(fileName);

        deleteFileIfExists(filePath);
    }

    // File validation methods
    private void validateFileType(MultipartFile file) {
        if (!isValidFileType(file)) {
            throw new UnsupportedFileTypeException("Unsupported file type: " + file.getContentType() + "\n(Supported file types: PDF, JPG, JPEG, PNG)");
        }
    }

    private void validateFilename(String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }
    }

    public boolean isValidFileType(MultipartFile file) {
        return ALLOWED_FILE_TYPES.contains(file.getContentType());
    }

    // File operations
    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID() + "_" + file.getOriginalFilename();
    }

    private Path resolveFilePath(String fileName) {
        Path targetDirectory = Paths.get(fileDirectory).normalize();
        return targetDirectory.resolve(fileName).normalize();
    }

    private void ensureDirectoryExists(Path filePath) {
        try {
            Path targetDirectory = filePath.getParent();
            if (!Files.exists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
            }
        } catch (IOException ex) {
            throw new UnsupportedFileTypeException("Failed to create directories: " + ex.getMessage());
        }
    }

    private String writeFile(MultipartFile file, Path filePath, String fileName) {
        try {
            Files.write(filePath, file.getBytes());
            return fileName;
        } catch (IOException ex) {
            throw new UnsupportedFileTypeException("File upload failed: " + file.getOriginalFilename() + "\n" + ex.getMessage());
        }
    }

    private byte[] readFile(Path filePath, String filename) {
        try {
            if (!Files.exists(filePath)) {
                throw new ResourceNotFoundException("FILE NOT FOUND: " + filename);
            }
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new UnsupportedFileTypeException("File download failed: " + filename + "\n" + ex.getMessage());
        }
    }

    private void deleteFileIfExists(Path filePath) {
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException ex) {
            throw new UnsupportedFileTypeException("File deletion failed! " + ex.getMessage());
        }
    }
}
