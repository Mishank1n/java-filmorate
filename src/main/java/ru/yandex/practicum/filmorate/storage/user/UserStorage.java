package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    void addUser(User user);

    void deleteUser(Integer id);

    User getUser(Integer id);

    List<User> getBaseOfUsers();

    void updateUser(User user);

    void addFriendToUser(Integer userId, Integer friendId);

    void deleteFriendFromUser(Integer userId, Integer friendId);

    List<User> getUserFriends(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer otherUserId);

}
