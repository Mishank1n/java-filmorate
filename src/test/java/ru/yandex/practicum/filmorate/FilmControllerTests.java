package ru.yandex.practicum.filmorate;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmService.class, UserDbStorage.class, UserController.class, FilmController.class, FilmDbStorage.class, FilmRowMapper.class, UserRowMapper.class, UserService.class})
public class FilmControllerTests {

    private final FilmController filmController;
    private final UserController userController;
    private final UserDbStorage userStorage;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    public void addFilm() {
        Film film = getTestFilm();
        filmController.createFilm(film);
        Film getFilm = filmController.getFilm(film.getId());
        assertEquals(film, getFilm);
    }

    Film getTestFilm() {
        Film film = new Film();
        film.setId(1);
        film.setName("TestFilmName");
        film.setDescription("TestFilmDescription");
        film.setReleaseDate(LocalDate.of(2001, 2, 3));
        film.setDuration(51);
        film.setMpa(new Mpa(1, "G", "у фильма нет возрастных ограничений"));
        film.addGenre(new Genre(1, "Комедия"));
        return film;
    }

    Film getSecondTestFilm() {
        Film newFilm = new Film();
        newFilm.setName("Test name");
        newFilm.setId(2);
        newFilm.setReleaseDate(LocalDate.of(2020, 12, 12));
        newFilm.setDescription("Description of test film");
        newFilm.setDuration(300);
        newFilm.setMpa(new Mpa(2, "PG", "детям рекомендуется смотреть фильм с родителями"));
        newFilm.addGenre(new Genre(2, "Драма"));
        return newFilm;
    }

    User getTestUser() {
        User user = new User();
        user.setName("Test name");
        user.setId(userController.getUserService().getNextId());
        user.setLogin("TestLogin");
        user.setBirthday(LocalDate.of(2005, 12, 12));
        user.setEmail("test@gmail.com");
        return user;
    }

    @Test
    public void updateFilm() {
        Film film = getTestFilm();
        filmController.createFilm(film);
        Film newFilm = getSecondTestFilm();
        newFilm.setId(1);
        assertEquals(newFilm, filmController.updateFilm(newFilm));
    }

    @Test
    public void getAllFilms() {
        Film film = getTestFilm();
        filmController.createFilm(film);
        Film newFilm = getSecondTestFilm();
        filmController.createFilm(newFilm);
        assertEquals(2, filmController.getFilms().size());
        assertTrue(filmController.getFilms().stream().map(Film::getName).anyMatch(s -> s.equals(film.getName())));
        assertTrue(filmController.getFilms().stream().map(Film::getName).anyMatch(s -> s.equals(newFilm.getName())));
    }

    @Test
    public void errorAddFilmWithNullName() {
        Film film = getTestFilm();
        film.setName("");
        var errors = validator.validate(film);
        assertEquals(1, errors.size());
        assertEquals("Название фильма не может быть пустым!", errors.iterator().next().getMessage());
    }

    @Test
    public void errorAddFilmWithNegativeDuration() {
        Film film = getTestFilm();
        film.setDuration(-10);
        var errors = validator.validate(film);
        assertEquals(1, errors.size());
        assertEquals("Продолжительность фильма должна быть положительным числом!", errors.iterator().next().getMessage());
    }

    @Test
    public void errorAddFilmWithReleaseDate() {
        Film film = getTestFilm();
        film.setReleaseDate(LocalDate.of(1720, 12, 12));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void errorAddFilmWithLongDescription() {
        Film film = getTestFilm();
        film.setDescription("T".repeat(201));
        var errors = validator.validate(film);
        assertEquals(1, errors.size());
        assertEquals("Длина описания фильма не должна превышать 200 символов!", errors.iterator().next().getMessage());
    }

    @Test
    public void errorUpdateFilmWhichIsNotInBase() {
        Film film = getTestFilm();
        filmController.createFilm(film);
        Film newFilm = getSecondTestFilm();
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(newFilm));
    }

    @Test
    public void errorUpdateFilmWithNullId() {
        Film film = getTestFilm();
        filmController.createFilm(film);
        Film newFilm = getSecondTestFilm();
        newFilm.setId(null);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(newFilm));
    }

    @Test
    public void getFilm() {
        Film film = getTestFilm();
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
        Film film = getTestFilm();
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
        Film film = getTestFilm();
        filmController.createFilm(film);
        User user = getTestUser();
        userController.createUser(user);
        filmController.addLikeToFilm(film.getId(), user.getId());
        assertEquals(1, filmController.getPopularFilms(10).size());
        assertTrue(filmController.getPopularFilms(10).stream().map(Film::getName).anyMatch(s -> s.equals(film.getName())));
    }

    @Test
    public void errorAddLikeToFilmWhichIsNotInStorage() {
        User user = getTestUser();
        userController.createUser(user);
        assertThrows(NotFoundException.class, () -> {
            filmController.addLikeToFilm(1, user.getId());
        });
    }

    @Test
    public void errorAddLikeToFilmFromUserWhichIsNotInStorage() {
        Film film = getTestFilm();
        filmController.createFilm(film);
        assertThrows(NotFoundException.class, () -> {
            filmController.addLikeToFilm(film.getId(), 1);
        });
    }

    @Test
    public void removeLikeFromFilm() {
        Film film = getTestFilm();
        filmController.createFilm(film);
        User user = getTestUser();
        userController.createUser(user);
        filmController.addLikeToFilm(film.getId(), user.getId());
        assertEquals(1, filmController.getPopularFilms(20).size());
        filmController.deleteLikeFromFilm(film.getId(), user.getId());
        assertEquals(0, filmController.getPopularFilms(20).size());
    }

    @Test
    public void errorRemoveNullLikeFromFilm() {
        Film film = getTestFilm();
        filmController.createFilm(film);
        User user = getTestUser();
        userController.createUser(user);
        assertThrows(NotFoundException.class, () -> {
            filmController.deleteLikeFromFilm(film.getId(), user.getId());
        });
    }

    @Test
    public void errorRemoveLikeFromFilmWhichIsNotInStorage() {
        User user = getTestUser();
        userController.createUser(user);
        assertThrows(NotFoundException.class, () -> {
            filmController.deleteLikeFromFilm(1, user.getId());
        });
    }
}