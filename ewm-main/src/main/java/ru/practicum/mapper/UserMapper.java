package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User fromDto(NewUserRequest dto);

    User fromDto(UserDto dto);

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    UserShortDto toShortDto(User user);

    List<UserShortDto> toDtoShortList(List<User> users);

}
