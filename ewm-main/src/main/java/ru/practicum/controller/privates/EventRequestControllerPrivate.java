package ru.practicum.controller.privates;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.eventrequest.participation.ParticipationRequestDto;
import ru.practicum.mapper.EventRequestMapper;
import ru.practicum.model.EventRequest;
import ru.practicum.service.eventrequest.EventRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventRequestControllerPrivate {

    EventRequestService eventRequestService;
    EventRequestMapper eventRequestMapper;

    @GetMapping
    public List<ParticipationRequestDto> getEventsRequests(@PathVariable("userId") Integer userId) {
        List<EventRequest> result = eventRequestService.findByUserIdPrivate(userId);
        return eventRequestMapper.toParticipationDtoList(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createEventRequest(@PathVariable("userId") Integer userId,
                                                      @RequestParam("eventId") Integer eventId) {
        EventRequest result = eventRequestService.createEventRequestPrivate(userId, eventId);
        return eventRequestMapper.toParticipationDto(result);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") Integer userId,
                                                 @PathVariable("requestId") Integer requestId) {
        EventRequest result = eventRequestService.cancelEventRequestPrivate(userId, requestId);
        return eventRequestMapper.toParticipationDto(result);
    }
}
