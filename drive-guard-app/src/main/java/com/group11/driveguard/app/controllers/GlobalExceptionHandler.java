package com.group11.driveguard.app.controllers;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail entityNotFoundException(EntityNotFoundException e) {
        return createProblemDetail(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ProblemDetail entityExistsException(EntityExistsException e) {
        return createProblemDetail(HttpStatus.CONFLICT, e);
    }

    private ProblemDetail createProblemDetail(HttpStatus status, Exception e) {
        final var detail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        detail.setTitle(e.getClass().getSimpleName());
        return detail;
    }
}
