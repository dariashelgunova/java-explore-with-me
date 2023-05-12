package ru.practicum.service;

import ru.practicum.model.Hit;
import ru.practicum.view.HitView;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    Hit saveStats(Hit hit);

    List<HitView> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
