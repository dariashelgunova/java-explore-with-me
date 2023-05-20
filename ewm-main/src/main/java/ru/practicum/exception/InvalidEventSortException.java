package ru.practicum.exception;

public class InvalidEventSortException extends RuntimeException {
    public InvalidEventSortException(String message) {
        super(message);
    }
}
// 400 NumberFormatException
// 400 Event must be published
