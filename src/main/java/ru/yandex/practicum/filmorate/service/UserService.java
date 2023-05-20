package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addFriendToUser(int userId, int friendId) {
        if (inMemoryUserStorage.contains(userId) && inMemoryUserStorage.contains(friendId)) {
            User user = inMemoryUserStorage.getUserById(userId);
            user.getFriends().add(friendId);
            User friend = inMemoryUserStorage.getUserById(friendId);
            friend.getFriends().add(user.getId());
            return user;
        } else {
            throw new ObjectNotFoundException("Пользователь не найден!");
        }
    }

    public User removeFriendFromUser(int userId, int friendId) {
        User user = inMemoryUserStorage.getUserById(userId);
        user.getFriends().remove(friendId);
        User friend = inMemoryUserStorage.getUserById(friendId);
        friend.getFriends().remove(user.getId());
        return user;
    }

    public List<User> getUserFriends(int userId) {
        User user = inMemoryUserStorage.getUserById(userId);
        Set<User> result = new HashSet<>();
        for (int friendId : user.getFriends()) {
            result.add(inMemoryUserStorage.getUserById(friendId));
        }
        return result.stream().sorted(Comparator.comparing(User::getId)).collect(Collectors.toList());
    }

    public Set<User> getMutualFriends(int userId, int targetId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User target = inMemoryUserStorage.getUserById(targetId);
        Set<User> result = new HashSet<>();
        if (user.getFriends() != null) {
            for (int friendId : user.getFriends()) {
                if (target.getFriends().contains(friendId))
                    result.add(inMemoryUserStorage.getUserById(friendId));
            }
        }
        return result;
    }

}
