package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTests {

    private FilmController filmController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    public void addFilm() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getNewId());
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
        film.setId(filmController.getNewId());
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
        film.setId(filmController.getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        Film newFilm = new Film();
        newFilm.setName("Test name");
        newFilm.setId(filmController.getNewId());
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
        film.setId(filmController.getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        var errors = validator.validate(film);
        assertEquals(1, errors.size());
        assertEquals("Название не может быть пустым!", errors.iterator().next().getMessage());
    }

    @Test
    public void errorAddFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getNewId());
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
        film.setId(filmController.getNewId());
        film.setReleaseDate(LocalDate.of(1720, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void errorAddFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getNewId());
        film.setReleaseDate(LocalDate.of(1720, 12, 12));
        film.setDescription("T".repeat(201));
        film.setDuration(200);
        var errors = validator.validate(film);
        assertEquals(1, errors.size());
        assertEquals("Длина описания не должна превышать 200 символов!", errors.iterator().next().getMessage());
    }

    @Test
    public void errorUpdateFilmWhichIsNotInBase() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getNewId());
        film.setReleaseDate(LocalDate.of(2020, 12, 12));
        film.setDescription("Description of test film");
        film.setDuration(200);
        filmController.createFilm(film);
        Film newFilm = new Film();
        newFilm.setName("Test name");
        newFilm.setId(filmController.getNewId());
        newFilm.setReleaseDate(LocalDate.of(2020, 12, 12));
        newFilm.setDescription("Description of test film");
        newFilm.setDuration(300);
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(newFilm));
    }

    @Test
    public void errorUpdateFilmWithNullId() {
        Film film = new Film();
        film.setName("Test name");
        film.setId(filmController.getNewId());
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
}
