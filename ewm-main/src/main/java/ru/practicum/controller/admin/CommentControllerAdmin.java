package ru.practicum.controller.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentControllerAdmin {
    CommentService commentService;
    CommentMapper commentMapper;

    @GetMapping("/{eventId}")
    public List<CommentDto> getCommentsByEventId(@PathVariable("eventId") Integer eventId) {
        List<Comment> result = commentService.findCommentsByEventIdAdmin(eventId);
        return commentMapper.toDtoList(result);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentsByEventId(@PathVariable("eventId") Integer eventId) {
        commentService.deleteCommentsByEventIdAdmin(eventId);
    }
}
