package com.interview.interview_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InterviewServiceApplication {
	@Value("${user.service.url}")
	private String userServiceUrl;

	public static void main(String[] args) {
		SpringApplication.run(InterviewServiceApplication.class, args);
	}

	CommandLineRunner runner(){
		return args ->
				System.out.println("User Service URL "+userServiceUrl);
	}
}

//https://github.com/ishikapanchariya/Interview-service.git