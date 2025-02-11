package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    void addUser(User user);

    void deleteUser(Integer id);

    User getUser(Integer id);

    Map<Integer, User> getBaseOfUsers();
}
