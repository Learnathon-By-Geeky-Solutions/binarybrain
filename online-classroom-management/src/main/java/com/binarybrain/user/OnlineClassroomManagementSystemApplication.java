package com.binarybrain.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
		info = @Info(title = "User Microservice", version = "1.0.0"),
		servers = {
				@Server(url = "https://binarybrains.gentlesmoke-d65a2350.westus2.azurecontainerapps.io/"),
				@Server(url = "http://localhost:5000")
		}
)
@SpringBootApplication
public class OnlineClassroomManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineClassroomManagementSystemApplication.class, args);
	}

}
