package ru.practicum.dto.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Integer id;
    String text;
    Integer userId;
    Integer eventId;
    LocalDateTime createdOn;
    Boolean isPositive;
    Boolean wasModified;
    LocalDateTime modifiedOn;
}
