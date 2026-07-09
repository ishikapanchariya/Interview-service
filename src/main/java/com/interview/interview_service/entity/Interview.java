package com.interview.interview_service.entity;

import com.interview.interview_service.enums.InterviewMode;
import com.interview.interview_service.enums.InterviewRound;
import com.interview.interview_service.enums.InterviewStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "interviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long candidateId;

    @Column(nullable = false)
    private Long interviewerId;

    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private LocalDate interviewDate;

    @Column(nullable = false)
    private LocalTime interviewTime;

    private String meetingLink;

    @Column(nullable = false)
    private InterviewRound round;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Interview mode is required")
    private InterviewMode interviewMode;

    @Column(length = 200)
    private String feedback;

    @Column(length = 200)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
