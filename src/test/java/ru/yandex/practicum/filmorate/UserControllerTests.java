package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTests {

    private UserController userController;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void addUser() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        assertEquals(1, userController.getUsers().size());
        assertTrue(userController.getUsers().contains(user));
    }

    @Test
    public void updateUser() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        User newUser = new User();
        newUser.setName("Test name");
        newUser.setId(1);
        newUser.setLogin("TestLogin!");
        newUser.setBirthday(LocalDate.of(2005, 12, 12));
        newUser.setEmail("testnew@gmail.com");
        assertEquals(newUser, userController.updateUser(newUser));
    }

    @Test
    public void getAllUsers() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        User newUser = new User();
        newUser.setName("Test name");
        newUser.setId(userController.getUserService().getNextId());
        newUser.setLogin("TestLogin!");
        newUser.setBirthday(LocalDate.of(2005, 12, 12));
        newUser.setEmail("testnew@gmail.com");
        userController.createUser(newUser);
        assertEquals(2, userController.getUsers().size());
        assertTrue(userController.getUsers().contains(user));
        assertTrue(userController.getUsers().contains(newUser));
    }

    @Test
    public void errorAddUserWithNullLogin() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void errorAddUserWithSpaseInLogin() {
        User user = new User();
        user.setName("Test name");
        user.setLogin("Test login");
        user.setId(userController.getUserService().getNextId());
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void errorAddUserWithBadEmail() {
        User user = new User();
        user.setName("Test name");
        user.setLogin("Testlogin");
        user.setId(userController.getUserService().getNextId());
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("@gmail.com");
        var errors = validator.validate(user);
        assertEquals(1, errors.size());
        assertEquals("Электронная почта пользователя должна быть нужного формата! Пример: email@gmail.com", errors.iterator().next().getMessage());
    }

    @Test
    public void errorAddUserWithBirthdayInFuture() {
        User user = new User();
        user.setName("Test name");
        user.setLogin("Testlogin");
        user.setId(userController.getUserService().getNextId());
        user.setBirthday(LocalDate.of(2030, 12, 12));
        user.setEmail("test@gmail.com");
        var errors = validator.validate(user);
        assertEquals(1, errors.size());
        assertEquals("Дата рождения пользователя не может быть в будущем!", errors.iterator().next().getMessage());
    }

    @Test
    public void errorAddUserWithSameEmail() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        User newUser = new User();
        newUser.setName("Test name");
        newUser.setId(userController.getUserService().getNextId());
        newUser.setLogin("TestLogin!");
        newUser.setBirthday(LocalDate.of(2005, 12, 12));
        newUser.setEmail("test@gmail.com");
        assertThrows(ValidationException.class, () -> userController.createUser(newUser));
    }

    @Test
    public void errorAddUserWithSameLogin() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        User newUser = new User();
        newUser.setName("Test name");
        newUser.setId(userController.getUserService().getNextId());
        newUser.setLogin("TestLogin");
        newUser.setBirthday(LocalDate.of(2005, 12, 12));
        newUser.setEmail("testnew@gmail.com");
        assertThrows(ValidationException.class, () -> userController.createUser(newUser));
    }

    @Test
    public void errorUpdateUserWithNullId() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        User newUser = new User();
        newUser.setName("Test name");
        newUser.setLogin("TestLogin!");
        newUser.setBirthday(LocalDate.of(2005, 12, 12));
        newUser.setEmail("testnew@gmail.com");
        assertThrows(ValidationException.class, () -> userController.updateUser(newUser));
    }

    @Test
    public void errorUpdateNotFoundUser() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        User newUser = new User();
        newUser.setName("Test name");
        newUser.setId(userController.getUserService().getNextId());
        newUser.setLogin("TestLogin!");
        newUser.setBirthday(LocalDate.of(2005, 12, 12));
        newUser.setEmail("testnew@gmail.com");
        assertThrows(NotFoundException.class, () -> userController.updateUser(newUser));
    }

    @Test
    public void getUser() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
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
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
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
    public void addToFriend() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        User newUser = new User();
        newUser.setName("Test name");
        newUser.setId(userController.getUserService().getNextId());
        newUser.setLogin("TestLogin!");
        newUser.setBirthday(LocalDate.of(2005, 12, 12));
        newUser.setEmail("testnew@gmail.com");
        userController.createUser(newUser);
        userController.addFriendToUser(user.getId(), newUser.getId());
        assertEquals(1, user.getFriends().size());
        assertEquals(1, user.getFriends().size());
    }

    @Test
    public void errorAddFriendWhichIsNotInStorageToUser() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        assertThrows(NotFoundException.class, () -> {
            userController.addFriendToUser(user.getId(), 2);
        });
    }

    @Test
    public void getCommonFriend() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        User newUser = new User();
        newUser.setName("Test name");
        newUser.setId(userController.getUserService().getNextId());
        newUser.setLogin("TestLogin!");
        newUser.setBirthday(LocalDate.of(2005, 12, 12));
        newUser.setEmail("testnew@gmail.com");
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
