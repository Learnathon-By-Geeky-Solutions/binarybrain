package com.binaryBrain.classroom_microservice.service;

import com.binaryBrain.classroom_microservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "OCM-USER-REGISTRATION", url="http://localhost:5001/")
public interface UserService {
    @GetMapping("/api/user/profile")
    public UserDto getUserProfile(@RequestHeader("X-User-Username") String username);

    @GetMapping("/api/user/profile/{id}")
    public UserDto getUserProfileById(@PathVariable Long id,
                                      @RequestHeader("X-User-Username") String username);
}
