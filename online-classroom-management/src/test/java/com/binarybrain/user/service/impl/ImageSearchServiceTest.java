package com.binarybrain.user.service.impl;

import com.binarybrain.user.model.User;
import com.binarybrain.user.model.UserImage;
import com.binarybrain.user.repository.UserImageRepository;
import com.binarybrain.user.repository.UserRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageSearchServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserImageRepository imageRepository;

    @InjectMocks
    private ImageSearchService imageSearchService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(imageSearchService, "apiDeveloperKey", "test-api-key");
    }

    @Test
    void searchByImage_ScoreAbove() throws JSONException, IOException {
        byte[] imageBytes = "test image".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "photo.jpg", "image/jpeg", imageBytes);
        MultipartFile[] inputImages = new MultipartFile[]{multipartFile};
        String probeBase64 = Base64.getEncoder().encodeToString(imageBytes);

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        UserImage userImage = new UserImage();
        userImage.setImageBase64(probeBase64);
        userImage.setUsername("john");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(imageRepository.findAll()).thenReturn(List.of(userImage));

        JSONObject fakeJson = new JSONObject();
        fakeJson.put("score", 0.80);

        ResponseEntity<String> response = new ResponseEntity<>(fakeJson.toString(), HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);

        List<User> result = imageSearchService.searchByImage(inputImages);

        assertEquals(1, result.size());
        assertEquals("john", result.getFirst().getUsername());
    }

    @Test
    void searchByImage_ScoreBelow() throws JSONException, IOException {
        byte[] imageBytes = "test image".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "photo.jpg", "image/jpeg", imageBytes);
        MultipartFile[] inputImages = new MultipartFile[]{multipartFile};
        String probeBase64 = Base64.getEncoder().encodeToString(imageBytes);

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        UserImage userImage = new UserImage();
        userImage.setImageBase64(probeBase64);
        userImage.setUsername("john");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(imageRepository.findAll()).thenReturn(List.of(userImage));

        JSONObject fakeJson = new JSONObject();
        fakeJson.put("score", 0.30);

        ResponseEntity<String> response = new ResponseEntity<>(fakeJson.toString(), HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);

        List<User> result = imageSearchService.searchByImage(inputImages);

        assertTrue(result.isEmpty(), "Expect no user when score is below threshold");
    }

    @Test
    void searchByImage_InvalidImage() throws JSONException, IOException {
        byte[] imageBytes = "test image".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "photo.jpg", "image/jpeg", imageBytes);
        MultipartFile[] inputImages = new MultipartFile[]{multipartFile};
        String probeBase64 = Base64.getEncoder().encodeToString(imageBytes);

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        UserImage userImage = new UserImage();
        userImage.setImageBase64(probeBase64);
        userImage.setUsername("john");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(imageRepository.findAll()).thenReturn(List.of(userImage));

        JSONObject fakeJson = new JSONObject();
        fakeJson.put("score", 0.30);

        ResponseEntity<String> response = new ResponseEntity<>(fakeJson.toString(), HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);

        assertThrows(IOException.class, () -> imageSearchService.searchByImage(inputImages));
    }


    @Test
    void searchByImage_ThrowsRestTemplateException() throws JSONException, IOException {
        byte[] imageBytes = "test image".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "photo.jpg", "image/jpeg", imageBytes);
        MultipartFile[] inputImages = new MultipartFile[]{multipartFile};
        String probeBase64 = Base64.getEncoder().encodeToString(imageBytes);

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        UserImage userImage = new UserImage();
        userImage.setImageBase64(probeBase64);
        userImage.setUsername("john");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(imageRepository.findAll()).thenReturn(List.of(userImage));

        JSONObject fakeJson = new JSONObject();
        fakeJson.put("score", 0.30);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RestClientException("Rest Client Exception"));

        assertThrows(IOException.class, () -> imageSearchService.searchByImage(inputImages));
    }

    @Test
    void searchByImage_ImageDecodingFailure() throws JSONException, IOException {
        byte[] imageBytes = "test image".getBytes(StandardCharsets.US_ASCII);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "photo.jpg", "image/jpeg", imageBytes);
        String probeBase64 = Base64.getEncoder().encodeToString(imageBytes);

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        UserImage userImage = new UserImage();
        userImage.setImageBase64(probeBase64);
        userImage.setUsername("john");


        JSONObject fakeJson = new JSONObject();
        fakeJson.put("score", 0.30);

        assertThrows(RuntimeException.class, () -> imageSearchService.searchByImage(null));
    }


}