package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectSaveException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("select * from user", UserDbStorage::makeUser);
    }

    static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                Objects.requireNonNull(rs.getDate("birthday")).toLocalDate());
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user")
                .usingGeneratedKeyColumns("user_id");
        int userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user = getUserById(userId);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update user set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where user_id = ?";
        int response = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday().format(Constant.dateFormatter),
                user.getId());
        if (response == 1) {
            return getUserById(user.getId());
        } else {
            log.info("Пользователь с идентификатором {} не изменен.", user.getId());
            throw new ObjectUpdateException(
                    String.format("Ошибка обновления пользователя! Id=%d", user.getId()));
        }
    }

    @Override
    public User getUserById(int id) {
        List<User> users = jdbcTemplate.query("select * from user where user_id=?", UserDbStorage::makeUser, id);
        if (!users.isEmpty()) {
            log.info("Найден пользователь: {} {}", users.get(0).getId(), users.get(0).getName());
            return users.get(0);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException(
                    String.format("Ошибка получения пользователя. Пользователь не найден! Id=%d", id));
        }
    }

    @Override
    public User removeFriendFromUser(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        SqlRowSet requestRows = jdbcTemplate.queryForRowSet("select * from friend_request " +
                "where initiator_user_id = ? and target_user_id = ? and confirmed = true", userId, friendId);
        if (requestRows.next()) {
            String sqlQuery = "update friend_request " +
                    "set initiator_user_id = ?, " +
                    "target_user_id = ?," +
                    "confirmed = false";
            int updatedRowsNum = jdbcTemplate.update(sqlQuery,
                    friendId, userId);
            if (updatedRowsNum == 0) {
                log.info("Ошибка обновления статуса заявки в друзья. UserId={}, FriendId={}", userId, friendId);
                throw new ObjectSaveException(
                        String.format("Ошибка обновления статуса заявки в друзья. UserId=%d, FriendId=%d", userId, friendId));
            }
        } else {
            String sqlQuery = "delete from friend_request " +
                    "where initiator_user_id = ? and target_user_id = ?";
            jdbcTemplate.update(sqlQuery,
                    userId,
                    friendId);
        }
        return getUserById(userId);
    }

    public User addFriendToUser(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        SqlRowSet requestRows = jdbcTemplate.queryForRowSet("select * from friend_request " +
                "where target_user_id = ? and initiator_user_id = ? ", userId, friendId);
        if (requestRows.next()) {
            confirmFriendRequest(requestRows.getInt("friend_request_id"));
        } else {
            String sqlQuery = "insert into friend_request(initiator_user_id, target_user_id, confirmed) " +
                    "select ?, ?, false " +
                    "where not exists (select 1 from friend_request where initiator_user_id = ? and target_user_id = ?)";
            jdbcTemplate.update(sqlQuery,
                    userId, friendId,
                    userId, friendId);
        }
        return getUserById(userId);
    }

    private void confirmFriendRequest(int friendRequestId) {
        String sqlQuery = "update friend_request set confirmed = ? " +
                "where request_id = ?";
        int updatedRowsNum = jdbcTemplate.update(sqlQuery, true, friendRequestId);
        if (updatedRowsNum == 0) {
            log.info("Ошибка обновления статуса заявки в друзья. Id={}", friendRequestId);
            throw new ObjectSaveException(
                    String.format("Ошибка обновления статуса заявки в друзья. Id=%d", friendRequestId));
        }
    }

    @Override
    public List<User> getUserFriends(int userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select u.* " +
                        "from user u " +
                        "where u.user_id in ( " +
                        "  select fr.target_user_id " +
                        "  from friend_request fr " +
                        "  where fr.initiator_user_id = ? " +
                        "  union " +
                        "  select fr.initiator_user_id " +
                        "  from friend_request fr " +
                        "  where fr.target_user_id = ? and fr.confirmed = true" +
                        ")",
                userId, userId);
        List<User> userList = new ArrayList<>();
        while (userRows.next()) {
            User user = new User(
                    userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate()
            );
            userList.add(user);
        }
        return userList;
    }

    @Override
    public List<User> getMutualFriends(int userId, int targetId) {
        List<User> targetFriends = getUserFriends(targetId);
        List<User> userFriends = getUserFriends(userId);
        return targetFriends.stream()
                .filter(userFriends::contains)
                .collect(Collectors.toList());
    }
}
