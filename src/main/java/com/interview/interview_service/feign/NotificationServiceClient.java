package com.interview.interview_service.feign;

import com.interview.interview_service.config.FeignConfig;
import com.interview.interview_service.dto.request.SendNotificationRequest;
import com.interview.interview_service.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${notification.service.url}", configuration = FeignConfig.class)
public interface NotificationServiceClient {

    @PostMapping("/api/v1/notifications/send")
    ApiResponse<Object> sendNotification(@RequestBody SendNotificationRequest request);
}
