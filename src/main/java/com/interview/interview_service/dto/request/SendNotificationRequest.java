package com.interview.interview_service.dto.request;

import com.interview.interview_service.enums.NotificationType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationRequest {
    private Long userId;
    private String recipient;
    private String subject;
    private String message;
    private NotificationType type;
}
