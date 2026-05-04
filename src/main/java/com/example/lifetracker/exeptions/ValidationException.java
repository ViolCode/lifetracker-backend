package com.example.lifetracker.exeptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}