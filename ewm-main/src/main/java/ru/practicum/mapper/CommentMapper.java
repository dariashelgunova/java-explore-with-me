package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.model.Comment;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    Comment fromDto(NewCommentDto dto);

    Comment fromDto(UpdateCommentDto dto);

    CommentDto toDto(Comment comment);

    List<CommentDto> toDtoList(List<Comment> comments);
}
