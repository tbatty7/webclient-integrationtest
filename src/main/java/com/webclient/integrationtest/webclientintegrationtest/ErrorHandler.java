package com.webclient.integrationtest.webclientintegrationtest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<WokeResponse> handleDmppsException(WebClientResponseException ex) {
        log.error("DMPPS threw error with status: [{}] body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        return getErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "WAKEUP", ex.getMessage());
    }

    private ResponseEntity<WokeResponse> getErrorResponseEntity(HttpStatus statusCode, String statusContext, String message) {
        return ResponseEntity.status(statusCode)
                .body(WokeResponse.builder().error(message + ", context: " + statusContext).build());
    }
}
