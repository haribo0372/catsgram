package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
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

    @PutMapping
    public User update(@RequestBody User user) {
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

    private Optional<User> findByEmail(String email) {
        return users.values().stream().filter(i -> i.getEmail().equals(email)).findAny();
    }

    private long getNextId() {
        long currentMaxUserId = users.keySet()
                .stream()
                .mapToLong(id -> id).max()
                .orElse(0);
        return ++currentMaxUserId;
    }
}
