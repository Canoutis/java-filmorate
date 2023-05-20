package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return inMemoryUserStorage.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        return inMemoryUserStorage.update(user);
    }

    @GetMapping(value = "/users/{id}")
    public User getUserById(@PathVariable int id) {
        return inMemoryUserStorage.getUserById(id);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public User addFriendToUser(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriendToUser(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public User removeFriendFromUser(@PathVariable int id, @PathVariable int friendId) {
        return userService.removeFriendFromUser(id, friendId);
    }

    @GetMapping(value = "/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
        return userService.getUserFriends(id);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public Set<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getMutualFriends(id, otherId);
    }
}
