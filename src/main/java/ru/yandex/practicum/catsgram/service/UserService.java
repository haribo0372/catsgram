package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new ConditionsNotMetException("Имейл должен быть указан");

        findByEmail(user.getEmail()).ifPresent(i -> {
            throw new DuplicatedDataException("Этот имейл уже используется");
        });

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);

        return user;
    }

    public User update(User user) {
        if (user.getId() == null)
            throw new ConditionsNotMetException("Id должен быть указан");

        findByEmail(user.getEmail()).ifPresent(i -> {
            if (!i.equals(user))
                throw new DuplicatedDataException("Этот имейл уже используется");
        });

        if (user.getEmail() == null || user.getUsername() == null || user.getPassword() == null)
            return user;

        users.put(user.getId(), user);

        return user;
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream().filter(i -> i.getEmail().equals(email)).findAny();
    }

    public Optional<User> findById(long id) {
        return users.values().stream().filter(i -> Objects.equals(i.getId(), id)).findAny();
    }

    private long getNextId() {
        long currentMaxUserId = users.keySet()
                .stream()
                .mapToLong(id -> id).max()
                .orElse(0);
        return ++currentMaxUserId;
    }
}
