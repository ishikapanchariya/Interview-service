package com.interview.interview_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackRequest {
    @NotBlank(message = "Feedback cannot be empty")
    private String feedback;
}