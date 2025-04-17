package com.binarybrain.user.service.impl;

import com.binarybrain.user.model.User;
import com.binarybrain.user.model.UserImage;
import com.binarybrain.user.repository.UserImageRepository;
import com.binarybrain.user.repository.UserRepository;
import com.binarybrain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserImageServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserImageRepository imageRepository;

    @Mock
    private UserService userService;

    @Mock
    private ImageSearchService imageSearchService;

    @InjectMocks
    private UserImageServiceImpl userImageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userImageService, "photoDirectory", tempDir.toString() + "/");
    }
    @Test
    void uploadPhoto() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/api");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Long userId = 1L;
        String username = "testuser";
        String imageContent = "test image";
        MultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", imageContent.getBytes());

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername(username);

        when(userService.getUserProfileById(userId, username)).thenReturn(mockUser);

        String result = userImageService.uploadPhoto(userId, file, username);

        assertTrue(result.contains("/api/user/photo/"));
        verify(userRepository).save(any(User.class));
        verify(imageRepository).save(any(UserImage.class));
    }

    @Test
    void getPhoto() throws IOException {
        String fileName = "sample.png";
        Path filePath = Paths.get(tempDir.toString(), fileName);
        byte[] sampleBytes = "dummy".getBytes();
        Files.write(filePath, sampleBytes);

        ReflectionTestUtils.setField(userImageService, "photoDirectory", tempDir.toString() + "/");

        byte[] result = userImageService.getPhoto(fileName);

        assertArrayEquals(sampleBytes, result);
    }

    @Test
    void searchUsersByImage() throws IOException {
        MultipartFile[] images = {
                new MockMultipartFile("image", "img1.png", "image/png", "dummy data".getBytes())
        };

        List<User> users = List.of(new User());
        when(imageSearchService.searchByImage(images)).thenReturn(users);

        List<User> result = userImageService.searchUsersByImage(images);

        assertEquals(1, result.size());
        verify(imageSearchService).searchByImage(images);
    }
}