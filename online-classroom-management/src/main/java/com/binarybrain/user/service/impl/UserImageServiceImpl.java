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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
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
        UserImage userImage = new UserImage();

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String photoUrl = photoFunction.apply(id.toString(), base64Image);

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
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename!");
        }
        try{
            Path targetDirectory = Paths.get(photoDirectory).normalize();
            Path photoPath = targetDirectory.resolve(filename).normalize();

            if (!photoPath.startsWith(targetDirectory)) {
                throw new IOException("Entry is outside of the target directory");
            }
            if(!Files.exists(photoPath)){
                throw new ResourceNotFoundException("Photo NOT FOUND: " + filename);
            }
            return Files.readAllBytes(photoPath);
        } catch (IOException ex){
            throw new ResourceNotFoundException("Photo download failed: "+ filename + "\n" + ex);
        }
    }

    @Override
    public List<User> searchUsersByImage(MultipartFile[] base64Image) throws IOException {
        return imageSearchService.searchByImage(base64Image);
    }

    private final Function<String, String> fileExtension = fileName -> Optional.of(fileName).filter(name -> name.contains(".")).
            map(name -> "." + name.substring(fileName.lastIndexOf(".") + 1)).orElse(".png");


    private final BiFunction<String, String, String> photoFunction = (id, base64Image) -> {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            String fileName = id + fileExtension.apply(id);

            Path fileStorageLocation = Paths.get(photoDirectory).toAbsolutePath().normalize();

            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.write(targetLocation, imageBytes);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/user/photo/" + fileName).toUriString();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to save image", exception);
        }
    };
}
