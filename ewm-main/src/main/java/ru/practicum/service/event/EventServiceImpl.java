package ru.practicum.service.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundObjectException;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.enums.SortEnum;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.StateAction;
import ru.practicum.pageable.OffsetBasedPageRequest;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.EventRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.statsservice.StatsService;
import ru.practicum.view.CommentView;
import ru.practicum.view.RequestView;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
    EventRepository eventRepository;
    UserRepository userRepository;
    StatsService statsService;
    EventRequestRepository eventRequestRepository;
    CommentRepository commentRepository;


    public Event findEventByIdPrivate(Integer userId, Integer eventId) {
        userRepository.getUserByIdOrThrowException(userId);
        Event result = getEventByIdOrThrowException(eventId);
        setCommentsToEvent(result);
        return setConfirmedRequestsToEvent(result);
    }

    public Event findEventById(Integer eventId) {
        return getEventByIdOrThrowException(eventId);
    }

    private Event getEventByIdOrThrowException(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundObjectException("Объект не был найден"));
    }

    public List<Event> findEventsByUserIdPrivate(Integer userId, Integer from, Integer size) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(size, from, null);
        List<Event> result = eventRepository.findByInitiatorId(userId, pageable);
        return setCommentsToEventsList(result);
    }

    public Event createEventPrivate(Integer userId, Event event) {
        User user = userRepository.getUserByIdOrThrowException(userId);
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        event.setInitiator(user);
        Event result = eventRepository.save(event);
        setCommentsToEvent(result);
        return setConfirmedRequestsToEvent(result);
    }

    public Event updateEventPrivate(Integer userId, Integer eventId, Event event) {
        userRepository.getUserByIdOrThrowException(userId);
        Event existingEvent = getEventByIdOrThrowException(eventId);
        if (Optional.ofNullable(existingEvent.getState()).orElse(State.PENDING).equals(State.PUBLISHED)) {
            throw new ConflictException("Редактируемое событие не может быть опубликованным!");
        }
        if (event.getStateAction() == null) {
            return changeEventFields(existingEvent, event);
        }
        if (event.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            existingEvent.setState(State.PUBLISHED);
            changeEventFields(existingEvent, event);
        } else if (event.getStateAction().equals(StateAction.REJECT_EVENT)) {
            existingEvent.setState(State.CANCELED);
            changeEventFields(existingEvent, event);
        } else if (event.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            existingEvent.setState(State.PENDING);
        } else if (event.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            existingEvent.setState(State.CANCELED);
        }
        Event result = eventRepository.save(existingEvent);
        setCommentsToEvent(result);
        return setConfirmedRequestsToEvent(result);
    }

    private Event changeEventFields(Event existingEvent, Event newEvent) {
        if (StringUtils.isNotBlank(newEvent.getAnnotation())) {
            existingEvent.setAnnotation(newEvent.getAnnotation());
        }
        if (newEvent.getCategory() != null) {
            existingEvent.setCategory(newEvent.getCategory());
        }
        if (StringUtils.isNotBlank(newEvent.getDescription())) {
            existingEvent.setDescription(newEvent.getDescription());
        }
        if (newEvent.getEventDate() != null) {
            existingEvent.setEventDate(newEvent.getEventDate());
        }
        if (newEvent.getLocation() != null) {
            existingEvent.setLocation(newEvent.getLocation());
        }
        if (newEvent.getPaid() != null) {
            existingEvent.setPaid(newEvent.getPaid());
        }
        if (newEvent.getParticipantLimit() != null && newEvent.getParticipantLimit() != 0) {
            existingEvent.setParticipantLimit(newEvent.getParticipantLimit());
        }
        if (newEvent.getState() != null) {
            existingEvent.setState(newEvent.getState());
        }
        if (StringUtils.isNotBlank(newEvent.getTitle())) {
            existingEvent.setTitle(newEvent.getTitle());
        }
        if (newEvent.getPublishedOn() != null) {
            existingEvent.setPublishedOn(newEvent.getPublishedOn());
        }
        return eventRepository.save(existingEvent);
    }

    public List<Event> getEventsAdmin(List<Integer> users, List<State> states, List<Integer> categories, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Integer from, Integer size) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(size, from, null);
        List<Event> events = eventRepository.findAllAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
        setCommentsToEventsList(events);
        return setConfirmedRequestsToEventsList(events);
    }

    public Event saveEvent(Event event) {
        if (event.getState() == null) {
            event.setState(State.PENDING);
        }
        return eventRepository.save(event);
    }

    public Event updateEventAdmin(Integer eventId, Event event) {
        LocalDateTime currentTime = LocalDateTime.now();
        Event existingEvent = getEventByIdOrThrowException(eventId);
        if (event.getStateAction() == null) {
            Event result = changeEventFields(existingEvent, event);
            return setConfirmedRequestsToEvent(result);
        }
        if (event.getStateAction().equals(StateAction.PUBLISH_EVENT) &&
                currentTime.plusHours(1).isAfter(existingEvent.getEventDate())) {
            throw new ConflictException("Редактировать событие нужно как минимум за час до публикации!");
        } else if (event.getStateAction().equals(StateAction.PUBLISH_EVENT) &&
                (!Optional.ofNullable(existingEvent.getState()).orElse(State.PENDING).equals(State.PENDING))) {
            throw new ConflictException("Опубликовать событие можно, только если оно в состоянии ожидания публикации!");
        } else if (event.getStateAction().equals(StateAction.REJECT_EVENT) &&
                (existingEvent.getState().equals(State.PUBLISHED))) {
            throw new ConflictException("Отклонить можно только еще неопубликованное событие!");
        } else {
            if (event.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setPublishedOn(currentTime);
                event.setState(State.PUBLISHED);
            } else {
                event.setState(State.CANCELED);
            }
            Event result = changeEventFields(existingEvent, event);
            setCommentsToEvent(result);
            return setConfirmedRequestsToEvent(result);
        }
    }

    public List<Event> getEventsPublic(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                       boolean onlyAvailable, SortEnum sortEnum, Integer from, Integer size, String ip) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new BadRequestException("Время начала не может быть позже окончания!");
            }
        }
        Sort sort;
        if (sortEnum == SortEnum.EVENT_DATE) {
            sort = Sort.by(Sort.Direction.ASC, "eventDate");
        } else {
            sort = null;
        }
        List<Event> result;
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(size, from, sort);
        LocalDateTime currentTime = LocalDateTime.now();
        if (!Objects.equals(text, "")) {
            text = text.toLowerCase();
        }
        Boolean available = null;
        if (onlyAvailable) {
            available = true;
        }
        if (rangeStart == null || rangeEnd == null) {
            result = eventRepository.getEvents(text, text, categories, paid, currentTime, available, pageable);
        } else {
            result = eventRepository.getEvents(text, text, categories, paid, rangeStart, rangeEnd, available, pageable);
        }
        List<Event> finalResult = statsService.setViewsByEvents(result);
        statsService.sendStats("/events", ip);
        setCommentsToEventsList(finalResult);
        if (sortEnum == SortEnum.VIEWS) {
            finalResult.sort(Comparator.comparingInt(Event::getViews));
        }
        return finalResult;
    }

    public Event getEventByIdPublic(Integer eventId, String ip) {
        Event result = eventRepository.getPublicEventById(eventId)
                .orElseThrow(() -> new NotFoundObjectException("Данное событие не найдено или еще не опубликовано!"));
        statsService.sendStats("/events/" + result.getId(), ip);
        Event event = statsService.setStatsByEvent(result);
        setCommentsToEvent(event);
        return setConfirmedRequestsToEvent(event);
    }

    public List<Event> findEventsByIds(List<Integer> ids) {
        return eventRepository.findByIds(ids);
    }

    private Event setConfirmedRequestsToEvent(Event event) {
        RequestView view = eventRequestRepository.findParticipantsAmountView(event.getId());
        if (view == null) {
            event.setConfirmedRequests(0);
            return event;
        }
        Integer confirmedRequests = view.getConfirmedRequests();
        event.setConfirmedRequests(confirmedRequests);
        return event;
    }

    private List<Event> setConfirmedRequestsToEventsList(List<Event> events) {
        List<Integer> ids = events.stream()
                .map(Event::getId).collect(Collectors.toList());
        List<RequestView> views = eventRequestRepository.findParticipantsAmountView(ids);
        Map<Integer, Integer> confirmedRequestsByEventId = new HashMap<>();
        for (RequestView view : views) {
            confirmedRequestsByEventId.put(view.getEventId(), view.getConfirmedRequests());
        }
        for (Event event : events) {
            Integer confirmedRequests = confirmedRequestsByEventId.getOrDefault(event.getId(), 0);
            event.setConfirmedRequests(Objects.requireNonNullElse(confirmedRequests, 0));
        }
        return events;
    }

    private List<Event> setCommentsToEventsList(List<Event> events) {
        List<Integer> ids = events.stream()
                .map(Event::getId).collect(Collectors.toList());
        List<CommentView> views = commentRepository.findCommentsView(ids);
        Map<Integer, Integer> commentsByEventId = new HashMap<>();
        for (CommentView view : views) {
            commentsByEventId.put(view.getEventId(), view.getComments());
        }
        for (Event event : events) {
            Integer comments = commentsByEventId.getOrDefault(event.getId(), 0);
            event.setComments(Objects.requireNonNullElse(comments, 0));
        }
        return events;
    }

    private Event setCommentsToEvent(Event event) {
        int comments = Optional.ofNullable(commentRepository.findCommentsAmount(event.getId())).orElse(0);
        event.setComments(comments);
        return event;
    }

}
