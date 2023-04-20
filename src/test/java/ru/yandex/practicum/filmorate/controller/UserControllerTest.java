package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.UserSaveException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
public class UserControllerTest {
    @Autowired
    private UserController controller;

    @Test
    void shouldThrowExceptionWithEmptyEmail() {
        User user = User.builder()
                .email("")
                .name("Tester")
                .login("Tester")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Assertions.assertThrows(UserSaveException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowExceptionWithIncorrectEmail() {
        User user = User.builder()
                .email("incorrect")
                .name("Tester")
                .login("Tester")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Assertions.assertThrows(UserSaveException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowExceptionWithEmptyLogin() {
        User user = User.builder()
                .email("mail@etcdev.ru")
                .name("Tester")
                .login("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Assertions.assertThrows(UserSaveException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowExceptionWithSpaceInLogin() {
        User user = User.builder()
                .email("mail@etcdev.ru")
                .name("Tester")
                .login("tes ter")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Assertions.assertThrows(UserSaveException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowExceptionWithFutureBirthday() {
        User user = User.builder()
                .email("mail@etcdev.ru")
                .name("Tester")
                .login("tester")
                .birthday(LocalDate.of(2024, 1, 1))
                .build();
        Assertions.assertThrows(UserSaveException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowExceptionWithEmptyRequest() {
        User user = User.builder().build();
        Assertions.assertThrows(NullPointerException.class, () -> controller.create(user));
    }

    @Test
    void shouldBeOkWithCorrectUser() {
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
}
