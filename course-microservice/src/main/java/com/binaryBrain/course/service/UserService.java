package com.binaryBrain.course.service;

import com.binaryBrain.course.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "OCM-USER-REGISTRATION", url="http://localhost:5001/")
public interface UserService {
    @GetMapping("/api/user/profile")
    UserDto getUserProfile(@RequestHeader("X-User-Username") String username);

    @GetMapping("/api/user/profile/{id}")
    UserDto getUserProfileById(@PathVariable Long id,
                               @RequestHeader("X-User-Username") String username);
}