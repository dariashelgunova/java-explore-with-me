package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.exception.NotFoundObjectException;
import ru.practicum.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(value = "select c " +
            "from Comment c " +
            "where lower(c.text) like (concat('%', :text, '%')) " +
            "and (:events is null or c.eventId in :events) " +
            "and (:positive is null or c.isPositive = :positive) " +
            "and (:modified is null or c.wasModified = :modified) " +
            "and c.createdOn between :start and :finish ")
    List<Comment> getComments(@Param("text") String text, @Param("events") List<Integer> events,
                              @Param("positive") Boolean positive, @Param("modified") Boolean modified,
                              @Param("start") LocalDateTime start, @Param("finish") LocalDateTime end,
                              Pageable pageable);

    @Query(value = "select c " +
            "from Comment c " +
            "where lower(c.text) like (concat('%', :text, '%')) " +
            "and (:events is null or c.eventId in :events) " +
            "and (:positive is null or c.isPositive = :positive) " +
            "and (:modified is null or c.wasModified = :modified) " +
            "and c.createdOn > :currentTime ")
    List<Comment> getComments(@Param("text") String text, @Param("events") List<Integer> events,
                              @Param("positive") Boolean positive, @Param("modified") Boolean modified,
                              @Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    default Comment getCommentByIdOrThrowException(Integer commentId) {
        return findById(commentId)
                .orElseThrow(() -> new NotFoundObjectException("Объект не был найден"));
    }

    List<Comment> findByUserId(Integer userId);

    List<Comment> findByEventId(Integer eventId);
}
