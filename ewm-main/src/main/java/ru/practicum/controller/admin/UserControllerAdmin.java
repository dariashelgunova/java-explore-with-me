package ru.practicum.controller.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.service.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserControllerAdmin {

    UserService userService;
    UserMapper userMapper;

    @GetMapping
    public List<UserDto> getAdminUsers(
            @RequestParam(required = false) List<Integer> ids,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<User> result = userService.findUsersAdmin(ids, from, size);
        return userMapper.toDtoList(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createAdminUsers(@Valid @RequestBody NewUserRequest userDto) {
        User newUser = userMapper.fromDto(userDto);
        User createdUser = userService.createUserAdmin(newUser);
        return userMapper.toDto(createdUser);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdminUserById(@PathVariable("userId") Integer userId) {
        userService.deleteUserByIdAdmin(userId);
    }
}
