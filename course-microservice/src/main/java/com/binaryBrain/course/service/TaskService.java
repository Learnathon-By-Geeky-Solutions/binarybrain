package com.binaryBrain.course.service;

import com.binaryBrain.course.dto.TaskDto;
import com.binaryBrain.course.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "TASK-MICROSERVICE", url="http://localhost:5004/")
public interface TaskService {
    @GetMapping("/api/v1/private/task/{id}")
    TaskDto getTaskById(@PathVariable Long id,
                        @RequestHeader("X-User-Username") String username);

    @GetMapping("/api/v1/private/task/by-ids")
    List<TaskDto> getTasksByIds(@RequestParam List<Long> taskIds,
                                @RequestHeader("X-User-Username") String username);
}
