package ru.practicum.dto.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCommentDto {
    @NotBlank
    @Size(max = 5000)
    String text;
    Boolean isPositive;
    LocalDateTime modifiedOn = LocalDateTime.now();
}
