package com.binarybrain.submission.service;

import com.binarybrain.submission.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-MICROSERVICE")
public interface UserService {
    @GetMapping("/api/user/profile")
    UserDto getUserProfile(@RequestHeader("X-User-Username") String username);
}
