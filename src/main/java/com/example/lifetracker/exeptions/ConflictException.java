package com.example.lifetracker.exeptions;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}