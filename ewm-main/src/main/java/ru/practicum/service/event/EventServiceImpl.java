package ru.practicum.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.HitClient;
import ru.practicum.client.StatsClient;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundObjectException;
import ru.practicum.model.*;
import ru.practicum.model.enums.SortEnum;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.StateAction;
import ru.practicum.pageable.OffsetBasedPageRequest;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.user.UserService;
import ru.practicum.view.EventView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
    EventRepository eventRepository;
    UserService userService;
    StatsClient statsClient;
    HitClient hitClient;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        event.setInitiator(user);
        return eventRepository.save(event);
    }

    public Event updateEventPrivate(Integer userId, Integer eventId, Event event) {
        userService.findUserById(userId);
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

        return eventRepository.save(existingEvent);
    }

    private Event changeEventFields(Event existingEvent, Event newEvent) {
        if (newEvent.getAnnotation() != null) {
            existingEvent.setAnnotation(newEvent.getAnnotation());
        }
        if (newEvent.getCategory() != null) {
            existingEvent.setCategory(newEvent.getCategory());
        }
        if (newEvent.getDescription() != null) {
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
//        if (newEvent.getRequestModeration() != null) {
//            existingEvent.setRequestModeration(newEvent.getRequestModeration());
//        }
        if (newEvent.getState() != null) {
            existingEvent.setState(newEvent.getState());
        }
        if (newEvent.getTitle() != null) {
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
//        List<EventView> result = eventRepository.getEventsAdmin(users, states,
//                categories, rangeStart, rangeEnd, pageable);
//        List<Event> events = new ArrayList<>();
//        for (EventView view : result) {
//            events.add(setFields(view));
//        }
        return eventRepository.findAllAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
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
            return changeEventFields(existingEvent, event);
        }
        if (event.getStateAction().equals(StateAction.PUBLISH_EVENT) && currentTime.plusHours(1).isAfter(existingEvent.getEventDate())) {
            throw new ConflictException("Редактировать событие нужно как минимум за час до публикации!");
        } else if (event.getStateAction().equals(StateAction.PUBLISH_EVENT) && (!Optional.ofNullable(existingEvent.getState()).orElse(State.PENDING).equals(State.PENDING))) {
            throw new ConflictException("Опубликовать событие можно, только если оно в состоянии ожидания публикации!");
        } else if (event.getStateAction().equals(StateAction.REJECT_EVENT) && (existingEvent.getState().equals(State.PUBLISHED))) {
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
        List<Event> result = new ArrayList<>();
        List<EventView> subResult;
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
        ResponseEntity<Object> result = statsClient.getStats(start.format(dateTimeFormatter), end.format(dateTimeFormatter), uris, true);
        Object statsObject = result.getBody();
        if (statsObject != null) {
            List<ViewStats> stats = Arrays.asList(new ObjectMapper().convertValue(statsObject, ViewStats[].class));
            if (!stats.isEmpty()) {
                event.setViews(stats.get(0).getHits());
            }
        }
        return event;
    }

    private void sendStats(String uri, String ip) {
        EndpointHit hit = new EndpointHit();
        hit.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));
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
            return setStatsByEvent(result.get(0));
        }
    }

    public List<Event> findEventsByIds(List<Integer> ids) {
        return eventRepository.findByIds(ids);
    }
//    private Event setFields(EventView eventView) {
//        return new Event(eventView.getId(), eventView.getAnnotation(), eventView.getCategory(), eventView.getConfirmedRequests(),
//                eventView.getAvailable(), eventView.getCreatedOn(), eventView.getDescription(), eventView.getEventDate(),
//                eventView.getInitiator(), eventView.getLocation(), eventView.getPaid(), eventView.getParticipantLimit(),
//                eventView.getPublishedOn(), eventView.getRequestModeration(), eventView.getState(), eventView.getTitle(),
//                null, 0, null);
//    }

}
