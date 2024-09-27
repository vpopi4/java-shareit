package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserInMemoryRepository implements UserRepository {
    private final HashMap<Integer, User> users;
    private final HashMap<String, Integer> emails;

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(users.get(emails.get(email)));
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user.getId());

        return user;
    }

    @Override
    public User update(User user) {
        return save(user);
    }

    @Override
    public void deleteById(Integer id) {
        findById(id).ifPresent(user -> {
            users.remove(id);
            emails.remove(user.getEmail());
        });
    }
}
