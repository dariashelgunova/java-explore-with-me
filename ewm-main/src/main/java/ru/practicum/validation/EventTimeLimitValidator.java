package ru.practicum.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public class EventTimeLimitValidator implements ConstraintValidator<EventTimeLimit, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {
        LocalDateTime currentTime = LocalDateTime.now();

        if (eventDate == null) {
            return false;
        }
        return eventDate.isAfter(currentTime.plus(2, ChronoUnit.HOURS));
    }
}
