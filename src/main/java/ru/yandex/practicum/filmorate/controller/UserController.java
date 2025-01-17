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
            log.error("Пользователь с данной электронной почтой уже существует!");
            throw new ValidationException("Пользователь с данным электронной почтой уже существует!");
        } else if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            log.error("Логин не может быть быть пустым и содержать пробелы!");
            throw new ValidationException("Логин не может быть быть пустым и содержать пробелы!");
        } else if (baseOfUsers.values().stream().map(User::getLogin).anyMatch(login -> user.getLogin().equals(login))) {
            log.error("Пользователь с данным логином уже существует!");
            throw new ValidationException("Пользователь с данным логином уже существует!");
        }
        if (user.getName() == null) {
            log.info("Вместо имени будет использоваться логин");
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        log.info("Пользователю присвоен id = {}", user.getId());
        baseOfUsers.put(user.getId(), user);
        log.info("Пользователь создан и добавлен в базу");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Началось обновление фильма");
        if (newUser.getId() == null) {
            log.error("Получен пользователь с пустым id");
            throw new ValidationException("Id не должен быть пустым!");
        } else if (baseOfUsers.containsKey(newUser.getId())) {
            log.info("Пользователь был найден");
            User oldUser = baseOfUsers.get(newUser.getId());
            if (newUser.getName() != null && !newUser.getName().equals(oldUser.getName())) {
                log.info("Изменено имя пользователя");
                oldUser.setName(newUser.getName());
            }
            if (newUser.getBirthday() != null && !newUser.getBirthday().equals(oldUser.getBirthday())) {
                log.info("Изменена дата рождения пользователя");
                oldUser.setBirthday(newUser.getBirthday());
            }
            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
                log.info("Обновлен адрес электронной почты пользователя");
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null && !newUser.getLogin().equals(oldUser.getLogin())) {
                if (newUser.getLogin().contains(" ") || newUser.getLogin().isEmpty()) {
                    log.error("Логин не может быть быть пустым и содержать пробелы!");
                    throw new ValidationException("Логин не может быть быть пустым и содержать пробелы!");
                } else {
                    log.info("Обновлен логин пользователя");
                    oldUser.setLogin(newUser.getLogin());
                }
            }
            log.info("Пользователь обновлен");
            return oldUser;
        }
        throw new NotFoundException(String.format("Пользователь с id = %d не найден!", newUser.getId()));
    }

    public Integer getNextId() {
        int nextId = baseOfUsers.keySet().stream().max(Integer::compareTo).orElse(0);
        return ++nextId;
    }
}
