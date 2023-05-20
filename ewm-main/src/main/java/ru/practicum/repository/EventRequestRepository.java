package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.EventRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRequestRepository extends JpaRepository<EventRequest, Integer> {
    List<EventRequest> findByUserId(Integer userId);
    List<EventRequest> findByUserIdAndEventId(Integer userId, Integer eventId);
    @Query(value = "count(DISTINCT r.user_id)" +
            "from requests r " +
            "where r.event_id = ?1 AND r.status = confirmed " +
            "group by r.event_id " +
            "order by count(DISTINCT r.user_id) DESC", nativeQuery = true)
    Integer findParticipantsAmount(Integer eventId);
    List<EventRequest> findByEventId(Integer eventId);
    List<EventRequest> findByIdIn(List<Integer> id);
}
