package com.binaryBrain.task_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class TaskMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskMicroserviceApplication.class, args);
	}

}
