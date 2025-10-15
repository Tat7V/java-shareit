package ru.practicum.shareit.exeptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}