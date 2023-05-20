package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectSaveException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int generationId = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public boolean contains(int userId) {
        return users.containsKey(userId);
    }

    public User create(User user) {
        if (isInvalidUser(user)) {
            log.warn("Ошибка создания пользователя. Ошибка входных данных! " + user);
            throw new ObjectSaveException("Ошибка создания пользователя. Ошибка входных данных!");
        } else {
            if (user.getName() == null || user.getName().isEmpty()) user.setName(user.getLogin());
            user.setId(++generationId);
            user.setFriends(new HashSet<>());
            users.put(user.getId(), user);
            return user;
        }
    }

    public User update(User user) {
        if (isInvalidUser(user) || !users.containsKey(user.getId())) {
            log.warn("Ошибка обновления пользователя с id={}. Ошибка входных данных! " + user, user.getId());
            throw new ObjectUpdateException("Ошибка обновления пользователя. Ошибка входных данных!");
        } else {
            if (user.getFriends() == null) user.setFriends(new HashSet<>());
            users.put(user.getId(), user);
            return user;
        }
    }

    public User getUserById(int userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new ObjectNotFoundException("Пользователь не найден!");
        }
    }

    private boolean isInvalidUser(User user) {
        return !user.getEmail().contains("@")
                || user.getLogin().isEmpty()
                || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now());
    }
}
