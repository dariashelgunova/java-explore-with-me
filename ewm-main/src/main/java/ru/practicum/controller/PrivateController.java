package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateController {
    EventRequestService eventRequestService;
    EventRequestMapper eventRequestMapper;
    EventService eventService;
    EventMapper eventMapper;
    CategoryService categoryService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable("userId") Integer userId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        List<Event> result = eventService.findEventsByUserIdPrivate(userId, from, size);
        return eventMapper.toShortDtoList(result);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Integer userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        Event newEvent = eventMapper.fromDto(eventDto, categoryService.findCategoryByIdPublic(eventDto.getCategory()));
        Event createdEvent = eventService.createEventPrivate(userId, newEvent);
        return eventMapper.toFullDto(createdEvent);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventById(@PathVariable("userId") Integer userId,
                                     @PathVariable("eventId") Integer eventId) {
        Event result = eventService.findEventByIdPrivate(userId, eventId);
        return eventMapper.toFullDto(result);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable("userId") Integer userId,
                                    @PathVariable("eventId") Integer eventId,
                                    @Valid @RequestBody UpdateEventUserRequest eventDto) {
        Event newEvent = eventMapper.fromDto(eventDto, categoryService.findCategoryByIdPublic(eventDto.getCategory()));
        Event updatedEvent = eventService.updateEventPrivate(userId, eventId, newEvent);
        return eventMapper.toFullDto(updatedEvent);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventsRequestsPrivate(@PathVariable("userId") Integer userId,
                                                                  @PathVariable("eventId") Integer eventId) {
        List<EventRequest> result = eventRequestService.findByUserIdAndEventIdPrivate(userId, eventId);
        return eventRequestMapper.toParticipationDtoList(result);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable("userId") Integer userId,
                                                              @PathVariable("eventId") Integer eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest eventDto) {
        List<EventRequest> result = eventRequestService.changeEventRequestStatusPrivate(userId, eventId, eventDto.getRequestIds(), eventDto.getStatus());
        return eventRequestMapper.toEventRequestStatusUpdateResult(result);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getEventsRequests(@PathVariable("userId") Integer userId) {
        List<EventRequest> result = eventRequestService.findByUserIdPrivate(userId);
        return eventRequestMapper.toParticipationDtoList(result);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createEventRequest(@PathVariable("userId") Integer userId,
                                                      @RequestParam("eventId") Integer eventId) {
        EventRequest result = eventRequestService.createEventRequestPrivate(userId, eventId);
        return eventRequestMapper.toParticipationDto(result);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") Integer userId,
                                                 @PathVariable("requestId") Integer requestId) {
        EventRequest result = eventRequestService.cancelEventRequestPrivate(userId, requestId);
        return eventRequestMapper.toParticipationDto(result);
    }
}
