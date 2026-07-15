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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {
    private final InterviewRepository interviewRepository;
    private final InterviewMapper interviewMapper;
    private final UserServiceClient userServiceClient;

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
            // ignore
        }
        try {
            response.setInterviewer(getInterviewer(updatedInterview.getInterviewerId()));
        } catch (Exception e) {
            // ignore
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
}
