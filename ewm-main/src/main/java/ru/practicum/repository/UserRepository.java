package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByIdIn(List<Integer> ids, Pageable pageable);

    List<User> findBy(Pageable pageable);

    List<User> findByName(String name);
}
