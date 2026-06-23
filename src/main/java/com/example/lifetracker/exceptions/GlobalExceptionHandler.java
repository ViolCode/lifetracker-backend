package com.example.lifetracker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> build(
            HttpStatus status,
            String message
    ) {

        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", status.value(),
                        "error", message
                ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(
            NotFoundException ex
    ) {

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidation(
            ValidationException ex
    ) {

        return build(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(
            BadRequestException ex
    ) {

        return build(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflict(
            ConflictException ex
    ) {

        return build(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error ->
                        error.getDefaultMessage()
                )
                .orElse("Validation failed");

        return build(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(
            BadCredentialsException ex
    ) {

        return build(
                HttpStatus.UNAUTHORIZED,
                "Wrong email or password."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(
            Exception ex
    ) {

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong"
        );
    }
}