package ru.practicum.service.user;

import ru.practicum.model.User;

import java.util.List;

public interface UserService {
    List<User> findUsersAdmin(List<Integer> ids, Integer from, Integer size);

    User createUserAdmin(User user);

    void deleteUserByIdAdmin(Integer userId);

    User findUserById(Integer userId);
}
