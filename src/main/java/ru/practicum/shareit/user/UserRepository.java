package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Integer id);

    Optional<User> findByEmail(String email);

    User save(User user);

    User update(User user);

    void deleteById(Integer id);
}
