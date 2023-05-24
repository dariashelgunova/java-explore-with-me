package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Location;
import ru.practicum.model.enums.StateAction;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {
    @Size(min = 20)
    @Size(max = 2000)
    String annotation;
    Integer category;
    @Size(min = 20)
    @Size(max = 7000)
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    LocalDateTime eventDate;
    Location location;
    boolean paid;
    int participantLimit;
    boolean requestModeration;
    StateAction stateAction;
    @Size(min = 3)
    @Size(max = 120)
    String title;
}
