package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class UserControllerTest {
    private final UserController controller;

    @Autowired
    public UserControllerTest(UserController controller) {
        this.controller = controller;
    }

    @Test
    void shouldBeOkCreateCorrectUser() {
        User user = User.builder()
                .email("mail@etcdev.ru")
                .name("Tester")
                .login("Tester")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User savedUser = controller.create(user);
        Assertions.assertEquals(1, controller.findAll().size());
        Assertions.assertEquals(user.getLogin(), savedUser.getLogin());
        Assertions.assertEquals(user.getName(), savedUser.getName());
        Assertions.assertEquals(user.getEmail(), savedUser.getEmail());
        Assertions.assertEquals(user.getBirthday(), savedUser.getBirthday());
    }

    @Test
    void shouldBeOkUpdateCorrectUser() {
        User user = User.builder()
                .email("mail@etcdev.ru")
                .name("Tester")
                .login("Tester")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User savedUser = controller.create(user);
        savedUser.setName("Developer");
        User updatedUser = controller.update(savedUser);
        Assertions.assertEquals(updatedUser.getLogin(), savedUser.getLogin());
        Assertions.assertEquals(updatedUser.getName(), savedUser.getName());
        Assertions.assertEquals(updatedUser.getEmail(), savedUser.getEmail());
        Assertions.assertEquals(updatedUser.getBirthday(), savedUser.getBirthday());
    }

    @Test
    void shouldGetCorrectFilmsList() {
        List<User> users = controller.findAll();
        User user = User.builder()
                .email("mail@sganiev.ru")
                .name("Dev")
                .login("Dev")
                .birthday(LocalDate.of(1998, 1, 1))
                .build();
        controller.create(user);
        List<User> users2 = controller.findAll();
        Assertions.assertEquals(users.size() + 1, users2.size());
    }
}
