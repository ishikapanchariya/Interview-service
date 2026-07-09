package com.interview.interview_service.mapper;

import com.interview.interview_service.dto.request.CreateInterviewRequest;
import com.interview.interview_service.dto.response.InterviewResponse;
import com.interview.interview_service.entity.Interview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterviewMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "status",ignore = true)
    @Mapping(target = "feedback",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    Interview toEntity(CreateInterviewRequest request);

    InterviewResponse toResponse(Interview interview);
}
