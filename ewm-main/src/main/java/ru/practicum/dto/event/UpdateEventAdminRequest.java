package ru.practicum.dto.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Location;
import ru.practicum.model.enums.StateAction;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {
    String annotation;
    int category;
    String description;
    LocalDateTime eventDate;
    Location location;
    boolean paid;
    int participantLimit;
    boolean requestModeration;
    StateAction stateAction;
    String title;
}
