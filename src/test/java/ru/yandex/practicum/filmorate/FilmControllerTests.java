package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTests {

    private FilmController filmController;
    private Validator validator;
    private UserController userController;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), userStorage, new UserService(userStorage)));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userController = new UserController(filmController.getFilmService().getUserService());
    }


    @Test
    public void addFilm() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        assertTrue(filmController.getFilms().contains(film));
    }

    @Test
    public void updateFilm() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        Film newFilm = new Film();
        newFilm.setName("Test name");
        newFilm.setId(1);
        newFilm.setReleaseDate(LocalDate.of(2020, 12, 12));
        newFilm.setDescription("Description of test film");
        newFilm.setDuration(300);
        assertEquals(newFilm, filmController.updateFilm(newFilm));
    }

    @Test
    public void getAllFilms() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        Film newFilm = new Film();
        newFilm.setName("Test name");
        newFilm.setId(filmController.getFilmService().getNewId());
        newFilm.setReleaseDate(LocalDate.of(2020, 12, 12));
        newFilm.setDescription("Description of test film");
        newFilm.setDuration(300);
        filmController.createFilm(newFilm);
        assertEquals(2, filmController.getFilms().size());
        assertTrue(filmController.getFilms().contains(film));
        assertTrue(filmController.getFilms().contains(newFilm));
    }

    @Test
    public void errorAddFilmWithNullName() {
        Film film = new Film();
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        var errors = validator.validate(film);
        assertEquals(1, errors.size());
        assertEquals("Название фильма не может быть пустым!", errors.iterator().next().getMessage());
    }

    @Test
    public void errorAddFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(-200);
        var errors = validator.validate(film);
        assertEquals(1, errors.size());
        assertEquals("Продолжительность фильма должна быть положительным числом!", errors.iterator().next().getMessage());
    }

    @Test
    public void errorAddFilmWithReleaseDate() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(1720, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void errorAddFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(1720, 12, 12));
        film.setDescription("T".repeat(201));
        film.setDuration(200);
        var errors = validator.validate(film);
        assertEquals(1, errors.size());
        assertEquals("Длина описания фильма не должна превышать 200 символов!", errors.iterator().next().getMessage());
    }

    @Test
    public void errorUpdateFilmWhichIsNotInBase() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        Film newFilm = new Film();
        newFilm.setName("Test name");
        newFilm.setId(filmController.getFilmService().getNewId());
        newFilm.setReleaseDate(LocalDate.of(2020, 12, 12));
        newFilm.setDescription("Description of test film");
        newFilm.setDuration(300);
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(newFilm));
    }

    @Test
    public void errorUpdateFilmWithNullId() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        Film newFilm = new Film();
        newFilm.setName("Test name");
        newFilm.setReleaseDate(LocalDate.of(2020, 12, 12));
        newFilm.setDescription("Description of test film");
        newFilm.setDuration(300);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(newFilm));
    }

    @Test
    public void getFilm() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        Film newFilm = filmController.getFilm(film.getId());
        assertEquals(film, newFilm);
    }

    @Test
    public void errorGetFilmThatNotInStorage() {
        assertThrows(NotFoundException.class, () -> {
            filmController.getFilm(1);
        });
    }

    @Test
    public void deleteFilm() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        assertEquals(1, filmController.getFilms().size());
        filmController.deleteFilm(film.getId());
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void errorDeleteFilmThatNotInStorage() {
        assertThrows(NotFoundException.class, () -> {
            filmController.deleteFilm(1);
        });
    }

    @Test
    public void addLikeToFilm() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        filmController.addLikeToFilm(film.getId(), user.getId());
        assertEquals(1, filmController.getFilm(film.getId()).getLikesOfUsers().size());
        assertTrue(filmController.getFilm(film.getId()).getLikesOfUsers().contains(user.getId()));
    }

    @Test
    public void errorAddLikeToFilmWhichIsNotInStorage() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        assertThrows(NotFoundException.class, () -> {
            filmController.addLikeToFilm(1, user.getId());
        });
    }

    @Test
    public void errorAddLikeToFilmFromUserWhichIsNotInStorage() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        assertThrows(NotFoundException.class, () -> {
            filmController.addLikeToFilm(film.getId(), 1);
        });
    }

    @Test
    public void removeLikeFromFilm() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        filmController.addLikeToFilm(film.getId(), user.getId());
        filmController.deleteLikeFromFilm(film.getId(), user.getId());
        assertEquals(0, filmController.getFilm(film.getId()).getLikesOfUsers().size());
        assertFalse(filmController.getFilm(film.getId()).getLikesOfUsers().contains(user.getId()));
    }

    @Test
    public void errorRemoveNullLikeFromFilm() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getFilmService().getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        assertThrows(NotFoundException.class, () -> {
            filmController.deleteLikeFromFilm(film.getId(), user.getId());
        });
    }

    @Test
    public void errorRemoveLikeFromFilmWhichIsNotInStorage() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        userController.createUser(user);
        assertThrows(NotFoundException.class, () -> {
            filmController.deleteLikeFromFilm(1, user.getId());
        });
    }
}
