package com.binaryBrain.classroom_microservice.service;

import com.binaryBrain.classroom_microservice.dto.CourseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "Course-Microservice", url="http://localhost:5003/")
public interface CourseService {
    @GetMapping("/api/v1/private/course/{id}")
    CourseDto getCourseById(@PathVariable Long id,
                            @RequestHeader("Authorization") String jwt);

}
