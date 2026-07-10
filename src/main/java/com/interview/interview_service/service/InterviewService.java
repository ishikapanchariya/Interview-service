package com.interview.interview_service.service;

import com.interview.interview_service.dto.request.CreateInterviewRequest;
import com.interview.interview_service.dto.request.FeedbackRequest;
import com.interview.interview_service.dto.request.RescheduleInterviewRequest;
import com.interview.interview_service.dto.request.UpdateInterviewRequest;
import com.interview.interview_service.dto.response.ApiResponse;

import java.util.List;

public interface InterviewService {

    ApiResponse<InterviewService> scheduleInterview(CreateInterviewRequest request);
    ApiResponse<InterviewService> getInterviewById(Long interviewId);
    ApiResponse<List<InterviewService>> getAllInterviews();
    ApiResponse<List<InterviewService>> getCandidateInterviews(Long candidateId);
    ApiResponse<List<InterviewService>> getInterviewerInterviews(Long interviewerId);
    ApiResponse<InterviewService> updateInterview(Long InterviewerId, UpdateInterviewRequest request);
    ApiResponse<InterviewService>rescheduleInterview(Long InterviewerId, RescheduleInterviewRequest request);
    ApiResponse<InterviewService> cancelInterview(Long InterviewerId);
    ApiResponse<InterviewService>completeInterview(Long InterviewerId);
    ApiResponse<InterviewService>addFeedback(Long InterviewerId, FeedbackRequest  request);

}
