package com.binaryBrain.classroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ClassroomMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClassroomMicroserviceApplication.class, args);
	}

}
