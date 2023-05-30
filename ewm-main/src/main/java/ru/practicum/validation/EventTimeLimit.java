package ru.practicum.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = EventTimeLimitValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventTimeLimit {
    String message() default "Мероприятие не должно быть ранее, чем через два часа!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

