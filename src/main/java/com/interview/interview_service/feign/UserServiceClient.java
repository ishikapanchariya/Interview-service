package com.interview.interview_service.feign;

import com.interview.interview_service.config.FeignConfig;
import com.interview.interview_service.dto.response.ApiResponse;
import com.interview.interview_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service",
        url = "${user.service.url}",
configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/api/v1/internal/users/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable Long id);
}