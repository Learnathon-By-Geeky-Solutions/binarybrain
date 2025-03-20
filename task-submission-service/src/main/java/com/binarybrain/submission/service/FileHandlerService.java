package com.binarybrain.submission.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileHandlerService {
    String uploadFile(MultipartFile file);
    byte[] downloadFile(String filename);

    void deleteFile(String fileName);
}
