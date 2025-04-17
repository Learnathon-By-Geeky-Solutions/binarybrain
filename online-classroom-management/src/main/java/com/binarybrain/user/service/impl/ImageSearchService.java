package com.binarybrain.user.service.impl;

import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.user.model.User;
import com.binarybrain.user.model.UserImage;
import com.binarybrain.user.repository.UserImageRepository;
import com.binarybrain.user.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
@Service
public class ImageSearchService {

    @Value("${opencv.api-key}")
    private String apiDeveloperKey;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final UserImageRepository imageRepository;

    public ImageSearchService(RestTemplate restTemplate, UserRepository userRepository, UserImageRepository imageRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public List<User> searchByImage(MultipartFile[] image) throws IOException {
        List<String> imageBase64 = convertToBase64List(image);

        HttpHeaders headers = buildHeader();

        List<User> userList = userRepository.findAll().stream().toList();
        List<UserImage> userImageList = getAllUserImage64();
        List<User> predictPersons = new ArrayList<>();

        int i=0;
        for(UserImage userImage: userImageList){
            String dbImage64 = userImage.getImageBase64();

            Map<String, Object> requestBody = buildJsonPayload(imageBase64, dbImage64);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            double matchingScore = callOpenCvApi(entity);

            if(matchingScore >= 0.75) {
                predictPersons.add(userList.get(i));
            }
            ++i;
        }
        return predictPersons;
    }

    private HttpHeaders buildHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiDeveloperKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        return headers;
    }
    
    private Map<String, Object> buildJsonPayload(List<String> imageBase64, String dbImage64){
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("gallery", Collections.singletonList(dbImage64));
        requestBody.put("probe", imageBase64);
        requestBody.put("search_mode", "FAST");
        return requestBody;
    }

    private double callOpenCvApi(HttpEntity<Map<String, Object>> entity) throws IOException {
        String url = "https://sg.opencv.fr/compare";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = new JSONObject(response.getBody());
            return json.getDouble("score");
        } else {
            throw new IOException("Failed to search! " + response.getStatusCode());
        }
    }

    public List<UserImage> getAllUserImage64() throws ResourceNotFoundException {
        List<UserImage> allUsersImage = imageRepository.findAll();
        return allUsersImage.stream()
                .toList();
    }

    private List<String> convertToBase64List(MultipartFile[] images){
        return Arrays.stream(images)
                .map(image -> {
                    try {
                        return java.util.Base64.getEncoder().encodeToString(image.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to encode image", e);
                    }
                })
                .toList();
    }

}
