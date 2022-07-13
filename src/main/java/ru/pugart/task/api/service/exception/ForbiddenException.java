package ru.pugart.task.api.service.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message) {
        super(message);
    }
}
