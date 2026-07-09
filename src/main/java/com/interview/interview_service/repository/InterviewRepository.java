package com.interview.interview_service.repository;

import com.interview.interview_service.entity.Interview;
import com.interview.interview_service.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByInterviewerId(Long interviewerId);

    List<Interview> findByCandidateId(Long candidateId);

    List<Interview> findByStatus(InterviewStatus status);

    boolean existsByInterviewerIdAndInterviewDateAndInterviewTime
            (Long interviewerId,
             LocalDate interviewDate, LocalTime interviewTime);

    boolean existsByCandidateIdAndInterviewDateAndInterviewTime
            (Long candidateId,
             LocalDate interviewDate, LocalTime interviewTime);
}
