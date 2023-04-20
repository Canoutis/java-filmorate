package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.UserSaveException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private int generationId = 0;
    private final HashMap<Integer, User> hmUsers = new HashMap<>();

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(hmUsers.values());
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        if (!isValidUser(user)) {
            log.warn("Ошибка создания пользователя. Ошибка входных данных! " + user);
            throw new UserSaveException("Ошибка создания пользователя. Ошибка входных данных!");
        } else {
            if (user.getName() == null || user.getName().isEmpty()) user.setName(user.getLogin());
            user.setId(++generationId);
            hmUsers.put(user.getId(), user);
            return user;
        }
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        if (!isValidUser(user) || !hmUsers.containsKey(user.getId())) {
            log.warn("Ошибка обновления пользователя с id={}. Ошибка входных данных! " + user, user.getId());
            throw new UserSaveException("Ошибка создания пользователя. Ошибка входных данных!");
        } else {
            hmUsers.put(user.getId(), user);
            return user;
        }
    }

    private boolean isValidUser(User user) {
        return user.getEmail().contains("@")
                && !user.getLogin().isEmpty()
                && !user.getLogin().contains(" ")
                && !user.getBirthday().isAfter(LocalDate.now());
    }
}
