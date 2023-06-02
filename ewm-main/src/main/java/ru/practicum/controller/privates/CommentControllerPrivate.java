package ru.practicum.controller.privates;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.service.comment.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentControllerPrivate {
    CommentService commentService;
    CommentMapper commentMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable("userId") Integer userId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        Comment newComment = commentMapper.fromDto(newCommentDto);
        Comment createdComment = commentService.createCommentPrivate(userId, newComment);
        return commentMapper.toDto(createdComment);

    }

    @GetMapping
    public List<CommentDto> findCommentsByUserId(@PathVariable("userId") Integer userId) {
        List<Comment> result = commentService.findCommentsByUserPrivate(userId);
        return commentMapper.toDtoList(result);

    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable("userId") Integer userId,
                                  @PathVariable("commentId") Integer commentId) {
        commentService.deleteCommentByIdPrivate(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable("userId") Integer userId,
                                    @PathVariable("commentId") Integer commentId,
                                    @Valid @RequestBody UpdateCommentDto commentDto) {
        Comment newComment = commentMapper.fromDto(commentDto);
        Comment comment = commentService.updateCommentPrivate(userId, commentId, newComment);
        return commentMapper.toDto(comment);
    }
}
