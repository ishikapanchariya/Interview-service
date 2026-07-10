package com.interview.interview_service.service.impl;

import com.interview.interview_service.dto.request.CreateInterviewRequest;
import com.interview.interview_service.dto.request.FeedbackRequest;
import com.interview.interview_service.dto.request.RescheduleInterviewRequest;
import com.interview.interview_service.dto.request.UpdateInterviewRequest;
import com.interview.interview_service.dto.response.ApiResponse;
import com.interview.interview_service.service.InterviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewServiceImpl  implements InterviewService {

    @Override
    public ApiResponse<InterviewService> scheduleInterview
            (CreateInterviewRequest request) {

        return null;
    }

    @Override
    public ApiResponse<InterviewService> getInterviewById(Long interviewId) {
        return null;
    }

    @Override
    public ApiResponse<List<InterviewService>> getAllInterviews() {
        return null;
    }

    @Override
    public ApiResponse<List<InterviewService>> getCandidateInterviews(Long candidateId) {
        return null;
    }

    @Override
    public ApiResponse<List<InterviewService>> getInterviewerInterviews(Long interviewerId) {
        return null;
    }

    @Override
    public ApiResponse<InterviewService> updateInterview(Long InterviewerId, UpdateInterviewRequest request) {
        return null;
    }

    @Override
    public ApiResponse<InterviewService> rescheduleInterview(Long InterviewerId, RescheduleInterviewRequest request) {
        return null;
    }

    @Override
    public ApiResponse<InterviewService> cancelInterview(Long InterviewerId) {
        return null;
    }

    @Override
    public ApiResponse<InterviewService> completeInterview(Long InterviewerId) {
        return null;
    }

    @Override
    public ApiResponse<InterviewService> addFeedback(Long InterviewerId, FeedbackRequest request) {
        return null;
    }
}
