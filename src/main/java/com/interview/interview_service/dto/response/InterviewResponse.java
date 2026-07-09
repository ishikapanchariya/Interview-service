package com.interview.interview_service.dto.response;

import com.interview.interview_service.enums.InterviewRound;
import com.interview.interview_service.enums.InterviewStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewResponse {
    private Long id;
    private UserResponse candidate;
    private UserResponse interviewer;
    private String jobTitle;
    private InterviewRound round;
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private String meetingLink;
    private InterviewStatus status;
    private String feedback;
    private String remarks;
}
