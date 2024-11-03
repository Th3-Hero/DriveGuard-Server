package com.group11.driveguard.app.controllers;

import com.group11.driveguard.api.error.ProblemDetailFactory;
import com.group11.driveguard.api.error.ValidationDetail;
import com.group11.driveguard.api.error.ValidationProcessor;
import com.group11.driveguard.app.exceptions.InvalidCredentialsException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail entityNotFoundException(EntityNotFoundException e) {
        return ProblemDetailFactory.createProblemDetail(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ProblemDetail entityExistsException(EntityExistsException e) {
        return ProblemDetailFactory.createProblemDetail(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail invalidCredentialsException(InvalidCredentialsException e) {
        // We could throw a 403 Forbidden here, but 401 Unauthorized hides the existence of the resource
        return ProblemDetailFactory.createProblemDetail(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        ValidationDetail body = ProblemDetailFactory.createValidationDetail();
        ValidationProcessor.addConstraintViolationsToValidationDetail(ex.getConstraintViolations(), body);
        return ResponseEntity.badRequest().body(body);
    }

}
