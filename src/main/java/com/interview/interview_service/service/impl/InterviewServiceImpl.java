package com.interview.interview_service.service.impl;

import com.interview.interview_service.constants.MessageConstants;
import com.interview.interview_service.dto.request.CreateInterviewRequest;
import com.interview.interview_service.dto.request.FeedbackRequest;
import com.interview.interview_service.dto.request.RescheduleInterviewRequest;
import com.interview.interview_service.dto.request.UpdateInterviewRequest;
import com.interview.interview_service.dto.response.ApiResponse;
import com.interview.interview_service.dto.response.UserResponse;
import com.interview.interview_service.entity.Interview;
import com.interview.interview_service.exception.InvalidInterviewException;
import com.interview.interview_service.exception.ResourceNotFoundException;
import com.interview.interview_service.feign.UserServiceClient;
import com.interview.interview_service.mapper.InterviewMapper;
import com.interview.interview_service.repository.InterviewRepository;
import com.interview.interview_service.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl  implements InterviewService {
    private final InterviewRepository interviewRepository;
    private final InterviewMapper interviewMapper;
    private final UserServiceClient userServiceClient;

    // Fetch Interview by Id
    private Interview getInterview(Long interviewId) {
        return interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(MessageConstants.INTERVIEW_NOT_FOUND));
    }

    //Fetch Candidate from User Service
    private UserResponse getCandidate(Long candidateId) {
        ApiResponse<UserResponse> response =
                userServiceClient.getUserById(candidateId);
        if(response==null || response.getData()==null){
            throw new ResourceNotFoundException
                    (MessageConstants.CANDIDATE_NOT_FOUND);}
        return response.getData();
    }

    private UserResponse getInterviewer(Long interviewerId) {
        ApiResponse<UserResponse> response =
                userServiceClient.getUserById(interviewerId);
        if(response==null || response.getData()==null){
            throw new ResourceNotFoundException
                    (MessageConstants.INTERVIEWER_NOT_FOUND);
        }
        return response.getData();
    }

    private void validateCandidate(UserResponse candidate) {
        if(!candidate.getRole().equals("ROLE_CANDIDATE")) {
                throw new InvalidInterviewException(MessageConstants.INVALID_CANDIDATE);
        }
    }
    private void validateInterviewer(UserResponse interviewer) {
        if(!interviewer.getRole().equals("ROLE_INTERVIEWER")) {
            throw new InvalidInterviewException(MessageConstants.INVALID_INTERVIEWER);
        }
    }

    private void validateInterviewSlot(CreateInterviewRequest request){
        boolean interviewerBusy =
                interviewRepository.existsByInterviewerIdAndInterviewDateAndInterviewTime(
                        request.getInterviewerId(),
                        request.getInterviewDate(),
                        request.getInterviewTime());
        if(interviewerBusy){

        }
    }

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
