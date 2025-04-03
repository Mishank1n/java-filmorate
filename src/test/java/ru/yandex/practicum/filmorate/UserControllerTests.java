package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserController.class, UserRowMapper.class, UserService.class})
public class UserControllerTests {


    private final UserController userController;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private User getTestUser() {
        User user = new User();
        user.setName("Test name");
        user.setId(1);
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        return user;
    }

    private User getSecondTestUser() {
        User newUser = new User();
        newUser.setName("Test new name");
        newUser.setId(2);
        newUser.setLogin("TestNewLogin!");
        newUser.setBirthday(LocalDate.of(2005, 12, 12));
        newUser.setEmail("testnew@gmail.com");
        return newUser;
    }

    @Test
    public void addUser() {
        User user = getTestUser();
        userController.createUser(user);
        assertEquals(1, userController.getUsers().size());
        assertTrue(userController.getUsers().contains(user));
    }

    @Test
    public void updateUser() {
        User user = getTestUser();
        userController.createUser(user);
        User newUser = getSecondTestUser();
        newUser.setId(1);
        assertEquals(newUser, userController.updateUser(newUser));
    }

    @Test
    public void getAllUsers() {
        User user = getTestUser();
        userController.createUser(user);
        User newUser = getSecondTestUser();
        userController.createUser(newUser);
        assertEquals(2, userController.getUsers().size());
        assertTrue(userController.getUsers().contains(user));
        assertTrue(userController.getUsers().contains(newUser));
    }

    @Test
    public void errorAddUserWithNullLogin() {
        User user = getTestUser();
        user.setLogin(null);
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void errorAddUserWithSpaseInLogin() {
        User user = getTestUser();
        user.setLogin("Test login");
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void errorAddUserWithBadEmail() {
        User user = getTestUser();
        user.setEmail("@gmail.com");
        var errors = validator.validate(user);
        assertEquals(1, errors.size());
        assertEquals("Электронная почта пользователя должна быть нужного формата! Пример: email@gmail.com", errors.iterator().next().getMessage());
    }

    @Test
    public void errorAddUserWithBirthdayInFuture() {
        User user = getTestUser();
        user.setBirthday(LocalDate.of(2030, 12, 12));
        var errors = validator.validate(user);
        assertEquals(1, errors.size());
        assertEquals("Дата рождения пользователя не может быть в будущем!", errors.iterator().next().getMessage());
    }

    @Test
    public void errorAddUserWithSameEmail() {
        User user = getTestUser();
        userController.createUser(user);
        User newUser = getSecondTestUser();
        newUser.setEmail("test@gmail.com");
        assertThrows(ValidationException.class, () -> userController.createUser(newUser));
    }

    @Test
    public void errorAddUserWithSameLogin() {
        User user = getTestUser();
        userController.createUser(user);
        User newUser = getSecondTestUser();
        newUser.setLogin("TestLogin");
        assertThrows(ValidationException.class, () -> userController.createUser(newUser));
    }

    @Test
    public void errorUpdateUserWithNullId() {
        User user = getTestUser();
        userController.createUser(user);
        User newUser = getSecondTestUser();
        newUser.setId(null);
        assertThrows(ValidationException.class, () -> userController.updateUser(newUser));
    }

    @Test
    public void errorUpdateNotFoundUser() {
        User user = getTestUser();
        userController.createUser(user);
        User newUser = getSecondTestUser();
        assertThrows(NotFoundException.class, () -> userController.updateUser(newUser));
    }

    @Test
    public void getUser() {
        User user = getTestUser();
        userController.createUser(user);
        assertEquals(user, userController.getUser(user.getId()));
    }

    @Test
    public void errorGetUserWhichIsNotInStorage() {
        assertThrows(NotFoundException.class, () -> {
            userController.getUser(0);
        });
    }

    @Test
    public void deleteUser() {
        User user = getTestUser();
        userController.createUser(user);
        assertEquals(1, userController.getUsers().size());
        userController.deleteUser(user.getId());
        assertEquals(0, userController.getUsers().size());
    }

    @Test
    public void errorDeleteUserWhichIsNotInStorage() {
        assertThrows(NotFoundException.class, () -> {
            userController.deleteUser(0);
        });
    }

    @Test
    public void addToFriendNotConfirmed() {
        User user = getTestUser();
        userController.createUser(user);
        User newUser = getSecondTestUser();
        userController.createUser(newUser);
        userController.addFriendToUser(user.getId(), newUser.getId());
        assertEquals(1, userController.getUserFriends(user.getId()).size());
        assertEquals(0, userController.getUserFriends(newUser.getId()).size());
    }

    @Test
    public void addToFriendConfirmed() {
        User user = getTestUser();
        userController.createUser(user);
        User newUser = getSecondTestUser();
        userController.createUser(newUser);
        userController.addFriendToUser(user.getId(), newUser.getId());
        userController.addFriendToUser(newUser.getId(), user.getId());
        assertEquals(1, userController.getUserFriends(user.getId()).size());
        assertEquals(0, userController.getUserFriends(newUser.getId()).size());
    }

    @Test
    public void errorAddFriendWhichIsNotInStorageToUser() {
        User user = getTestUser();
        userController.createUser(user);
        assertThrows(NotFoundException.class, () -> {
            userController.addFriendToUser(user.getId(), 2);
        });
    }

    @Test
    public void getCommonFriend() {
        User user = getTestUser();
        userController.createUser(user);
        User newUser = getSecondTestUser();
        userController.createUser(newUser);
        User commonFriend = new User();
        commonFriend.setName("Friend name");
        commonFriend.setId(userController.getUserService().getNextId());
        commonFriend.setLogin("Friend");
        commonFriend.setBirthday(LocalDate.of(2005, 12, 12));
        commonFriend.setEmail("friend@gmail.com");
        userController.createUser(commonFriend);
        userController.addFriendToUser(user.getId(), commonFriend.getId());
        userController.addFriendToUser(newUser.getId(), commonFriend.getId());
        assertEquals(1, userController.getCommonFriends(user.getId(), newUser.getId()).size());
        assertEquals(commonFriend, userController.getCommonFriends(user.getId(), newUser.getId()).stream().findFirst().get());
    }
}
