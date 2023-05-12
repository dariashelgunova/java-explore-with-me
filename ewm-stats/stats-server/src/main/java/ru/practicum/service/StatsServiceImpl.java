package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;
import ru.practicum.view.HitView;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsServiceImpl implements StatsService {

    StatsRepository repository;

    public Hit saveStats(Hit hit) {
        return repository.save(hit);
    }

    public List<HitView> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {

        if (unique) {
            if (uris == null) {
                return repository.getStats(start, end, unique);
            } else {
                List<String> urisList = Arrays.asList(uris);
                return repository.getStats(start, end, urisList, unique);
            }
        } else {
            if (uris == null) {
                return repository.getStats(start, end);
            } else {
                List<String> urisList = Arrays.asList(uris);
                return repository.getStats(start, end, urisList);
            }
        }
    }
}

