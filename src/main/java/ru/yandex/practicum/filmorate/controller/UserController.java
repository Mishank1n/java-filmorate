package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Getter
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    Map<Integer, User> baseOfUsers = new HashMap<>();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Integer userId) {
        log.info("Получен запрос на получение пользователя с id = {}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Integer userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        return userService.deleteUser(userId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        return userService.create(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public Set<Integer> addFriendToUser(@PathVariable Integer userId, @PathVariable Integer friendId) {
        log.info("Получен запрос на добавление пользователя с id = {} в друзья к пользователю с id = {}", friendId, userId);
        return userService.addFriendToUser(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public String deleteFriendFromUserFriends(@PathVariable Integer userId, @PathVariable Integer friendId) {
        log.info("Получен запрос на удаление из списка друзей пользователя с id = {} друга с id = {}", userId, friendId);
        return userService.deleteFriendFromUserFriends(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getUserFriends(@PathVariable Integer userId) {
        log.info("Получен запрос на получение списка друзей пользователя с id = {}", userId);
        return userService.getUserFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public Collection<User> getCommonFriends(@PathVariable Integer userId, @PathVariable Integer otherUserId) {
        log.info("Получен запрос на получение списка общих друзей пользователя с id = {} и пользователя с id = {}", userId, otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя с id = {}", newUser);
        return userService.updateUser(newUser);
    }
}
