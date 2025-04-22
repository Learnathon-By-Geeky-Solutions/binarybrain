package com.binarybrain.user.service.impl;

import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.user.model.User;
import com.binarybrain.user.model.UserImage;
import com.binarybrain.user.repository.UserImageRepository;
import com.binarybrain.user.repository.UserRepository;
import com.binarybrain.user.service.UserImageService;
import com.binarybrain.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Service
public class UserImageServiceImpl implements UserImageService {

    @Value("${photo.upload-dir}")
    private String photoDirectory;

    private final UserRepository userRepository;
    private final UserImageRepository imageRepository;
    private final UserService userService;
    private final ImageSearchService imageSearchService;

    // Constructor injection
    public UserImageServiceImpl(UserRepository userRepository, UserImageRepository imageRepository, UserService userService, ImageSearchService imageSearchService) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.imageSearchService = imageSearchService;
    }

    @Override
    public String uploadPhoto(Long id, MultipartFile file, String username) throws IOException {
        // Fetch user profile
        User user = userService.getUserProfileById(id, username);

        // Convert image to Base64 and generate a URL for the photo
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String photoUrl = generatePhotoUrl(id.toString(), base64Image);

        // Update user profile with the new photo URL
        user.setProfilePicture(photoUrl);
        userRepository.save(user);

        // Save user image record
        UserImage userImage = new UserImage();
        userImage.setId(id);
        userImage.setUsername(username);
        userImage.setImageBase64(base64Image);
        imageRepository.save(userImage);

        return photoUrl;
    }

    @Override
    public byte[] getPhoto(String filename) {
        validateFilename(filename);
        Path photoPath = resolveFilePath(filename);

        try {
            if (!Files.exists(photoPath)) {
                throw new ResourceNotFoundException("Photo NOT FOUND: " + filename);
            }
            return Files.readAllBytes(photoPath);
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Photo download failed: " + filename + "\n" + ex);
        }
    }

    @Override
    public List<User> searchUsersByImage(MultipartFile[] base64Image) throws IOException {
        return imageSearchService.searchByImage(base64Image);
    }

    private Path resolveFilePath(String filename) {
        Path targetDirectory = Paths.get(photoDirectory).normalize();
        return targetDirectory.resolve(filename).normalize();
    }

    private void validateFilename(String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename!");
        }
    }

    private final UnaryOperator<String> fileExtension = fileName -> {
        return Optional.ofNullable(fileName)
                .filter(name -> name.contains("."))
                .map(name -> "." + name.substring(name.lastIndexOf(".") + 1))
                .orElse(".png");
    };

    private String generatePhotoUrl(String id, String base64Image) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String fileName = id + fileExtension.apply(id);
            Path fileStorageLocation = Paths.get(photoDirectory).toAbsolutePath().normalize();

            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }

            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.write(targetLocation, imageBytes);

            // Generate the URL for the uploaded photo
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/user/photo/" + fileName)
                    .toUriString();
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to save image", exception);
        }
    }
}
 