package ru.practicum.exception;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ApiError {
    String status;
    List<String> errors;
    String message;
    String reason;
    LocalDateTime timestamp;
}
