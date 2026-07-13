package com.interview.interview_service.service;

import com.interview.interview_service.dto.request.CreateInterviewRequest;
import com.interview.interview_service.dto.request.FeedbackRequest;
import com.interview.interview_service.dto.request.RescheduleInterviewRequest;
import com.interview.interview_service.dto.request.UpdateInterviewRequest;
import com.interview.interview_service.dto.response.ApiResponse;
import com.interview.interview_service.dto.response.InterviewResponse;

import java.util.List;

public interface InterviewService {

    ApiResponse<InterviewResponse> scheduleInterview(CreateInterviewRequest request);
    ApiResponse<InterviewResponse> getInterviewById(Long interviewId);
    ApiResponse<List<InterviewResponse>> getAllInterviews();
    ApiResponse<List<InterviewResponse>> getCandidateInterviews(Long candidateId);
    ApiResponse<List<InterviewResponse>> getInterviewerInterviews(Long interviewerId);
    ApiResponse<InterviewResponse> updateInterview(Long interviewId, UpdateInterviewRequest request);
    ApiResponse<InterviewResponse> rescheduleInterview(Long interviewId, RescheduleInterviewRequest request);
    ApiResponse<InterviewResponse> cancelInterview(Long interviewId);
    ApiResponse<InterviewResponse> completeInterview(Long interviewId);
    ApiResponse<InterviewResponse> addFeedback(Long interviewId, FeedbackRequest request);

}
