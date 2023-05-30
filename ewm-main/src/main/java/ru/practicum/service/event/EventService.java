package ru.practicum.service.event;

import ru.practicum.model.Event;
import ru.practicum.model.enums.SortEnum;
import ru.practicum.model.enums.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    Event findEventById(Integer eventId);

    List<Event> findEventsByUserIdPrivate(Integer userId, Integer from, Integer size);

    Event createEventPrivate(Integer userId, Event event);

    Event findEventByIdPrivate(Integer userId, Integer eventId);

    Event updateEventPrivate(Integer userId, Integer eventId, Event event);

    List<Event> getEventsAdmin(List<Integer> users, List<State> states, List<Integer> categories, LocalDateTime rangeStart,
                               LocalDateTime rangeEnd, Integer from, Integer size);

    Event updateEventAdmin(Integer eventId, Event event);

    Event saveEvent(Event event);

    List<Event> getEventsPublic(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                boolean onlyAvailable, SortEnum sort, Integer from, Integer size, String ip);

    Event getEventByIdPublic(Integer eventId, String ip);

    List<Event> findEventsByIds(List<Integer> ids);
}
