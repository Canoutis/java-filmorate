package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping(value = "/users/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public User addFriendToUser(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriendToUser(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public User removeFriendFromUser(@PathVariable int id, @PathVariable int friendId) {
        return userService.removeFriendFromUser(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}")
    public void removeUserById(@PathVariable int id) {
        userService.removeUserById(id);
    }

    @GetMapping(value = "/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
        return userService.getUserFriends(id);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getMutualFriends(id, otherId);
    }

}
