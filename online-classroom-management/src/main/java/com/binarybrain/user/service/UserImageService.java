package com.binarybrain.user.service;

import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.user.model.User;
import com.binarybrain.user.model.UserImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserImageService {
    String uploadPhoto(Long id, MultipartFile file, String username) throws IOException;
    byte[] getPhoto(String filename) throws IOException;
    List<User> searchUsersByImage(MultipartFile[] base64Image) throws IOException;
}
