package ru.practicum.service.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundObjectException;
import ru.practicum.model.User;
import ru.practicum.pageable.OffsetBasedPageRequest;
import ru.practicum.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    public List<User> findUsersAdmin(List<Integer> ids, Integer from, Integer size) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(size, from, null);
        if (ids.size() == 0) {
            return userRepository.findBy(pageable);
        } else {
            return userRepository.findByIdIn(ids, pageable);
        }
    }

    public User createUserAdmin(User user) {
        return userRepository.save(user);
    }

    public void deleteUserByIdAdmin(Integer userId) {
        getUserByIdOrThrowException(userId);
        userRepository.deleteById(userId);
    }

    public User findUserById(Integer userId) {
        return getUserByIdOrThrowException(userId);
    }

    private User getUserByIdOrThrowException(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundObjectException("Объект не был найден"));
    }

}

