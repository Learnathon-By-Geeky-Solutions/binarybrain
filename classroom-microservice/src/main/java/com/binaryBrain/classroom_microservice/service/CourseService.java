package com.binaryBrain.classroom_microservice.service;

import com.binaryBrain.classroom_microservice.dto.CourseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "COURSE-MICROSERVICE", url="${course-service.url}")
public interface CourseService {
    @GetMapping("/api/v1/private/course/{id}")
    CourseDto getCourseById(@PathVariable Long id,
                            @RequestHeader("X-User-Username") String username);

    @GetMapping("/api/v1/private/course/by-ids")
    List<CourseDto> getCoursesByIds(@RequestParam List<Long> courseIds,
                                    @RequestHeader("X-User-Username") String username);

}
