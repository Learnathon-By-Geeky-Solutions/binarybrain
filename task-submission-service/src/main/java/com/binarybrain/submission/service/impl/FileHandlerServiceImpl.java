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
        try {
            validateFileType(file);

            Path targetDirectory = getNormalizedDirectory();
            String fileName = generateUniqueFilename(file.getOriginalFilename());
            Path filePath = resolveSecurePath(targetDirectory, fileName);

            createDirectoryIfMissing(targetDirectory);
            Files.write(filePath, file.getBytes());

            return fileName;
        } catch (IOException ex) {
            throw new UnsupportedFileTypeException("File upload failed: " + file.getOriginalFilename() + "\n" + ex);
        }
    }

    @Override
    public byte[] downloadFile(String filename) {
        validateFilename(filename);

        try {
            Path targetDirectory = getNormalizedDirectory();
            Path filePath = resolveSecurePath(targetDirectory, filename);

            if (!Files.exists(filePath)) {
                throw new ResourceNotFoundException("FILE NOT FOUND: " + filename);
            }

            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new UnsupportedFileTypeException("File download failed: " + filename + "\n" + ex);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        if (fileName == null) return;

        validateFilename(fileName);

        try {
            Path filePath = resolveSecurePath(getNormalizedDirectory(), fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException ex) {
            throw new UnsupportedFileTypeException("File deletion failed! " + ex);
        }
    }

    public boolean isValidFileType(MultipartFile file) {
        return ALLOWED_FILE_TYPES.contains(file.getContentType());
    }

    // ----- Private Helper Methods -----

    private void validateFileType(MultipartFile file) {
        if (!isValidFileType(file)) {
            throw new UnsupportedFileTypeException("Unsupported file type: " + file.getContentType()
                    + "\n (Supported file: PDF, JPG, JPEG, PNG)");
        }
    }

    private void validateFilename(String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename");
        }
    }

    private Path getNormalizedDirectory() {
        return Paths.get(fileDirectory).normalize();
    }

    private String generateUniqueFilename(String originalName) {
        return UUID.randomUUID() + "_" + originalName;
    }

    private Path resolveSecurePath(Path baseDirectory, String fileName) throws IOException {
        Path resolved = baseDirectory.resolve(fileName).normalize();
        if (!resolved.startsWith(baseDirectory)) {
            throw new IOException("Entry is outside of the target directory");
        }
        return resolved;
    }

    private void createDirectoryIfMissing(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }
}
