package com.binarybrain.submission.service;

import com.binarybrain.submission.dto.TaskDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "TASK-MICROSERVICE", url="${task-service.url}")
public interface TaskService {
    @GetMapping("/api/v1/private/task/{id}")
    TaskDto getTaskById(@PathVariable Long id,
                        @RequestHeader("X-User-Username") String username);

}
