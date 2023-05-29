package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EventRequest;
import ru.practicum.view.RequestView;

import java.util.List;

public interface EventRequestRepository extends JpaRepository<EventRequest, Integer> {
    List<EventRequest> findByUserId(Integer userId);

    List<EventRequest> findByUserIdAndEventId(Integer userId, Integer eventId);

    @Query(value = "select count(DISTINCT r.user_id) " +
            "from requests r " +
            "where r.event_id = ?1 AND r.status = 'CONFIRMED' " +
            "group by r.event_id " +
            "order by count(DISTINCT r.user_id) DESC", nativeQuery = true)
    Integer findParticipantsAmount(Integer eventId);

    @Query(value = "select r.event_id as eventId, count(DISTINCT r.user_id) as confirmedRequests " +
            "from requests r " +
            "where r.event_id = ?1 AND r.status = 'CONFIRMED' " +
            "group by r.event_id " +
            "order by count(DISTINCT r.user_id) DESC", nativeQuery = true)
    RequestView findParticipantsAmountView(Integer eventId);

    @Query(value = "select r.event_id as eventId, count(DISTINCT r.user_id) as confirmedRequests " +
            "from requests r " +
            "where r.event_id in ?1 AND r.status = 'CONFIRMED' " +
            "group by r.event_id " +
            "order by count(DISTINCT r.user_id) DESC", nativeQuery = true)
    List<RequestView> findParticipantsAmountView(List<Integer> eventIds);

    List<EventRequest> findByEventId(Integer eventId);

    List<EventRequest> findByIdIn(List<Integer> id);
}
