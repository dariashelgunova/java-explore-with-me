package ru.practicum.service.comment;

import ru.practicum.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    List<Comment> getCommentsPublic(String text, List<Integer> events, boolean onlyModified,
                                    boolean onlyPositive, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    Integer from, Integer size);

    Comment createCommentPrivate(Integer userId, Comment comment);

    Comment findCommentByIdPublic(Integer commentId);

    List<Comment> findCommentsByUserPrivate(Integer userId);

    void deleteCommentByIdPrivate(Integer userId, Integer commentId);

    Comment updateCommentPrivate(Integer userId, Integer commentId, Comment newComment);

    void deleteCommentByIdAdmin(Integer commentId);

    void deleteCommentsByEventIdAdmin(Integer eventId);

    List<Comment> findCommentsByEventIdAdmin(Integer eventId, Integer from, Integer size);
}
