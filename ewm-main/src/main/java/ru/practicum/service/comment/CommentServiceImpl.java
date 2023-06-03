package ru.practicum.service.comment;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.enums.State;
import ru.practicum.pageable.OffsetBasedPageRequest;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {
    CommentRepository commentRepository;
    UserRepository userRepository;
    EventRepository eventRepository;

    public List<Comment> getCommentsPublic(String text, List<Integer> events, boolean onlyModified,
                                           boolean onlyPositive, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           Integer from, Integer size) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(size, from, null);
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new BadRequestException("Время начала не может быть позже окончания!");
            }
        }
        LocalDateTime currentTime = LocalDateTime.now();
        if (!Objects.equals(text, "")) {
            text = text.toLowerCase();
        }
        Boolean positive = null;
        if (onlyPositive) {
            positive = true;
        }
        Boolean modified = null;
        if (onlyModified) {
            modified = true;
        }
        if (rangeStart == null || rangeEnd == null) {
            return commentRepository.getComments(text, events, positive, modified, currentTime, pageable);
        } else {
            return commentRepository.getComments(text, events, positive, modified, rangeStart, rangeEnd, pageable);
        }
    }

    public Comment findCommentByIdPublic(Integer commentId) {
        return commentRepository.getCommentByIdOrThrowException(commentId);
    }

    public Comment createCommentPrivate(Integer userId, Comment comment) {
        User user = userRepository.getUserByIdOrThrowException(userId);
        Event event = eventRepository.getEventByIdOrThrowException(comment.getEventId());
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Оставить комментарий можно только к опубликованному событию!");
        }
        comment.setUserId(userId);
        return commentRepository.save(comment);
    }

    public List<Comment> findCommentsByUserPrivate(Integer userId) {
        return commentRepository.findByUserId(userId);
    }

    public void deleteCommentByIdPrivate(Integer userId, Integer commentId) {
        Comment comment = commentRepository.getCommentByIdOrThrowException(commentId);
        if (!Objects.equals(comment.getUserId(), userId)) {
            throw new ConflictException("У данного пользователя нет доступа к данной операции");
        }
        commentRepository.deleteById(commentId);
    }

    public Comment updateCommentPrivate(Integer userId, Integer commentId, Comment newComment) {
        Comment existingComment = commentRepository.getCommentByIdOrThrowException(commentId);
        if (!Objects.equals(existingComment.getUserId(), userId)) {
            throw new ConflictException("У данного пользователя нет доступа к данной операции");
        }
        if (newComment.getModifiedOn().isAfter(existingComment.getCreatedOn().plus(2, ChronoUnit.HOURS))) {
            throw new ConflictException("Редактировать комментарий можно только в течение 2 часов после публикации");
        }
        return changeCommentFields(newComment, existingComment);
    }

    private Comment changeCommentFields(Comment newComment, Comment oldComment) {
        oldComment.setText(newComment.getText());
        if (newComment.getIsPositive() != null) {
            oldComment.setIsPositive(newComment.getIsPositive());
        }
        oldComment.setWasModified(true);
        oldComment.setModifiedOn(newComment.getModifiedOn());
        return commentRepository.save(oldComment);
    }

    public void deleteCommentByIdAdmin(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    public void deleteCommentsByEventIdAdmin(Integer eventId) {
        commentRepository.deleteByEventId(eventId);
    }

    public List<Comment> findCommentsByEventIdAdmin(Integer eventId, Integer from, Integer size) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(size, from, null);
        return commentRepository.findByEventId(eventId, pageable);
    }
}

