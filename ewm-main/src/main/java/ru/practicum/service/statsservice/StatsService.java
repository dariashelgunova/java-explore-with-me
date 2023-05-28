package ru.practicum.service.statsservice;

import ru.practicum.model.Event;

import java.util.List;

public interface StatsService {

    void sendStats(String uri, String ip);

    Event setStatsByEvent(Event event);

    List<Event> setViewsByEvents(List<Event> events);
}
