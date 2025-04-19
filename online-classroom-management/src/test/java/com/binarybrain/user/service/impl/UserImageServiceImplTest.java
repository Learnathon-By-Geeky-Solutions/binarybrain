package com.binarybrain.user.service.impl;

import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.user.model.User;
import com.binarybrain.user.model.UserImage;
import com.binarybrain.user.repository.UserImageRepository;
import com.binarybrain.user.repository.UserRepository;
import com.binarybrain.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;
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
    @InjectMocks
    private UserImageServiceImpl userImageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserImageRepository userImageRepository;

    @Mock
    private UserService userService;

    @Mock
    private ImageSearchService imageSearchService;

    @Mock
    private MultipartFile multipartFile;

    private final String testUploadDir = "test-user-uploads";
    private final String username = "testuser";
    private final Long userId = 1L;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(userImageService, "photoDirectory", testUploadDir);
        Path dirPath = Paths.get(testUploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Files.write(dirPath.resolve(userId + ".png"), "fake image".getBytes());
    }

    @AfterEach
    void tearDown() throws IOException {
        FileSystemUtils.deleteRecursively(Paths.get(testUploadDir));
    }

    @Test
    void uploadPhoto() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/api");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String imageContent = "test image";
        MultipartFile file = new MockMultipartFile("file", "photo.png", "image/jpeg", imageContent.getBytes());

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername(username);

        when(userService.getUserProfileById(userId, username)).thenReturn(mockUser);

        String result = userImageService.uploadPhoto(userId, file, username);

        assertTrue(result.contains("/api/user/photo/"));
        verify(userRepository).save(any(User.class));
        verify(userImageRepository).save(any(UserImage.class));
    }

    @Test
    void getPhoto_ShouldReturnBytes_WhenFileExists() {
        byte[] result = userImageService.getPhoto(userId + ".png");
        assertArrayEquals("fake image".getBytes(), result);
    }

    @Test
    void getPhoto_ShouldThrowException_WhenPathTraversalAttempted() {
        assertThrows(IllegalArgumentException.class, () -> userImageService.getPhoto("../evil.png"));
    }

    @Test
    void getPhoto_ShouldThrowException_WhenFileDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> userImageService.getPhoto("not-found.png"));
    }

    @Test
    void searchUsersByImage_ShouldReturnMatchingUsers() throws Exception {
        MultipartFile[] files = new MultipartFile[] { multipartFile };
        List<User> users = List.of(new User(), new User());

        when(imageSearchService.searchByImage(files)).thenReturn(users);

        List<User> result = userImageService.searchUsersByImage(files);

        assertEquals(2, result.size());
        verify(imageSearchService).searchByImage(files);
    }
}