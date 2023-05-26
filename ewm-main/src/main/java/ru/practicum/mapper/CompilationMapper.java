package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EventMapper.class})
public interface CompilationMapper {
    @Mapping(target = "events", source = "events")
    Compilation fromDto(UpdateCompilationRequest dto, List<Event> events);

    @Mapping(target = "events", source = "events")
    Compilation fromDto(NewCompilationDto dto, List<Event> events);

    CompilationDto toDto(Compilation compilation);

    List<CompilationDto> toDtoList(List<Compilation> compilations);
}
