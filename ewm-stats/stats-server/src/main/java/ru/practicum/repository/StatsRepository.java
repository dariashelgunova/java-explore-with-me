package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;
import ru.practicum.view.HitView;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Integer> {

    @Query(value = "select h.app, h.uri, count(DISTINCT h.ip) as hitsAmount " +
            "from hits h " +
            "where h.time between ?1 AND ?2 AND h.uri IN ?3 " +
            "group by h.app, h.uri " +
            "order by count(DISTINCT h.ip) DESC", nativeQuery = true)
    List<HitView> getStats(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique);

    @Query(value = "select h.app, h.uri, count(h.ip) as hitsAmount " +
            "from hits h " +
            "where h.time between ?1 AND ?2 AND h.uri IN ?3 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) DESC", nativeQuery = true)
    List<HitView> getStats(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query(value = "select h.app, h.uri, count(DISTINCT h.ip) as hitsAmount " +
            "from hits h " +
            "where h.time between ?1 AND ?2 " +
            "group by h.app, h.uri " +
            "order by count(DISTINCT h.ip) DESC", nativeQuery = true)
    List<HitView> getStats(LocalDateTime start, LocalDateTime end, boolean unique);

    @Query(value = "select h.app, h.uri,  count(h.ip) as hitsAmount " +
            "from hits h " +
            "where h.time between ?1 AND ?2 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) DESC", nativeQuery = true)
    List<HitView> getStats(LocalDateTime start, LocalDateTime end);
}
