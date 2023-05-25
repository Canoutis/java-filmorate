package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriendToUser(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(user.getId());
        return user;
    }

    public User removeFriendFromUser(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        user.getFriends().remove(friendId);
        User friend = userStorage.getUserById(friendId);
        friend.getFriends().remove(user.getId());
        return user;
    }

    public List<User> getUserFriends(int userId) {
        User user = userStorage.getUserById(userId);
        List<User> result = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            result.add(userStorage.getUserById(friendId));
        }
        return result;
    }

    public Set<User> getMutualFriends(int userId, int targetId) {
        User user = userStorage.getUserById(userId);
        User target = userStorage.getUserById(targetId);
        Set<User> result = new HashSet<>();
        if (user.getFriends() != null) {
            for (int friendId : user.getFriends()) {
                if (target.getFriends().contains(friendId))
                    result.add(userStorage.getUserById(friendId));
            }
        }
        return result;
    }
}
