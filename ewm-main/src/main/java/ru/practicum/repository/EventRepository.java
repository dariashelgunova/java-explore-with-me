package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;
import ru.practicum.model.enums.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByCategoryId(Integer categoryId);

    List<Event> findByInitiatorId(Integer initiatorId, Pageable pageable);

    @Query(value = "select e " +
            "from Event e " +
            "where (:initiator_id is null or e.initiator.id in :initiator_id) " +
            "and (:state is null or e.state in :state) " +
            "and (:category_id is null or e.category.id in :category_id) " +
            "and e.eventDate between :start and :finish")
    List<Event> findAllAdmin(@Param("initiator_id") List<Integer> listUserIds,
                             @Param("state") List<State> listStates,
                             @Param("category_id") List<Integer> listCategoriesIds,
                             @Param("start") LocalDateTime rangeStart,
                             @Param("finish") LocalDateTime rangeEnd,
                             Pageable pageable);

    @Query(value = "select e " +
            "from Event e " +
            "where lower(e.annotation) like (concat('%', :annotation, '%')) " +
            "or lower(e.description) like (concat('%', :description, '%'))" +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and e.eventDate between :start and :finish " +
            "and (:onlyAvailable is null or e.available = :onlyAvailable) " +
            "and e.state = 'PUBLISHED' ")
    List<Event> getEvents(@Param("annotation") String annotationText, @Param("description") String descriptionText,
                          @Param("categories") List<Integer> categoryIds, @Param("paid") Boolean paid,
                          @Param("start") LocalDateTime start, @Param("finish") LocalDateTime end,
                          @Param("onlyAvailable") Boolean onlyAvailable, Pageable pageable);


    @Query(value = "select e " +
            "from Event e " +
            "where lower(e.annotation) like (concat('%', :annotation, '%')) " +
            "or lower(e.description) like (concat('%', :description, '%')) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and e.eventDate > :currentTime " +
            "and (:onlyAvailable is null or e.available = :onlyAvailable) " +
            "and e.state = 'PUBLISHED'")
    List<Event> getEvents(@Param("annotation") String annotationText, @Param("description") String descriptionText,
                          @Param("categories") List<Integer> categoryIds, @Param("paid") Boolean paid,
                          @Param("currentTime") LocalDateTime currentTime, @Param("onlyAvailable") Boolean onlyAvailable,
                          Pageable pageable);

    @Query(value = "select e.* " +
            "from events as e " +
            "where e.id = ?1 " +
            "and e.state = 'PUBLISHED' " +
            "group by e.id ", nativeQuery = true)
    List<Event> getPublicEventById(Integer eventId);

    @Query(value = "select e " +
            "from Event e " +
            "where ((:ids) is null or e.id in (:ids)) ")
    List<Event> findByIds(@Param("ids") List<Integer> ids);
}
