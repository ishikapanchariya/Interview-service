package com.interview.interview_service.dto.request;

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

    @NotBlank(message = "interview round is required")
    private InterviewRound round;

    @NotNull(message = "Interview date is required")
    @Future(message = "Interview date must be in the future")
    //Prevents scheduling interviews in the past
    private LocalDate interviewDate;

    @NotNull(message = "Interview time is required")
    private LocalTime interviewTime;

    private String meetingLink;

    private String remarks;
}
