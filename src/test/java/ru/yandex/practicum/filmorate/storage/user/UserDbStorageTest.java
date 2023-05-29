package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    public void testOneUserCreate() {
        User tempUser = User.builder()
                .name("Test")
                .email("mail@etcdev.ru")
                .login("canoutis")
                .birthday(LocalDate.of(1998, 5, 27))
                .build();
        User createdUser = userStorage.create(tempUser);

        Assertions.assertEquals("Test", createdUser.getName());
        Assertions.assertEquals("mail@etcdev.ru", createdUser.getEmail());
        Assertions.assertEquals("canoutis", createdUser.getLogin());
        Assertions.assertEquals(LocalDate.of(1998, 5, 27), createdUser.getBirthday());
    }

    @Test
    public void testOneUserGetById() {
        User tempUser = User.builder()
                .name("Test")
                .email("mail@etcdev.ru")
                .login("canoutis")
                .birthday(LocalDate.of(1998, 5, 27))
                .build();
        User createdUser = userStorage.create(tempUser);

        User foundUser = userStorage.getUserById(createdUser.getId());

        Assertions.assertEquals(createdUser.getId(), foundUser.getId());
        Assertions.assertEquals("Test", foundUser.getName());
        Assertions.assertEquals("mail@etcdev.ru", foundUser.getEmail());
        Assertions.assertEquals("canoutis", foundUser.getLogin());
        Assertions.assertEquals(LocalDate.of(1998, 5, 27), foundUser.getBirthday());
    }

    @Test
    public void testOneUserUpdate() {
        User tempUser = User.builder()
                .name("Test")
                .email("mail@etcdev.ru")
                .login("canoutis")
                .birthday(LocalDate.of(1998, 5, 27))
                .build();
        User createdUser = userStorage.create(tempUser);
        createdUser.setName("Updated Name");
        createdUser.setEmail("mail@iotachi.ru");
        createdUser.setLogin("iota");
        createdUser.setBirthday(LocalDate.of(1970, 1, 1));
        User foundUser = userStorage.update(createdUser);
        Assertions.assertEquals(createdUser.getId(), foundUser.getId());
        Assertions.assertEquals("Updated Name", foundUser.getName());
        Assertions.assertEquals("mail@iotachi.ru", foundUser.getEmail());
        Assertions.assertEquals("iota", foundUser.getLogin());
        Assertions.assertEquals(LocalDate.of(1970, 1, 1), foundUser.getBirthday());
    }

    @Test
    public void testGetEmptyUserFriends() {
        User tempUser = User.builder()
                .name("Test")
                .email("mail@etcdev.ru")
                .login("canoutis")
                .birthday(LocalDate.of(1998, 5, 27))
                .build();
        User createdUser = userStorage.create(tempUser);
        Assertions.assertEquals(0, userStorage.getUserFriends(createdUser.getId()).size());

    }

    @Test
    public void testGetOneFriendUserByOneFriend() {
        User tempUser = User.builder()
                .name("Test")
                .email("mail@etcdev.ru")
                .login("canoutis")
                .birthday(LocalDate.of(1998, 5, 27))
                .build();
        User createdUser = userStorage.create(tempUser);
        User tempUser2 = User.builder()
                .name("Test2")
                .email("mail2@etcdev.ru")
                .login("canoutis2")
                .birthday(LocalDate.of(1990, 5, 27))
                .build();
        User createdUser2 = userStorage.create(tempUser2);
        userStorage.addFriendToUser(createdUser.getId(), createdUser2.getId());
        List<User> friends = userStorage.getUserFriends(createdUser.getId());
        Assertions.assertEquals(1, friends.size());
        Assertions.assertEquals(createdUser2, friends.get(0));
    }

    @Test
    public void testRemoveOneFriendUserByOneFriend() {
        User tempUser = User.builder()
                .name("Test")
                .email("mail@etcdev.ru")
                .login("canoutis")
                .birthday(LocalDate.of(1998, 5, 27))
                .build();
        User createdUser = userStorage.create(tempUser);
        User tempUser2 = User.builder()
                .name("Test2")
                .email("mail2@etcdev.ru")
                .login("canoutis2")
                .birthday(LocalDate.of(1990, 5, 27))
                .build();
        User createdUser2 = userStorage.create(tempUser2);
        userStorage.addFriendToUser(createdUser.getId(), createdUser2.getId());
        Assertions.assertEquals(1, userStorage.getUserFriends(createdUser.getId()).size());
        userStorage.removeFriendFromUser(createdUser.getId(), createdUser2.getId());
        Assertions.assertEquals(0, userStorage.getUserFriends(createdUser.getId()).size());
    }

    @Test
    public void testGetOneMutualFriendUser() {
        User tempUser = User.builder()
                .name("Test")
                .email("mail@etcdev.ru")
                .login("canoutis")
                .birthday(LocalDate.of(1998, 5, 27))
                .build();
        User createdUser = userStorage.create(tempUser);
        User tempUser2 = User.builder()
                .name("Test2")
                .email("mail2@etcdev.ru")
                .login("canoutis2")
                .birthday(LocalDate.of(1990, 5, 27))
                .build();
        User createdUser2 = userStorage.create(tempUser2);
        User tempUser3 = User.builder()
                .name("Test3")
                .email("mail3@etcdev.ru")
                .login("canoutis3")
                .birthday(LocalDate.of(1980, 5, 27))
                .build();
        User createdUser3 = userStorage.create(tempUser3);
        userStorage.addFriendToUser(createdUser.getId(), createdUser3.getId());
        userStorage.addFriendToUser(createdUser2.getId(), createdUser3.getId());
        Assertions.assertEquals(1, userStorage.getMutualFriends(createdUser.getId(), createdUser2.getId()).size());
        Assertions.assertEquals(createdUser3, userStorage.getMutualFriends(createdUser.getId(), createdUser2.getId()).get(0));
    }
}
