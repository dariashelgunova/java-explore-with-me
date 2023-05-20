package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.event.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class, CategoryMapper.class})
public interface EventMapper {
    List<EventFullDto> toFullDtoList(List<Event> events);
    EventFullDto toFullDto(Event event);
    List<EventShortDto> toShortDtoList(List<Event> events);
    EventShortDto toShortDto(Event event);
    @Mapping(target = "category", source = "category")
    Event fromDto(NewEventDto dto, Category category);
    @Mapping(target = "category", source = "category")
    Event fromDto(UpdateEventUserRequest dto, Category category);
    @Mapping(target = "category", source = "category")
    Event fromDto(UpdateEventAdminRequest dto, Category category);
}
