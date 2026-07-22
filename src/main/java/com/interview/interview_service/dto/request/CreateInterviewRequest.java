package com.interview.interview_service.dto.request;

import com.interview.interview_service.enums.InterviewMode;
import com.interview.interview_service.enums.InterviewRound;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInterviewRequest {

    @NotNull(message = "Candidate Id is required")
    private Long candidateId;

    @NotNull(message = "Interviewer Id is required")
    private Long interviewerId;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotNull(message = "Interview round is required")
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
