package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatsService;
import ru.practicum.view.HitView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsController {

    StatsService statsService;
    HitMapper hitMapper;


    @PostMapping("/hit")
    public EndpointHit saveStats(@RequestBody EndpointHit hit, HttpServletRequest request) {
        log.debug("Request Info = " + request.toString());
        Hit givenHit = hitMapper.fromEndpointHit(hit);
        Hit result = statsService.saveStats(givenHit);
        return hitMapper.toEndpointHit(result);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") boolean unique,
                                    HttpServletRequest request) {
        log.debug("Request Info = " + request.toString());
        List<HitView> result = statsService.getStats(start, end, uris, unique);
        return hitMapper.toViewStatsListFromView(result);
    }

}
