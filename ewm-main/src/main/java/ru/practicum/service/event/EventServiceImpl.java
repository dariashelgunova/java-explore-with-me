package ru.practicum.service.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.HitClient;
import ru.practicum.client.StatsClient;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundObjectException;
import ru.practicum.model.*;
import ru.practicum.model.enums.SortEnum;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.StateAction;
import ru.practicum.pageable.OffsetBasedPageRequest;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.service.eventrequest.EventRequestService;
import ru.practicum.service.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
    EventRepository eventRepository;
    UserService userService;
    StatsClient statsClient;
    HitClient hitClient;
    LocationRepository locationRepository;

    public void checkIfThereAreNoEventsInCategory(Category category) {
        List<Event> eventsByCategory = eventRepository.findByCategoryId(category.getId());
        if (!eventsByCategory.isEmpty()) {
            throw new ConflictException("В данной категории есть события!");
        }
    }
    public Event findEventByIdPrivate(Integer userId, Integer eventId) {
        userService.findUserById(userId);
        return getEventByIdOrThrowException(eventId);
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
        return eventRepository.findByInitiatorId(userId, pageable);
    }

    public Event createEventPrivate(Integer userId, Event event) {
        User user = userService.findUserById(userId);
        event.setInitiator(user);
        return eventRepository.save(event);
    }

    public Event updateEventPrivate(Integer userId, Integer eventId, Event event) {
        userService.findUserById(userId);
        Event existingEvent = getEventByIdOrThrowException(eventId);
        if (existingEvent.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Редактируемое событие не может быть опубликованным!");
        } else {
            changeEventFields(existingEvent, event);
            if (event.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                existingEvent.setState(State.PUBLISHED);
            } else if (event.getStateAction().equals(StateAction.REJECT_EVENT)) {
                existingEvent.setState(State.CANCELED);
            }
        }
        return eventRepository.save(existingEvent);
    }

    private Event changeEventFields(Event existingEvent, Event newEvent) {
        if (newEvent.getAnnotation() != null) {
            existingEvent.setAnnotation(newEvent.getAnnotation());
        } else if (newEvent.getCategory() != null) {
            existingEvent.setCategory(newEvent.getCategory());
        } else if (newEvent.getDescription() != null) {
            existingEvent.setDescription(newEvent.getDescription());
        } else if (newEvent.getEventDate() != null) {
            existingEvent.setEventDate(newEvent.getEventDate());
        } else if (newEvent.getLocation() != null) {
            existingEvent.setLocation(newEvent.getLocation());
        } else if (newEvent.getPaid() != null) {
            existingEvent.setPaid(newEvent.getPaid());
        } else if (newEvent.getParticipantLimit() != null) {
            existingEvent.setParticipantLimit(newEvent.getParticipantLimit());
        } else if (newEvent.getRequestModeration() != null) {
            existingEvent.setRequestModeration(newEvent.getRequestModeration());
        } else if (newEvent.getState() != null) {
            existingEvent.setState(newEvent.getState());
        } else if (newEvent.getTitle() != null) {
            existingEvent.setTitle(newEvent.getTitle());
        } else if (newEvent.getPublishedOn() != null) {
            existingEvent.setPublishedOn(newEvent.getPublishedOn());
        }
        return eventRepository.save(existingEvent);
    }

    public List<Event> getEventsAdmin(Integer[] users, String[] states, Integer[] categories, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Integer from, Integer size) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(size, from, null);
        List<Integer> listUserIds = Arrays.asList(users);
        List<String> listStates = Arrays.asList(states);
        List<Integer> listCategoriesIds = Arrays.asList(categories);
        return eventRepository.findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(listUserIds, listStates,
                listCategoriesIds, rangeStart, rangeEnd, pageable);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEventAdmin(Integer eventId, Event event) {
        LocalDateTime currentTime = LocalDateTime.now();
        Event existingEvent = getEventByIdOrThrowException(eventId);
        if (event.getStateAction().equals(StateAction.PUBLISH_EVENT) && currentTime.plusHours(1).isAfter(existingEvent.getEventDate())) {
            throw new ConflictException("Редактировать событие нужно как минимум за час до публикации!");
        } else if (event.getStateAction().equals(StateAction.PUBLISH_EVENT) && !existingEvent.getState().equals(State.PENDING)) {
            throw new ConflictException("Опубликовать событие можно, только если оно в состоянии ожидания публикации!");
        } else if (event.getStateAction().equals(StateAction.REJECT_EVENT) && existingEvent.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Отклонить можно только еще неопубликованное событие!");
        } else {
            if (event.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setPublishedOn(currentTime);
                event.setState(State.PUBLISHED);
                return changeEventFields(existingEvent, event);
            } else {
                event.setState(State.CANCELED);
                return changeEventFields(existingEvent, event);
            }
        }
    }

    public List<Event> getEventsPublic(String text, Integer[] categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                       boolean onlyAvailable, SortEnum sortEnum, Integer from, Integer size, String ip) {
        Sort sort;
        if (sortEnum == SortEnum.EVENT_DATE) {
            sort = Sort.by(Sort.Direction.ASC, "event_date");
        } else {
            sort = null;
        }
        List<Event> result;
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(size, from, sort);
        List<Integer> categoriesIds = Arrays.asList(categories);
        LocalDateTime currentTime = LocalDateTime.now();

        if (paid == null) {
            if (rangeStart == null || rangeEnd == null) {
                result = eventRepository.getEvents(text, text, categoriesIds, currentTime, onlyAvailable, pageable);
            } else {
                result = eventRepository.getEvents(text, text, categoriesIds, rangeStart, rangeEnd, onlyAvailable, pageable);
            }
        } else {
            if (rangeStart == null || rangeEnd == null) {
                result = eventRepository.getEvents(text, text, categoriesIds,paid, currentTime, onlyAvailable, pageable);
            } else {
                result = eventRepository.getEvents(text, text, categoriesIds, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
            }
        }
        for (Event event : result) {
            setStatsByEvent(event);
        }
        sendStats("/events", ip);
        if (sortEnum == SortEnum.VIEWS) {
            result.sort(Comparator.comparingInt(Event::getViews));
        }
        return result;
    }

    private Event setStatsByEvent(Event event) {
        String[] uris = new String[1];
        uris[0] = "/events/" + event.getId();
        LocalDateTime start = LocalDateTime.now().minusYears(5);
        LocalDateTime end = LocalDateTime.now().plusYears(5);
        ResponseEntity<Object> result = statsClient.getStats(start.toString(), end.toString(), uris, false);
        ViewStats stats = (ViewStats) result.getBody();
        event.setViews(stats.getHits());
        return event;
    }

    private void sendStats(String uri, String ip) {
        EndpointHit hit = new EndpointHit();
        hit.setTimestamp(LocalDateTime.now().toString());
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setApp("ewm-main-service");
        hitClient.create(hit);
    }

    public Event getEventByIdPublic(Integer eventId, String ip) {
        List<Event> result = eventRepository.getPublicEventById(eventId);
        if (result.isEmpty()) {
            throw new NotFoundObjectException("Данное событие не найдено или еще не опубликовано!");
        } else {
            Event event = result.get(0);
            sendStats("/events/" + event.getId(), ip);
            return setStatsByEvent(event);
        }
    }

    public List<Event> findEventsByIds(List<Integer> ids) {
        return eventRepository.findByIdIn(ids);
    }

}
