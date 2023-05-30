package ru.practicum.controller.publics;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.model.enums.SortEnum;
import ru.practicum.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventControllerPublic {

    EventService eventService;
    EventMapper eventMapper;

    @GetMapping
    public List<EventShortDto> getPublicEvents(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "") List<Integer> categories,
            @RequestParam(required = false) boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(defaultValue = "ALL") String sort,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        SortEnum sortEnum = SortEnum.findByValueOrThrowException(sort);

        List<Event> result = eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sortEnum, from, size, request.getRemoteAddr());
        return eventMapper.toShortDtoList(result);
    }

    @GetMapping("/{id}")
    public EventFullDto getPublicEventById(@PathVariable("id") Integer id, HttpServletRequest request) {
        Event result = eventService.getEventByIdPublic(id, request.getRemoteAddr());
        return eventMapper.toFullDto(result);
    }
}