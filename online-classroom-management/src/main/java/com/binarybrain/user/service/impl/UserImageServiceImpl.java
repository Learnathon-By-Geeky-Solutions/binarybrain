package com.binarybrain.user.service.impl;

import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.exception.UserHasNotPermissionException;
import com.binarybrain.exception.global.GlobalExceptionHandler;
import com.binarybrain.user.model.User;
import com.binarybrain.user.model.UserImage;
import com.binarybrain.user.repository.UserImageRepository;
import com.binarybrain.user.repository.UserRepository;
import com.binarybrain.user.service.UserImageService;
import com.binarybrain.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.UnaryOperator;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class UserImageServiceImpl implements UserImageService {
    @Value("${photo.upload-dir}")
    private String photoDirectory;
    private final UserRepository userRepository;
    private final UserImageRepository imageRepository;
    private final UserService userService;
    private final ImageSearchService imageSearchService;

    public UserImageServiceImpl(UserRepository userRepository, UserImageRepository imageRepository, UserService userService, ImageSearchService imageSearchService) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.imageSearchService = imageSearchService;
    }


    @Override
    public String uploadPhoto(Long id, MultipartFile file, String username) throws IOException {
        User user = userService.getUserProfileById(id, username);
        GlobalExceptionHandler.Thrower.throwIf(!user.getUsername().equals(username), new UserHasNotPermissionException("You don't have permission to upload another person's image!"));

        UserImage userImage = new UserImage();

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String photoUrl = generatePhotoUrl(id.toString(), base64Image);

        user.setProfilePicture(photoUrl);
        userImage.setId(id);
        userImage.setUsername(username);
        userImage.setImageBase64(base64Image);

        userRepository.save(user);
        imageRepository.save(userImage);
        return photoUrl;
    }

    @Override
    public byte[] getPhoto(String filename) {
        GlobalExceptionHandler.Thrower.throwIf(filename.contains("..") || filename.contains("/") || filename.contains("\\"),new IllegalArgumentException("Invalid filename!"));
        try{
            Path targetDirectory = Paths.get(photoDirectory).normalize();
            Path photoPath = targetDirectory.resolve(filename).normalize();
            GlobalExceptionHandler.Thrower.throwIf(!photoPath.startsWith(targetDirectory), new IOException("Entry is outside of the target directory"));
            return Files.readAllBytes(photoPath);
        } catch (IOException ex){
            throw new ResourceNotFoundException("Photo download failed: "+ filename + "\n" + ex);
        }
    }

    @Override
    public List<User> searchUsersByImage(MultipartFile[] base64Image) throws IOException {
        return imageSearchService.searchByImage(base64Image);
    }

    private final UnaryOperator<String> fileExtension = fileName -> Optional.of(fileName)
            .filter(name -> name.contains(".")).
            map(name -> "." + name.substring(name.lastIndexOf(".") + 1))
            .orElse(".png");


    private String generatePhotoUrl(String id, String base64Image) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            String fileName = id + fileExtension.apply(id);

            Path fileStorageLocation = Paths.get(photoDirectory).toAbsolutePath().normalize();

            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            GlobalExceptionHandler.Thrower.throwIf(!Files.exists(fileStorageLocation), new IOException("Entry is outside of the target directory"));
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.write(targetLocation, imageBytes);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/user/photo/" + fileName).toUriString();
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to save image", exception);
        }
    }
}
