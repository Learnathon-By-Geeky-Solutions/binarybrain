package com.binarybrain.classroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
		info = @Info(title = "Classroom Microservice", version = "1.0.0"),
		servers = @Server(url = "http://localhost:5000")
)
@SpringBootApplication
@EnableFeignClients
public class ClassroomMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClassroomMicroserviceApplication.class, args);
	}

}
