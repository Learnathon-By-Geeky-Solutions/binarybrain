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
    @Override
    public String uploadFile(MultipartFile file) {
        try{
            if (!isValidFileType(file)){
                throw new UnsupportedFileTypeException("Unsupported file type: " + file.getContentType() + "\n (Supported file: PDF, JPG, JPEG, PNG)");
            }
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetDirectory = Paths.get(fileDirectory).normalize();
            Path path = targetDirectory.resolve(fileName).normalize();

            if (!path.startsWith(targetDirectory)) {
                throw new IOException("Entry is outside of the target directory");
            }

            if (!Files.exists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
            }

            Files.write(path, file.getBytes());
            return fileName;
        }catch (IOException ex){
            throw new UnsupportedFileTypeException("File upload failed: "+ file.getOriginalFilename() + "\n" + ex);
        }
    }

    @Override
    public byte[] downloadFile(String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename");
        }
        try{
            Path targetDirectory = Paths.get(fileDirectory).normalize();
            Path filePath = targetDirectory.resolve(filename).normalize();

            if (!filePath.startsWith(targetDirectory)) {
                throw new IOException("Entry is outside of the target directory");
            }
            if(!Files.exists(filePath)){
                throw new ResourceNotFoundException("FILE NOT FOUND: " + filename);
            }
            return Files.readAllBytes(filePath);
        } catch (IOException ex){
            throw new UnsupportedFileTypeException("File download failed: "+ filename + "\n" + ex);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        if (fileName==null) {
            return;
        }
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename");
        }
        Path filePath = Paths.get(fileDirectory).resolve(fileName).normalize();
        try {
            if(Files.exists(filePath)){
                Files.delete(filePath);
            }
        }catch (IOException ex){
            throw new UnsupportedFileTypeException("File deletion failed! " + ex);
        }
    }

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "image/jpg"
    );
    private boolean isValidFileType(MultipartFile file) {
        return ALLOWED_FILE_TYPES.contains(file.getContentType());
    }

}
