package com.interview.interview_service.controller;

import com.interview.interview_service.dto.request.CreateInterviewRequest;
import com.interview.interview_service.dto.request.FeedbackRequest;
import com.interview.interview_service.dto.request.RescheduleInterviewRequest;
import com.interview.interview_service.dto.request.UpdateInterviewRequest;
import com.interview.interview_service.dto.response.ApiResponse;
import com.interview.interview_service.dto.response.InterviewResponse;
import com.interview.interview_service.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<InterviewResponse>> scheduleInterview(
            @Valid @RequestBody CreateInterviewRequest request) {
        return ResponseEntity.ok(interviewService.scheduleInterview(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InterviewResponse>> getInterviewById(
            @PathVariable Long id) {
        return ResponseEntity.ok(interviewService.getInterviewById(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getAllInterviews() {
        return ResponseEntity.ok(interviewService.getAllInterviews());
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getCandidateInterviews(
            @PathVariable Long candidateId) {
        return ResponseEntity.ok(interviewService.getCandidateInterviews(candidateId));
    }

    @GetMapping("/interviewer/{interviewerId}")
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getInterviewerInterviews(
            @PathVariable Long interviewerId) {
        return ResponseEntity.ok(interviewService.getInterviewerInterviews(interviewerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InterviewResponse>> updateInterview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInterviewRequest request) {
        return ResponseEntity.ok(interviewService.updateInterview(id, request));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<ApiResponse<InterviewResponse>> rescheduleInterview(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleInterviewRequest request) {
        return ResponseEntity.ok(interviewService.rescheduleInterview(id, request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<InterviewResponse>> cancelInterview(
            @PathVariable Long id) {
        return ResponseEntity.ok(interviewService.cancelInterview(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<InterviewResponse>> completeInterview(
            @PathVariable Long id) {
        return ResponseEntity.ok(interviewService.completeInterview(id));
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<ApiResponse<InterviewResponse>> addFeedback(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(interviewService.addFeedback(id, request));
    }
}
