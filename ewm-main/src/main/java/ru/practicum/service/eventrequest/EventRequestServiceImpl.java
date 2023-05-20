package ru.practicum.service.eventrequest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundObjectException;
import ru.practicum.model.Event;
import ru.practicum.model.EventRequest;
import ru.practicum.model.User;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.Status;
import ru.practicum.repository.EventRequestRepository;
import ru.practicum.service.event.EventService;
import ru.practicum.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventRequestServiceImpl implements EventRequestService {
    EventRequestRepository eventRequestRepository;
    UserService userService;
    EventService eventService;

    public List<EventRequest> findByUserIdPrivate(Integer userId) {
        userService.findUserById(userId);
        return eventRequestRepository.findByUserId(userId);
    }

    public EventRequest cancelEventRequestPrivate(Integer userId, Integer requestId) {
        userService.findUserById(userId);
        EventRequest request = findEventRequestById(requestId);
        request.setStatus(Status.PENDING);
        return eventRequestRepository.save(request);
    }

    public EventRequest createEventRequestPrivate(Integer userId, Integer eventId) {
        User user = userService.findUserById(userId);
        Event event = eventService.findEventById(eventId);
        checkIfUserHasRequest(userId, eventId);
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Инициатор события не может отправить заявку на участие в нем!");
        } else if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Нельзя отправить заявку на участие в еще не опубликованном событии!");
        } else if (Objects.equals(findParticipantsAmountConfirmedByEventId(eventId), event.getParticipantLimit()) || event.getParticipantLimit() == 0) {
            event.setAvailable(false);
            eventService.saveEvent(event);
            throw new ConflictException("Достигнуто максимальное количество участников!");
        } else {
            EventRequest newRequest = new EventRequest();
            if (event.getRequestModeration()) {
                newRequest.setStatus(Status.PENDING);
            } else {
                newRequest.setStatus(Status.CONFIRMED);
            }
            newRequest.setUser(user);
            newRequest.setEvent(event);
            newRequest.setCreatedOn(LocalDateTime.now());
            return eventRequestRepository.save(newRequest);
        }
    }

    public EventRequest findEventRequestById(Integer requestId) {
        return getEventRequestByIdOrThrowException(requestId);
    }

    private EventRequest getEventRequestByIdOrThrowException(Integer requestId) {
        return eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundObjectException("Объект не был найден"));
    }

    private void checkIfUserHasRequest(Integer userId, Integer eventId) {
        if (!eventRequestRepository.findByUserIdAndEventId(userId, eventId).isEmpty()) {
            throw new ConflictException("Данный пользователь уже отправлял запрос на участие в мероприятии!");
        }
    }

    private Integer findParticipantsAmountConfirmedByEventId(Integer eventId) {
        return eventRequestRepository.findParticipantsAmount(eventId);
    }

    public List<EventRequest> findByUserIdAndEventIdPrivate(Integer userId, Integer eventId) {
        return eventRequestRepository.findByEventId(eventId);
    }

    public List<EventRequest> changeEventRequestStatusPrivate(Integer userId, Integer eventId, List<Integer> requestIds, Status status) {
        userService.findUserById(userId);
        Event event = eventService.findEventById(eventId);
        List<EventRequest> requests = findEventRequestsByRequestsIdsIn(requestIds);
        if (status == Status.REJECTED) {
            for (EventRequest request : requests) {
                request.setStatus(Status.REJECTED);
            }
        } else {
            for (EventRequest request : requests) {
                if (request.getStatus() != Status.PENDING) {
                    throw new ConflictException("Статус заявки уже был изменен!");
                } else if (Objects.equals(findParticipantsAmountConfirmedByEventId(eventId), event.getParticipantLimit())
                        || event.getParticipantLimit() == 0) {
                    request.setStatus(Status.REJECTED);
                } else {
                    request.setStatus(Status.CONFIRMED);
                }
            }
        }
        return requests;
    }

    private List<EventRequest> findEventRequestsByRequestsIdsIn(List<Integer> requestIds) {
        return eventRequestRepository.findByIdIn(requestIds);
    }
}
