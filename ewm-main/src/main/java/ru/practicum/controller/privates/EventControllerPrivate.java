package ru.practicum.controller.privates;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.update.UpdateEventUserRequest;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.eventrequest.participation.ParticipationRequestDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.EventRequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventRequest;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.event.EventService;
import ru.practicum.service.eventrequest.EventRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventControllerPrivate {

    EventRequestService eventRequestService;
    EventRequestMapper eventRequestMapper;
    EventService eventService;
    EventMapper eventMapper;
    CategoryService categoryService;

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable("userId") Integer userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<Event> result = eventService.findEventsByUserIdPrivate(userId, from, size);
        return eventMapper.toShortDtoList(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Integer userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        Event newEvent = eventMapper.fromDto(eventDto, categoryService.findCategoryByIdPublic(eventDto.getCategory()));
        Event createdEvent = eventService.createEventPrivate(userId, newEvent);
        return eventMapper.toFullDto(createdEvent);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable("userId") Integer userId,
                                     @PathVariable("eventId") Integer eventId) {
        Event result = eventService.findEventByIdPrivate(userId, eventId);
        return eventMapper.toFullDto(result);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable("userId") Integer userId,
                                    @PathVariable("eventId") Integer eventId,
                                    @Valid @RequestBody UpdateEventUserRequest eventDto) {
        Event newEvent = eventMapper.fromDto(eventDto, categoryService.findCategoryByIdPublic(eventDto.getCategory()));
        Event updatedEvent = eventService.updateEventPrivate(userId, eventId, newEvent);
        return eventMapper.toFullDto(updatedEvent);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventsRequestsPrivate(@PathVariable("userId") Integer userId,
                                                                  @PathVariable("eventId") Integer eventId) {
        List<EventRequest> result = eventRequestService.findByUserIdAndEventIdPrivate(userId, eventId);
        return eventRequestMapper.toParticipationDtoList(result);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable("userId") Integer userId,
                                                              @PathVariable("eventId") Integer eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest eventDto) {
        List<EventRequest> result = eventRequestService.changeEventRequestStatusPrivate(userId, eventId, eventDto.getRequestIds(), eventDto.getStatus());
        return eventRequestMapper.toEventRequestStatusUpdateResult(result);
    }
}
