package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Location;
import ru.practicum.validation.EventTimeLimit;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotEmpty
    @NotBlank
    @Size(min = 20)
    @Size(max = 2000)
    String annotation;
    @NotNull
    Integer category;
    @NotEmpty
    @NotBlank
    @Size(min = 20)
    @Size(max = 7000)
    String description;
    @EventTimeLimit
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    @Size(min = 3)
    @Size(max = 120)
    @NotBlank
    String title;
}
