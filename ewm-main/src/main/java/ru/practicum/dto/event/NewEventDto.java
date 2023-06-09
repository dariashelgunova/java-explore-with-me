package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.validation.EventTimeLimit;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    Integer category;
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;
    @EventTimeLimit
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    LocationDto location;
    boolean paid;
    @PositiveOrZero
    int participantLimit;
    boolean requestModeration = true;
    @Size(min = 3, max = 120)
    @NotBlank
    String title;
}
