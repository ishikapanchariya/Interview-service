package com.interview.interview_service.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.interview.interview_service.dto.response.ApiResponse;
import com.interview.interview_service.exception.InvalidInterviewException;
import com.interview.interview_service.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();
    private final ObjectMapper objectMapper;

    public CustomFeignErrorDecoder() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "Feign communication error";

        if (response.body() != null) {
            try (InputStream bodyIs = response.body().asInputStream()) {
                ApiResponse<?> apiResponse = objectMapper.readValue(bodyIs, ApiResponse.class);
                if (apiResponse != null && apiResponse.getMessage() != null) {
                    message = apiResponse.getMessage();
                }
            } catch (IOException e) {
                log.warn("Failed to parse Feign error response body", e);
            }
        }

        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new ResourceNotFoundException(message);
        } else if (response.status() == HttpStatus.BAD_REQUEST.value()) {
            return new InvalidInterviewException(message);
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}
