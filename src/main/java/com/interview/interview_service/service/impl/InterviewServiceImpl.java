package com.interview.interview_service.service.impl;

import com.interview.interview_service.constants.MessageConstants;
import com.interview.interview_service.dto.request.CreateInterviewRequest;
import com.interview.interview_service.dto.request.FeedbackRequest;
import com.interview.interview_service.dto.request.RescheduleInterviewRequest;
import com.interview.interview_service.dto.request.UpdateInterviewRequest;
import com.interview.interview_service.dto.response.ApiResponse;
import com.interview.interview_service.dto.response.InterviewResponse;
import com.interview.interview_service.dto.response.UserResponse;
import com.interview.interview_service.entity.Interview;
import com.interview.interview_service.enums.InterviewStatus;
import com.interview.interview_service.exception.InvalidInterviewException;
import com.interview.interview_service.exception.ResourceNotFoundException;
import com.interview.interview_service.feign.UserServiceClient;
import com.interview.interview_service.mapper.InterviewMapper;
import com.interview.interview_service.repository.InterviewRepository;
import com.interview.interview_service.service.InterviewService;
import com.interview.interview_service.dto.request.SendNotificationRequest;
import com.interview.interview_service.enums.NotificationType;
import com.interview.interview_service.feign.NotificationServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {
    private final InterviewRepository interviewRepository;
    private final InterviewMapper interviewMapper;
    private final UserServiceClient userServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    // Fetch Interview by Id
    private Interview getInterview(Long interviewId) {
        return interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(MessageConstants.INTERVIEW_NOT_FOUND));
    }

    // Fetch Candidate from User Service
    private UserResponse getCandidate(Long candidateId) {
        ApiResponse<UserResponse> response =
                userServiceClient.getUserById(candidateId);
        if (response == null || response.getData() == null) {
            throw new ResourceNotFoundException(MessageConstants.CANDIDATE_NOT_FOUND);
        }
        return response.getData();
    }

    // Fetch Interviewer from User Service
    private UserResponse getInterviewer(Long interviewerId) {
        ApiResponse<UserResponse> response =
                userServiceClient.getUserById(interviewerId);
        if (response == null || response.getData() == null) {
            throw new ResourceNotFoundException(MessageConstants.INTERVIEWER_NOT_FOUND);
        }
        return response.getData();
    }

    private void validateCandidate(UserResponse candidate) {
        if (!candidate.getRole().equals("ROLE_CANDIDATE")) {
            throw new InvalidInterviewException(MessageConstants.INVALID_CANDIDATE);
        }
    }

    private void validateInterviewer(UserResponse interviewer) {
        if (!interviewer.getRole().equals("ROLE_INTERVIEWER")) {
            throw new InvalidInterviewException(MessageConstants.INVALID_INTERVIEWER);
        }
    }

    private void validateInterviewSlot(Long interviewerId, LocalDate date, LocalTime time) {
        boolean interviewerBusy =
                interviewRepository.existsByInterviewerIdAndInterviewDateAndInterviewTime(
                        interviewerId,
                        date,
                        time);
        if (interviewerBusy) {
            throw new InvalidInterviewException(MessageConstants.INTERVIEW_SLOT_ALREADY_BOOKED);
        }
    }

    @Override
    @Transactional
    public ApiResponse<InterviewResponse> scheduleInterview(CreateInterviewRequest request) {
        UserResponse candidate = getCandidate(request.getCandidateId());
        validateCandidate(candidate);

        UserResponse interviewer = getInterviewer(request.getInterviewerId());
        validateInterviewer(interviewer);

        validateInterviewSlot(request.getInterviewerId(), request.getInterviewDate(), request.getInterviewTime());

        Interview interview = interviewMapper.toEntity(request);
        interview.setInterviewMode(request.getInterviewMode());
        interview.setStatus(InterviewStatus.SCHEDULED);
        Interview savedInterview = interviewRepository.save(interview);

        InterviewResponse response = interviewMapper.toResponse(savedInterview);
        response.setCandidate(candidate);
        response.setInterviewer(interviewer);

        // Trigger notifications for Candidate & Interviewer
        triggerNotification(candidate != null ? candidate.getId() : null, candidate != null ? candidate.getEmail() : null,
                "Interview Scheduled: " + request.getJobTitle(),
                "Your " + request.getRound() + " interview is scheduled on " + request.getInterviewDate() + " at " + request.getInterviewTime(),
                NotificationType.EMAIL);

        triggerNotification(candidate != null ? candidate.getId() : null, candidate != null ? candidate.getEmail() : null,
                "Interview Scheduled",
                "New interview scheduled for " + request.getJobTitle() + " on " + request.getInterviewDate(),
                NotificationType.IN_APP);

        if (interviewer != null) {
            triggerNotification(interviewer.getId(), interviewer.getEmail(),
                    "Assigned Interview: " + request.getJobTitle(),
                    "You have been assigned an interview for " + request.getJobTitle() + " on " + request.getInterviewDate() + " at " + request.getInterviewTime(),
                    NotificationType.EMAIL);
        }

        return ApiResponse.<InterviewResponse>builder()
                .success(true)
                .message(MessageConstants.INTERVIEW_SCHEDULED_SUCCESS)
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public ApiResponse<InterviewResponse> getInterviewById(Long interviewId) {
        Interview interview = getInterview(interviewId);

        UserResponse candidate = null;
        try {
            candidate = getCandidate(interview.getCandidateId());
        } catch (Exception e) {
            // Keep candidate null if fetch fails or user is deleted
        }

        UserResponse interviewer = null;
        try {
            interviewer = getInterviewer(interview.getInterviewerId());
        } catch (Exception e) {
            // Keep interviewer null if fetch fails
        }

        InterviewResponse response = interviewMapper.toResponse(interview);
        response.setCandidate(candidate);
        response.setInterviewer(interviewer);

        return ApiResponse.<InterviewResponse>builder()
                .success(true)
                .message("Interview retrieved successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public ApiResponse<List<InterviewResponse>> getAllInterviews() {
        List<Interview> interviews = interviewRepository.findAll();
        List<InterviewResponse> responseList = interviews.stream()
                .map(interview -> {
                    InterviewResponse res = interviewMapper.toResponse(interview);
                    try {
                        res.setCandidate(getCandidate(interview.getCandidateId()));
                    } catch (Exception e) {
                        // ignore
                    }
                    try {
                        res.setInterviewer(getInterviewer(interview.getInterviewerId()));
                    } catch (Exception e) {
                        // ignore
                    }
                    return res;
                })
                .toList();

        return ApiResponse.<List<InterviewResponse>>builder()
                .success(true)
                .message("All interviews retrieved successfully")
                .data(responseList)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public ApiResponse<List<InterviewResponse>> getCandidateInterviews(Long candidateId) {
        List<Interview> interviews = interviewRepository.findByCandidateId(candidateId);
        List<InterviewResponse> responseList = interviews.stream()
                .map(interview -> {
                    InterviewResponse res = interviewMapper.toResponse(interview);
                    try {
                        res.setCandidate(getCandidate(interview.getCandidateId()));
                    } catch (Exception e) {
                        // ignore
                    }
                    try {
                        res.setInterviewer(getInterviewer(interview.getInterviewerId()));
                    } catch (Exception e) {
                        // ignore
                    }
                    return res;
                })
                .toList();

        return ApiResponse.<List<InterviewResponse>>builder()
                .success(true)
                .message("Candidate interviews retrieved successfully")
                .data(responseList)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public ApiResponse<List<InterviewResponse>> getInterviewerInterviews(Long interviewerId) {
        List<Interview> interviews = interviewRepository.findByInterviewerId(interviewerId);
        List<InterviewResponse> responseList = interviews.stream()
                .map(interview -> {
                    InterviewResponse res = interviewMapper.toResponse(interview);
                    try {
                        res.setCandidate(getCandidate(interview.getCandidateId()));
                    } catch (Exception e) {
                        // ignore
                    }
                    try {
                        res.setInterviewer(getInterviewer(interview.getInterviewerId()));
                    } catch (Exception e) {
                        // ignore
                    }
                    return res;
                })
                .toList();

        return ApiResponse.<List<InterviewResponse>>builder()
                .success(true)
                .message("Interviewer interviews retrieved successfully")
                .data(responseList)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<InterviewResponse> updateInterview(Long interviewId, UpdateInterviewRequest request) {
        Interview interview = getInterview(interviewId);

        interviewMapper.updateInterviewFromEntity(request, interview);
        interview.setInterviewMode(request.getInterviewMode());
        Interview updatedInterview = interviewRepository.save(interview);

        InterviewResponse response = interviewMapper.toResponse(updatedInterview);
        try {
            response.setCandidate(getCandidate(updatedInterview.getCandidateId()));
        } catch (Exception e) {
            // ignore
        }
        try {
            response.setInterviewer(getInterviewer(updatedInterview.getInterviewerId()));
        } catch (Exception e) {
            // ignore
        }

        return ApiResponse.<InterviewResponse>builder()
                .success(true)
                .message(MessageConstants.INTERVIEW_UPDATED_SUCCESS)
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<InterviewResponse> rescheduleInterview(Long interviewId, RescheduleInterviewRequest request) {
        Interview interview = getInterview(interviewId);

        validateInterviewSlot(interview.getInterviewerId(), request.getInterviewDate(), request.getInterviewTime());

        interview.setInterviewDate(request.getInterviewDate());
        interview.setInterviewTime(request.getInterviewTime());
        interview.setStatus(InterviewStatus.RESCHEDULED);
        Interview updatedInterview = interviewRepository.save(interview);

        InterviewResponse response = interviewMapper.toResponse(updatedInterview);
        try {
            response.setCandidate(getCandidate(updatedInterview.getCandidateId()));
        } catch (Exception e) {
            // ignore
        }
        try {
            response.setInterviewer(getInterviewer(updatedInterview.getInterviewerId()));
        } catch (Exception e) {
            // ignore
        }

        // Trigger reschedule notification
        if (response.getCandidate() != null) {
            triggerNotification(response.getCandidate().getId(), response.getCandidate().getEmail(),
                    "Interview Rescheduled",
                    "Your interview slot has been rescheduled to " + request.getInterviewDate() + " at " + request.getInterviewTime(),
                    NotificationType.EMAIL);
            triggerNotification(response.getCandidate().getId(), response.getCandidate().getEmail(),
                    "Interview Rescheduled",
                    "Interview rescheduled to " + request.getInterviewDate(),
                    NotificationType.IN_APP);
        }

        return ApiResponse.<InterviewResponse>builder()
                .success(true)
                .message("Interview rescheduled successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<InterviewResponse> cancelInterview(Long interviewId) {
        Interview interview = getInterview(interviewId);
        interview.setStatus(InterviewStatus.CANCELLED);
        Interview updatedInterview = interviewRepository.save(interview);

        InterviewResponse response = interviewMapper.toResponse(updatedInterview);
        try {
            response.setCandidate(getCandidate(updatedInterview.getCandidateId()));
        } catch (Exception e) {
            // ignore
        }
        try {
            response.setInterviewer(getInterviewer(updatedInterview.getInterviewerId()));
        } catch (Exception e) {
            // ignore
        }

        // Trigger cancellation notification
        if (response.getCandidate() != null) {
            triggerNotification(response.getCandidate().getId(), response.getCandidate().getEmail(),
                    "Interview Cancelled",
                    "Your scheduled interview has been cancelled.",
                    NotificationType.EMAIL);
            triggerNotification(response.getCandidate().getId(), response.getCandidate().getEmail(),
                    "Interview Cancelled",
                    "Interview session was cancelled.",
                    NotificationType.IN_APP);
        }

        return ApiResponse.<InterviewResponse>builder()
                .success(true)
                .message(MessageConstants.INTERVIEW_CANCELLED_SUCCESS)
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<InterviewResponse> completeInterview(Long interviewId) {
        Interview interview = getInterview(interviewId);
        interview.setStatus(InterviewStatus.COMPLETED);
        Interview updatedInterview = interviewRepository.save(interview);

        InterviewResponse response = interviewMapper.toResponse(updatedInterview);
        try {
            response.setCandidate(getCandidate(updatedInterview.getCandidateId()));
        } catch (Exception e) {
            // jane de
        }
        try {
            response.setInterviewer(getInterviewer(updatedInterview.getInterviewerId()));
        } catch (Exception e) {
            // jane de
        }

        return ApiResponse.<InterviewResponse>builder()
                .success(true)
                .message(MessageConstants.INTERVIEW_COMPLETED_SUCCESS)
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<InterviewResponse> addFeedback(Long interviewId, FeedbackRequest request) {
        Interview interview = getInterview(interviewId);
        interview.setFeedback(request.getFeedback());
        Interview updatedInterview = interviewRepository.save(interview);

        InterviewResponse response = interviewMapper.toResponse(updatedInterview);
        try {
            response.setCandidate(getCandidate(updatedInterview.getCandidateId()));
        } catch (Exception e) {
            // ignore
        }
        try {
            response.setInterviewer(getInterviewer(updatedInterview.getInterviewerId()));
        } catch (Exception e) {
            // ignore
        }

        return ApiResponse.<InterviewResponse>builder()
                .success(true)
                .message(MessageConstants.FEEDBACK_ADDED_SUCCESS)
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void triggerNotification(Long userId, String recipient, String subject, String message, NotificationType type) {
        if (recipient == null && userId == null) return;
        try {
            notificationServiceClient.sendNotification(SendNotificationRequest.builder()
                    .userId(userId)
                    .recipient(recipient)
                    .subject(subject)
                    .message(message)
                    .type(type)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to send {} notification to {}: {}", type, recipient != null ? recipient : userId, e.getMessage());
        }
    }
}
