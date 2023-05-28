package ru.practicum.service.statsservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.HitClient;
import ru.practicum.client.StatsClient;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Event;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsServiceImpl implements StatsService {
    StatsClient statsClient;
    HitClient hitClient;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String appName;

    public StatsServiceImpl(StatsClient statsClient, HitClient hitClient, @Value("${app.service.name}")String appName) {
        this.statsClient = statsClient;
        this.hitClient = hitClient;
        this.appName = appName;
    }

    public void sendStats(String uri, String ip) {
        EndpointHit hit = new EndpointHit();
        hit.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setApp(appName);
        hitClient.create(hit);
    }

    public Event setStatsByEvent(Event event) {
        String[] uris = new String[1];
        uris[0] = "/events/" + event.getId();
        LocalDateTime start = LocalDateTime.now().minusYears(5);
        LocalDateTime end = LocalDateTime.now();
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
    public List<Event> setViewsByEvents(List<Event> events) {
        Map<Integer, Integer> viewsById = getViewsByIds(events);
        if (!events.isEmpty() && !viewsById.isEmpty()) {
            events.forEach(e -> e.setViews(viewsById.get(e.getId())));
        }
        return events;
    }

    private Map<Integer, Integer> getViewsByIds(List<Event> events) {
        List<Integer> ids = events.stream()
                .map(Event::getId).collect(Collectors.toList());
        Map<Integer, Integer> resultMap = new HashMap<>();
        String[] uris = new String[ids.size()];
        for (int i = 0; i < ids.size(); i ++) {
            uris[i] = "/events/" + ids.get(i);
        }
        LocalDateTime start = findEarliestEventDate(events);
        LocalDateTime end = LocalDateTime.now();
        ResponseEntity<Object> result = statsClient.getStats(start.format(dateTimeFormatter), end.format(dateTimeFormatter), uris, true);
        Object statsObject = result.getBody();
        if (statsObject != null) {
            List<ViewStats> stats = Arrays.asList(new ObjectMapper().convertValue(statsObject, ViewStats[].class));
            if (!stats.isEmpty()) {
                for (ViewStats viewStats : stats) {
                    String[] url = viewStats.getUri().split("/");
                    resultMap.put(Integer.parseInt(url[2]), viewStats.getHits());
                }
            }
        }
        return resultMap;
    }

    private LocalDateTime findEarliestEventDate(List<Event> events) {
        if (!events.isEmpty()) {
            return events.stream()
                    .filter(e -> e.getPublishedOn() != null)
                    .map(Event::getPublishedOn)
                    .min(Comparator.naturalOrder())
                    .orElse(LocalDateTime.now().minusYears(5));
        }
        return LocalDateTime.now().minusYears(5);
    }
}
