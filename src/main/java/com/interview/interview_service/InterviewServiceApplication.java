package com.interview.interview_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class InterviewServiceApplication {

	@Value("${user.service.url}")
	private String userServiceUrl;

	public static void main(String[] args) {
		SpringApplication.run(InterviewServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(){
		return args ->
				System.out.println("User Service URL "+userServiceUrl);
	}
}

//https://github.com/ishikapanchariya/Interview-service.git