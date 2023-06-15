package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int generationId = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(++generationId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ObjectNotFoundException(
                    String.format("Ошибка обновления пользователя. Пользователь не найден! Id=%d", user.getId()));
        } else {
            users.put(user.getId(), user);
            return user;
        }
    }

    @Override
    public User getUserById(int userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь не найден! Id=%d", userId));
        }
    }

    @Override
    public User removeFriendFromUser(int userId, int friendId) {
        User user = getUserById(userId);
        user.getFriends().remove(friendId);
        User friend = getUserById(friendId);
        friend.getFriends().remove(user.getId());
        return user;
    }

    public User addFriendToUser(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(user.getId());
        return user;
    }

    public List<User> getUserFriends(int userId) {
        User user = getUserById(userId);
        List<User> result = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            result.add(getUserById(friendId));
        }
        return result;
    }

    @Override
    public List<User> getMutualFriends(int userId, int targetId) {
        User user = getUserById(userId);
        User target = getUserById(targetId);
        List<User> result = new ArrayList<>();
        if (user.getFriends() != null) {
            for (int friendId : user.getFriends()) {
                if (target.getFriends().contains(friendId))
                    result.add(getUserById(friendId));
            }
        }
        return result;
    }

    @Override
    public void removeUserById(int userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь не найден! Id=%d", userId));
        }
    }

    @Override
    public List<Event> getFeed(int userId) {
        return null;
    }

    @Override
    public void addEvent(Event event) {
    }
}
