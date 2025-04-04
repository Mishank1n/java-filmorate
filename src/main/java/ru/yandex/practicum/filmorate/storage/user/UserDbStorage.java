package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;

@Slf4j
@Qualifier
@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String ADD_USER = "MERGE INTO users (id, login, name, email, birthday) KEY (login, email) " + "VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_USER = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? WHERE id = ?";
    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String GET_ALL_USERS = "SELECT * FROM users";
    private static final String ADD_FRIEND_TO_USER_FALSE = "INSERT INTO friends(user_id, friend_id, confirmed) " + "VALUES (?, ?, false)";
    private static final String ADD_FRIEND_TO_USER_TRUE = "UPDATE friends SET confirmed = true WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_FRIEND_FROM_USER = "DELETE FROM friends WHERE user_id = ? and friend_id = ?";
    private static final String GET_USER_FRIENDS = "SELECT * FROM users WHERE id IN " + "((SELECT friend_id FROM friends WHERE user_id = ? ) UNION  (SELECT user_id FROM friends WHERE friend_id=? AND confirmed = true))";
    private static final String GET_COMMON_FRIENDS = "WITH friends_of_2 AS (" + "    SELECT FRIEND_ID AS friend_id FROM FRIENDS WHERE USER_ID = ?" + "    UNION" + "    SELECT USER_ID AS friend_id FROM FRIENDS WHERE FRIEND_ID = ? AND CONFIRMED = TRUE" + ")," + "friends_of_3 AS (" + "    SELECT FRIEND_ID AS friend_id FROM FRIENDS WHERE USER_ID = ?" + "    UNION" + "    SELECT USER_ID AS friend_id FROM FRIENDS WHERE FRIEND_ID = ? AND CONFIRMED = TRUE" + ")\n" + "SELECT *\n" + "FROM USERS u\n" + "JOIN friends_of_2 f2 ON u.id = f2.friend_id\n" + "JOIN friends_of_3 f3 ON u.id = f3.friend_id;";
    private static final String DELETE_LIKES_WHEN_DELETE_USER = "DELETE FROM likes WHERE user_id = ?";
    private static final String DELETE_FRIENDS_WHEN_DELETE_USER = "DELETE FROM friends WHERE user_id = ?";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper<User> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public User getUser(Integer id) {
        log.info("Получен запрос в базу данных на получение пользователя с с id = {}", id);
        try {
            User result = jdbcTemplate.queryForObject(GET_USER_BY_ID, mapper, id);
            log.info("Из базы данных был получен пользователь с id = {}", id);
            return result;
        } catch (EmptyResultDataAccessException ignored) {
            log.error("Пользователь с с id = {} не был получен из базы данных", id);
            return null;
        }
    }

    @Override
    public List<User> getBaseOfUsers() {
        log.info("Получен запрос в базу данных на получение всех пользователей");
        return findMany(GET_ALL_USERS);
    }

    @Override
    public void updateUser(User user) {
        log.info("Получен запрос в базу данных на обновление пользователя с id = {}", user.getId());
        update(UPDATE_USER, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        log.info("Пользователь с id = {} был обновлен в базе данных", user.getId());
    }

    @Override
    public void addUser(User user) {
        log.info("Получен запрос в базу данных на добавление пользователя с id = {}", user.getId());
        insert(ADD_USER, user.getId(), user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        log.info("Пользователь с id = {} был добавлен в базу данных", user.getId());
    }

    @Override
    public void deleteUser(Integer userId) {
        log.info("Получен запрос в базу данных на удаление пользователя id = {} ", userId);
        delete(DELETE_USER, userId);
        delete(DELETE_FRIENDS_WHEN_DELETE_USER, userId);
        delete(DELETE_LIKES_WHEN_DELETE_USER, userId);
        log.info("Пользователь с id = {} был удален из базы данных", userId);
    }

    @Override
    public void addFriendToUser(Integer userId, Integer friendId) {
        log.info("Получен запрос в базу данных на добавление пользователем с id = {} друга с id = {}", userId, friendId);
        if (getUserFriends(friendId).contains(getUser(userId))) {
            insert(ADD_FRIEND_TO_USER_TRUE, userId, friendId);
            log.info("Пользователь с id = {} добавил друга с id = {} в базу данных - дружба подтверждена", userId, friendId);
        } else {
            insert(ADD_FRIEND_TO_USER_FALSE, userId, friendId);
            log.info("Пользователь с id = {} добавил друга с id = {} в базу данных - дружба не подтверждена", userId, friendId);
        }
    }

    @Override
    public void deleteFriendFromUser(Integer userId, Integer friendId) {
        log.info("Получен запрос в базу данных на удаление пользователем с id = {} друга с id = {}", userId, friendId);
        if (getUserFriends(friendId).contains(getUser(userId))) {
            delete(DELETE_FRIEND_FROM_USER, userId, friendId);
            insert(ADD_FRIEND_TO_USER_FALSE, friendId, userId);
            log.info("Пользователь с id = {} удалил друга из базы данных с id = {} - дружба стала неподтвержденной", userId, friendId);
        } else {
            delete(DELETE_FRIEND_FROM_USER, userId, friendId);
            log.info("Пользователь с id = {} удалил друга из базы данных с id = {} - дружбы нет", userId, friendId);
        }
    }

    @Override
    public List<User> getUserFriends(Integer userId) {
        log.info("Получен запрос в базу данных на получение списка всех друзей пользователя с id = {}", userId);
        return findMany(GET_USER_FRIENDS, userId, userId);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        log.info("Получен запрос в базу данных на получение списка общих друзей у пользователя с id = {} и пользователя с id = {}", userId, otherUserId);
        return findMany(GET_COMMON_FRIENDS, userId, userId, otherUserId, otherUserId);
    }

}
