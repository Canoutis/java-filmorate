package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isEmpty()) user.setName(user.getLogin());
        return userStorage.create(user);
    }

    public User update(User user) {
        if (user.getName() == null || user.getName().isEmpty()) user.setName(user.getLogin());
        return userStorage.update(user);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriendToUser(int userId, int friendId) {
        return userStorage.addFriendToUser(userId, friendId);
    }

    public User removeFriendFromUser(int userId, int friendId) {
        return userStorage.removeFriendFromUser(userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        return userStorage.getUserFriends(userId);
    }

    public List<User> getMutualFriends(int userId, int targetId) {
        return userStorage.getMutualFriends(userId, targetId);
    }

    public void removeUserById(int userId) {
        userStorage.removeUserById(userId);
    }

    public List<Film> getRecommendations(int userId) {
        return userStorage.getRecommendations(userId);
    }
}
