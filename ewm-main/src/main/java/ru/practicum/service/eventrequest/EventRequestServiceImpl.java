package ru.practicum.service.eventrequest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundObjectException;
import ru.practicum.model.Event;
import ru.practicum.model.EventRequest;
import ru.practicum.model.User;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.Status;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.EventRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventRequestServiceImpl implements EventRequestService {
    EventRequestRepository eventRequestRepository;
    UserRepository userRepository;
    EventRepository eventRepository;

    public List<EventRequest> findByUserIdPrivate(Integer userId) {
        userRepository.getUserByIdOrThrowException(userId);
        return eventRequestRepository.findByUserId(userId);
    }

    public EventRequest cancelEventRequestPrivate(Integer userId, Integer requestId) {
        userRepository.getUserByIdOrThrowException(userId);
        EventRequest request = findEventRequestById(requestId);
        request.setStatus(Status.CANCELED);
        return eventRequestRepository.save(request);
    }

    public EventRequest createEventRequestPrivate(Integer userId, Integer eventId) {
        User user = userRepository.getUserByIdOrThrowException(userId);
        Event event = eventRepository.getEventByIdOrThrowException(eventId);
        checkIfUserHasRequest(userId, eventId);
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Инициатор события не может отправить заявку на участие в нем!");
        } else if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Нельзя отправить заявку на участие в еще не опубликованном событии!");
        }
        EventRequest newRequest = new EventRequest();
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            newRequest.setStatus(Status.PENDING);
        } else {
            if (event.getParticipantLimit() != 0 && Objects.equals(findParticipantsAmountConfirmedByEventId(eventId), event.getParticipantLimit())) {
                event.setAvailable(false);
                eventRepository.save(event);
                throw new ConflictException("Достигнуто максимальное количество участников!");
            }
            newRequest.setStatus(Status.CONFIRMED);
        }
        newRequest.setUser(user);
        newRequest.setEvent(event);
        newRequest.setCreatedOn(LocalDateTime.now());
        return eventRequestRepository.save(newRequest);
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

    public List<EventRequest> findByUserIdAndEventIdPrivate(Integer userId, Integer eventId) {
        return eventRequestRepository.findByEventId(eventId);
    }

    @Transactional
    public List<EventRequest> changeEventRequestStatusPrivate(Integer userId, Integer eventId, List<Integer> requestIds, Status status) {
        userRepository.getUserByIdOrThrowException(userId);
        Event event = eventRepository.getEventByIdOrThrowException(eventId);
        List<EventRequest> requests = findEventRequestsByRequestsIdsIn(requestIds);
        Integer confirmedRequests = eventRequestRepository.findParticipantsAmount(eventId);

        if (event.getParticipantLimit() != 0 && Objects.equals(confirmedRequests, event.getParticipantLimit())) {
            throw new ConflictException("Достигнуто максимальное количество участников!");
        }
        if (status == Status.REJECTED) {
            for (EventRequest request : requests) {
                if (request.getStatus() == Status.CONFIRMED) {
                    throw new ConflictException("Нельзя отменить уже подтвержденную заявку");
                }
                request.setStatus(Status.REJECTED);
            }
        } else {
            for (EventRequest request : requests) {
                if (request.getStatus() == Status.REJECTED) {
                    throw new ConflictException("Статус заявки уже был изменен!");
                } else if (event.getParticipantLimit() != 0 && Objects.equals(confirmedRequests, event.getParticipantLimit())) {
                    request.setStatus(Status.REJECTED);
                } else {
                    request.setStatus(Status.CONFIRMED);
                    if (confirmedRequests != null) {
                        confirmedRequests += 1;
                    }
                }
            }
        }
        return requests;
    }

    private List<EventRequest> findEventRequestsByRequestsIdsIn(List<Integer> requestIds) {
        return eventRequestRepository.findByIdIn(requestIds);
    }

    private Integer findParticipantsAmountConfirmedByEventId(Integer eventId) {
        return eventRequestRepository.findParticipantsAmount(eventId);
    }
}
