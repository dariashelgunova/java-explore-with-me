package ru.practicum.dto.compilation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    @NotBlank
    @Size(max = 50)
    String title;
    boolean pinned;
    List<Integer> events;
}
