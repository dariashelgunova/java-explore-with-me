package ru.practicum.controller.publics;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.service.comment.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentControllerPublic {
    CommentService commentService;
    CommentMapper commentMapper;

    @GetMapping
    public List<CommentDto> getCommentsByEventId(@RequestParam(defaultValue = "") String text,
                                                 @RequestParam(defaultValue = "") List<Integer> events,
                                                 @RequestParam(defaultValue = "false") Boolean onlyModified,
                                                 @RequestParam(defaultValue = "false") Boolean onlyPositive,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {

        List<Comment> result = commentService.getCommentsPublic(text, events, onlyModified, onlyPositive, rangeStart,
                rangeEnd, from, size);
        return commentMapper.toDtoList(result);
    }

    @GetMapping("/{commentId}")
    public CommentDto findCommentById(@PathVariable("commentId") Integer commentId) {
        Comment result = commentService.findCommentByIdPublic(commentId);
        return commentMapper.toDto(result);
    }
}
