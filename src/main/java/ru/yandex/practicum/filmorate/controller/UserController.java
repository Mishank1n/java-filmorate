package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    Map<Integer, User> baseOfUsers = new HashMap<>();

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{user-id}")
    public User getUser(@PathVariable("user-id") Integer userId) {
        log.info("Получен запрос на получение пользователя с id = {}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{user-id}")
    public String deleteUser(@PathVariable("user-id") Integer userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        return userService.deleteUser(userId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        return userService.create(user);
    }

    @PutMapping("/{user-id}/friends/{friend-id}")
    public List<User> addFriendToUser(@PathVariable("user-id") Integer userId, @PathVariable("friend-id") Integer friendId) {
        log.info("Получен запрос на добавление пользователя с id = {} в друзья к пользователю с id = {}", friendId, userId);
        return userService.addFriendToUser(userId, friendId);
    }

    @DeleteMapping("/{user-id}/friends/{friend-id}")
    public String deleteFriendFromUserFriends(@PathVariable("user-id") Integer userId, @PathVariable("friend-id") Integer friendId) {
        log.info("Получен запрос на удаление из списка друзей пользователя с id = {} друга с id = {}", userId, friendId);
        return userService.deleteFriendFromUserFriends(userId, friendId);
    }

    @GetMapping("/{user-id}/friends")
    public Collection<User> getUserFriends(@PathVariable("user-id") Integer userId) {
        log.info("Получен запрос на получение списка друзей пользователя с id = {}", userId);
        return userService.getUserFriends(userId);
    }

    @GetMapping("/{user-id}/friends/common/{otherUser-id}")
    public Collection<User> getCommonFriends(@PathVariable("user-id") Integer userId, @PathVariable("otherUser-id") Integer otherUserId) {
        log.info("Получен запрос на получение списка общих друзей пользователя с id = {} и пользователя с id = {}", userId, otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }


    @PutMapping()
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя с id = {}", newUser.getId());
        return userService.updateUser(newUser);
    }
}
