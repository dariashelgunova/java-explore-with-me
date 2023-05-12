package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;
import ru.practicum.view.HitView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class HitMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Hit fromEndpointHit(EndpointHit hitDto) {
        if (hitDto == null) {
            return null;
        }

        Hit hit = new Hit();

        hit.setId(hitDto.getId());
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        if (hitDto.getTimestamp() != null) {
            hit.setTimestamp(LocalDateTime.parse(hitDto.getTimestamp(), FORMATTER));
        }
        return hit;
    }

    public EndpointHit toEndpointHit(Hit hit) {
        if (hit == null) {
            return null;
        }
        EndpointHit hitDto = new EndpointHit();

        hitDto.setId(hit.getId());
        hitDto.setApp(hit.getApp());
        hitDto.setUri(hit.getUri());
        hitDto.setIp(hit.getIp());
        hitDto.setTimestamp(hit.getTimestamp().toString());

        return hitDto;
    }

    public List<ViewStats> toViewStatsList(List<Hit> hits) {
        List<ViewStats> result = new ArrayList<>();
        for (Hit hit : hits) {
            result.add(toViewStats(hit));
        }
        return result;
    }

    public ViewStats toViewStats(Hit hit) {
        if (hit == null) {
            return null;
        }

        ViewStats viewStats = new ViewStats();

        viewStats.setApp(hit.getApp());
        viewStats.setUri(hit.getUri());
        viewStats.setHits(hit.getHitsAmount());

        return viewStats;
    }

    public List<ViewStats> toViewStatsListFromView(List<HitView> hits) {
        List<ViewStats> result = new ArrayList<>();
        for (HitView hit : hits) {
            result.add(toViewStatsFromView(hit));
        }
        return result;
    }

    public ViewStats toViewStatsFromView(HitView hit) {
        if (hit == null) {
            return null;
        }

        ViewStats viewStats = new ViewStats();

        viewStats.setApp(hit.getApp());
        viewStats.setUri(hit.getUri());
        viewStats.setHits(hit.getHitsAmount());

        return viewStats;
    }


    public LocalDateTime toLocalDateTime(String time) {
        if (time == null) {
            return null;
        }
        return LocalDateTime.parse(time, FORMATTER);
    }
}
