package com.binaryBrain.taskSubmission.service;

import com.binaryBrain.taskSubmission.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "OCM-USER-REGISTRATION", url="http://localhost:5001/")
public interface UserService {
    @GetMapping("/api/user/profile")
    public UserDto getUserProfile(@RequestHeader("X-User-Username") String username);
}
