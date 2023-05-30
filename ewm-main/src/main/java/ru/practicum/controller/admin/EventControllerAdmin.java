package ru.practicum.controller.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.update.UpdateEventAdminRequest;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.model.enums.State;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventControllerAdmin {

    EventService eventService;
    EventMapper eventMapper;
    CategoryService categoryService;

    @GetMapping
    public List<EventFullDto> getAdminEvents(
            @RequestParam(required = false) List<Integer> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(defaultValue = "2020-01-01 00:00:01") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(defaultValue = "2050-01-01 00:00:01") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<Event> result = eventService.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        return eventMapper.toFullDtoList(result);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAndStatus(@PathVariable("eventId") Integer eventId,
                                             @Valid @RequestBody UpdateEventAdminRequest eventDto) {
        Event event = eventMapper.fromDto(eventDto, categoryService.findCategoryByIdPublic(eventDto.getCategory()));
        Event updatedEvent = eventService.updateEventAdmin(eventId, event);
        return eventMapper.toFullDto(updatedEvent);
    }
}
