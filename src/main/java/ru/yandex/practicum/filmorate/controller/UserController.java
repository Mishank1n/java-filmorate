package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    Map<Integer, User> baseOfUsers = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Возвращен список пользователей");
        return baseOfUsers.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Началось создание пользователя");
        if (baseOfUsers.values().stream().map(User::getEmail).anyMatch(email -> user.getEmail().equals(email))) {
            log.error("Пользователь с электронной почтой: {} уже существует!", user.getEmail());
            throw new ValidationException(String.format("Пользователь с электронной почтой: %s уже существует!", user.getEmail()));
        } else if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            log.error("Логин не может быть быть пустым и содержать пробелы!");
            throw new ValidationException("Логин не может быть быть пустым и содержать пробелы!");
        } else if (baseOfUsers.values().stream().map(User::getLogin).anyMatch(login -> user.getLogin().equals(login))) {
            log.error("Пользователь с логином: {} уже существует!", user.getLogin());
            throw new ValidationException(String.format("Пользователь с логином: %s уже существует!", user.getLogin()));
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        baseOfUsers.put(user.getId(), user);
        log.info("Пользователь c id = {} создан и добавлен в базу", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Началось обновление фильма");
        if (newUser.getId() == null) {
            log.error("Получен пользователь с пустым id");
            throw new ValidationException("Id не должен быть пустым!");
        } else if (baseOfUsers.containsKey(newUser.getId())) {
            User oldUser = baseOfUsers.get(newUser.getId());
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
            log.info("Пользователь c id = {} обновлен", oldUser.getId());
            return oldUser;
        }
        log.error("Пользователь с id = {} не найден!", newUser.getId());
        throw new NotFoundException(String.format("Пользователь с id = %d не найден!", newUser.getId()));
    }

    public Integer getNextId() {
        int nextId = baseOfUsers.keySet().stream().max(Integer::compareTo).orElse(0);
        return ++nextId;
    }
}
