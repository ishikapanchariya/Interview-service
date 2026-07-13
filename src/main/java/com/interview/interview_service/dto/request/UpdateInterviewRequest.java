package com.interview.interview_service.dto.request;

import com.interview.interview_service.enums.InterviewMode;
import com.interview.interview_service.enums.InterviewRound;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInterviewRequest {

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotBlank(message = "Interview round is required")
    private InterviewRound round;

    @NotNull(message = "Interview date is required")
    @Future(message = "Interview date must be in the future")
    private LocalDate interviewDate;

    @NotNull(message = "Interview time is required")
    private LocalTime interviewTime;

    @NotNull(message = "Interview mode is required")
    private InterviewMode interviewMode;

    private String meetingLink;
    private String remarks;
}
