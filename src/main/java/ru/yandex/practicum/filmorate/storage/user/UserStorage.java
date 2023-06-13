package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User user);

    User getUserById(int id);

    User removeFriendFromUser(int filmId, int userId);

    User addFriendToUser(int userId, int friendId);

    List<User> getUserFriends(int userId);

    List<User> getMutualFriends(int userId, int targetId);
}
