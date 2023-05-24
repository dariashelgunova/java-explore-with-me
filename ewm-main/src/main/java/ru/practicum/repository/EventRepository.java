package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.enums.State;
import ru.practicum.view.EventView;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByCategoryId(Integer categoryId);
    List<Event> findByInitiatorId(Integer initiatorId, Pageable pageable);
    List<Event> findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(List<Integer> listUserIds,
                                                                                List<State> listStates,
                                                                                List<Integer> listCategoriesIds,
                                                                                LocalDateTime rangeStart,
                                                                                LocalDateTime rangeEnd,
                                                                                Pageable pageable);
    @Query(value = "select e.* " +
                "from events as e " +
                "where lower(e.annotation) like ?1 " +
                "or lower(e.description) like ?2 " +
                "and e.category_id in ?3 " +
                "and e.paid = ?4 " +
                "and e.event_date between ?5 and ?6 " +
                "and e.available = ?7 " +
                "and e.state = 'Published' " +
                "group by e.id ", nativeQuery = true)
    List<Event> getEvents(String annotationText, String descriptionText, List<Integer> categoryIds, Boolean paid,
                              LocalDateTime start, LocalDateTime end, Boolean onlyAvailable, Pageable pageable);
    @Query(value = "select e.* " +
            "from events as e " +
            "where lower(e.annotation) like ?1 " +
            "or lower(e.description) like ?2 " +
            "and e.category_id in ?3 " +
            "and e.event_date between ?4 and ?5 " +
            "and e.available = ?6 " +
            "and e.state = 'PUBLISHED' " +
            "group by e.id ", nativeQuery = true)
    List<Event> getEvents(String annotationText, String descriptionText, List<Integer> categoryIds, LocalDateTime start,
                          LocalDateTime end, Boolean onlyAvailable, Pageable pageable);
    @Query(value = "select e.* " +
            "from events as e " +
            "where lower(e.annotation) like ?1 " +
            "or lower(e.description) like ?2 " +
            "and e.category_id in ?3 " +
            "and e.event_date > ?4 " +
            "and e.available = ?5 " +
            "and e.state = 'PUBLISHED' " +
            "group by e.id ", nativeQuery = true)
    List<Event> getEvents(String annotationText, String descriptionText, List<Integer> categoryIds,
                          LocalDateTime currentTime, Boolean onlyAvailable, Pageable pageable);
    @Query(value = "select e.* " +
            "from events as e " +
            "where lower(e.annotation) like ?1 " +
            "or lower(e.description) like ?2 " +
            "and e.category_id in ?3 " +
            "and e.paid = ?4 " +
            "and e.event_date > ?5 " +
            "and e.available = ?6 " +
            "and e.state = 'PUBLISHED' " +
            "group by e.id ", nativeQuery = true)
    List<Event> getEvents(String annotationText, String descriptionText, List<Integer> categoryIds, Boolean paid,
                          LocalDateTime currentTime, Boolean onlyAvailable, Pageable pageable);
    @Query(value = "select e.* " +
            "from events as e " +
            "where e.id = ?1 " +
            "and e.state = 'PUBLISHED' " +
            "group by e.id " , nativeQuery = true)
    List<Event> getPublicEventById(Integer eventId);
    List<Event> findByIdIn(List<Integer> ids);
}
