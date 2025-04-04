package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ThingIsAlreadyContain;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@Getter
public class UserService {

    private final UserDbStorage userStorage;
    private final String errorMessageFindUserForLog = "Пользователь с id = {} не найден!";
    private final String errorMessageFindUserForException = "Пользователь с id = %d не найден!";

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        log.info("Возвращен список пользователей");
        return userStorage.getBaseOfUsers();
    }

    public User getUser(Integer userId) {
        log.info("Началось получение пользователя с id = {}", userId);
        if (userStorage.getUser(userId) != null) {
            log.info("Пользователь с id = {} был найден и возвращен", userId);
            return userStorage.getUser(userId);
        } else {
            log.error(errorMessageFindUserForLog, userId);
            throw new NotFoundException(String.format(errorMessageFindUserForException, userId));
        }
    }

    public User updateUser(User newUser) {
        log.info("Началось обновление пользователя");
        if (newUser.getId() == null) {
            log.error("Получен пользователь с пустым id");
            throw new ValidationException("Id не должен быть пустым!");
        } else if (userStorage.getBaseOfUsers().stream().map(User::getId).toList().contains(newUser.getId())) {
            User oldUser = userStorage.getUser(newUser.getId());
            if (newUser.getName() != null && !newUser.getName().equals(oldUser.getName())) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getBirthday() != null && !newUser.getBirthday().equals(oldUser.getBirthday())) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null && !newUser.getLogin().equals(oldUser.getLogin())) {
                if (newUser.getLogin().contains(" ") || newUser.getLogin().isEmpty()) {
                    log.error("Логин не может быть быть пустым и содержать пробелы!");
                    throw new ValidationException("Логин не может быть быть пустым и содержать пробелы!");
                } else {
                    oldUser.setLogin(newUser.getLogin());
                }
            }
            userStorage.updateUser(oldUser);
            log.info("Пользователь c id = {} обновлен", oldUser.getId());
            return oldUser;
        }
        log.error(errorMessageFindUserForLog, newUser.getId());
        throw new NotFoundException(String.format(errorMessageFindUserForException, newUser.getId()));
    }

    public String deleteUser(Integer userId) {
        log.info("Началось удаление пользователя с id = {}", userId);
        if (userStorage.getUser(userId) != null) {
            userStorage.deleteUser(userId);
            log.info("Пользователь с id = {} был удален", userId);
            return String.format("Пользователь с id = %d был удален", userId);
        } else {
            log.info(errorMessageFindUserForLog, userId);
            throw new NotFoundException(String.format(errorMessageFindUserForException, userId));
        }
    }

    public User create(User user) {
        log.info("Началось создание пользователя");
        if (userStorage.getBaseOfUsers().stream().map(User::getEmail).anyMatch(email -> user.getEmail().equals(email))) {
            log.error("Пользователь с электронной почтой: {} уже существует!", user.getEmail());
            throw new ValidationException(String.format("Пользователь с электронной почтой: %s уже существует!", user.getEmail()));
        } else if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            log.error("Логин не может быть быть пустым и содержать пробелы!");
            throw new ValidationException("Логин не может быть быть пустым и содержать пробелы!");
        } else if (userStorage.getBaseOfUsers().stream().map(User::getLogin).anyMatch(login -> user.getLogin().equals(login))) {
            log.error("Пользователь с логином: {} уже существует!", user.getLogin());
            throw new ValidationException(String.format("Пользователь с логином: %s уже существует!", user.getLogin()));
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        userStorage.addUser(user);
        log.info("Пользователь c id = {} создан и добавлен в базу", user.getId());
        return user;
    }

    public List<User> addFriendToUser(Integer userId, Integer friendId) {
        log.info("Началось добавление друга с id = {} к пользователю с id = {}", friendId, userId);
        if (userStorage.getUser(userId) != null && userStorage.getUser(friendId) != null) {
            if (!userStorage.getUserFriends(userId).contains(userStorage.getUser(friendId))) {
                userStorage.addFriendToUser(userId, friendId);
                log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", userId, friendId);
                return userStorage.getUserFriends(userId);
            } else {
                System.out.println("Уже есть");
                log.error("Пользователь с id = {} уже есть в списке друзей пользователя с id = {}", friendId, userId);
                throw new ThingIsAlreadyContain(String.format("Пользователь с id = %d уже есть в списке друзей пользователя с id = %d", friendId, userId));
            }
        }
        if (userStorage.getUser(userId) == null) {
            log.error(errorMessageFindUserForLog, userId);
            throw new NotFoundException(String.format(errorMessageFindUserForException, userId));
        }
        log.error(errorMessageFindUserForLog, friendId);
        throw new NotFoundException(String.format(errorMessageFindUserForException, friendId));
    }

    public String deleteFriendFromUserFriends(Integer userId, Integer friendId) {
        log.info("Началось удаление из друзей пользователя с id = {} пользователя с id = {}", userId, friendId);
        if (userStorage.getUser(userId) != null && userStorage.getUser(friendId) != null) {
            if (userStorage.getUserFriends(userId).isEmpty()) {
                log.info("У пользователя c id = {} нет друзей", userId);
                return String.format("У пользователя c id = %d нет друзей", userId);
            } else if (userStorage.getUserFriends(userId).contains(userStorage.getUser(friendId))) {
                userStorage.deleteFriendFromUser(userId, friendId);
                log.info("Пользователь с id = {} удалил пользователя из друзей с id = {}", userId, friendId);
                return String.format("Пользователь с id = %d удалил пользователя из друзей с id = %d", userId, friendId);
            } else {
                log.error("Пользователь с id = {} не имеет друга с id = {}", userId, friendId);
                throw new NotFoundException(String.format("Пользователь с id = %d не имеет друга с id = %d", userId, friendId));
            }
        } else if (userStorage.getUser(userId) == null) {
            log.error(errorMessageFindUserForLog, userId);
            throw new NotFoundException(String.format(errorMessageFindUserForException, userId));
        } else {
            log.error(errorMessageFindUserForLog, friendId);
            throw new NotFoundException(String.format(errorMessageFindUserForException, friendId));
        }
    }

    public List<User> getUserFriends(Integer userId) {
        log.info("Началось получение списка друзей пользователя с id = {}", userId);
        if (userStorage.getUser(userId) != null) {
            log.info("Получен и возвращен список друзей у пользователя с id = {}", userId);
            return userStorage.getUserFriends(userId);
        } else {
            log.error(errorMessageFindUserForLog, userId);
            throw new NotFoundException(String.format(errorMessageFindUserForException, userId));
        }
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        log.info("Началось получение общих друзей у пользователя с id = {} и пользователя с id = {}", userId, otherUserId);
        if (userStorage.getUser(userId) != null && userStorage.getUser(otherUserId) != null) {
            log.info("Получен и возвращен список всех общих друзей у пользователя с id = {} и пользователя с id = {}", userId, otherUserId);
            return userStorage.getCommonFriends(userId, otherUserId);
        } else if (userStorage.getUser(userId) == null) {
            log.error(errorMessageFindUserForLog, userId);
            throw new NotFoundException(String.format(errorMessageFindUserForException, userId));
        } else {
            log.error(errorMessageFindUserForLog, otherUserId);
            throw new NotFoundException(String.format(errorMessageFindUserForException, otherUserId));
        }
    }

    public Integer getNextId() {
        int nextId = userStorage.getBaseOfUsers().stream().map(User::getId).max(Integer::compareTo).orElse(0);
        return ++nextId;
    }

}
