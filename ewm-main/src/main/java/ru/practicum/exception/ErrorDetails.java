package ru.practicum.exception;

import lombok.Value;

@Value
public class ErrorDetails {
    int status;
    String error;
}
