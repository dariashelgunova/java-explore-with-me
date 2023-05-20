package ru.practicum.service.eventrequest;

import ru.practicum.model.EventRequest;
import ru.practicum.model.enums.Status;

import java.util.List;

public interface EventRequestService {
    List<EventRequest> findByUserIdPrivate(Integer userId);
    EventRequest findEventRequestById(Integer requestId);
    EventRequest cancelEventRequestPrivate(Integer userId, Integer requestId);
    EventRequest createEventRequestPrivate(Integer userId, Integer eventId);
    List<EventRequest> findByUserIdAndEventIdPrivate(Integer userId, Integer eventId);
    List<EventRequest> changeEventRequestStatusPrivate(Integer userId, Integer eventId, List<Integer> requestIds, Status status);
}
