package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatsService;
import ru.practicum.view.HitView;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsController {

    StatsService statsService;
    HitMapper hitMapper;


    @PostMapping("/hit")
    public EndpointHit saveStats(@RequestBody EndpointHit hit) {
        Hit givenHit = hitMapper.fromEndpointHit(hit);
        Hit result = statsService.saveStats(givenHit);
        return hitMapper.toEndpointHit(result);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        LocalDateTime startTime = hitMapper.toLocalDateTime(start);
        LocalDateTime endTime = hitMapper.toLocalDateTime(end);
        List<HitView> result = statsService.getStats(startTime, endTime, uris, unique);
        return hitMapper.toViewStatsListFromView(result);
    }

}
