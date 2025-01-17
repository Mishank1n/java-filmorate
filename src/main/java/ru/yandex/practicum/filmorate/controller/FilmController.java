package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> baseOfFilms = new HashMap<>();
    private final String errorMessageOfReleaseDate = "Неверная дата! Должна быть не раньше 28 декабря 1895 года!";

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Возвращены все фильмы");
        return baseOfFilms.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Началось создание фильма");
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error(errorMessageOfReleaseDate);
            throw new ValidationException(errorMessageOfReleaseDate);
        }
        film.setId(getNewId());
        log.info("Был установлен id = {} для фильма", film.getId());
        baseOfFilms.put(film.getId(), film);
        log.info("Фильм с id = {} создан и добавлен в базу", film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Началось обновление фильма");
        if (newFilm.getId() == null) {
            log.error("Получен фильм с пустым id");
            throw new ValidationException("Id не должен быть пустым!");
        } else if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error(errorMessageOfReleaseDate);
            throw new ValidationException(errorMessageOfReleaseDate);
        } else if (baseOfFilms.containsKey(newFilm.getId())) {
            log.info("Фильм c id = {} был найден", newFilm.getId());
            Film oldFilm = baseOfFilms.get(newFilm.getId());
            if (!newFilm.getName().equals(oldFilm.getName())) {
                log.info("Изменено имя фильма");
                oldFilm.setName(newFilm.getName());
            }
            if (!newFilm.getDescription().isEmpty() && !newFilm.getDescription().equals(oldFilm.getDescription())) {
                log.info("Изменено описание фильма");
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getDuration() != null && !newFilm.getDuration().equals(oldFilm.getDuration())) {
                log.info("Изменена длительность фильма");
                oldFilm.setDuration(newFilm.getDuration());
            }
            if (newFilm.getReleaseDate() != null && !newFilm.getReleaseDate().equals(oldFilm.getReleaseDate())) {
                log.info("Изменена дата релиза");
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            log.info("Фильм c id = {} обновлен", oldFilm.getId());
            return oldFilm;
        } else {
            log.error("Фильм с id = {} не найден!", newFilm.getId());
            throw new NotFoundException(String.format("Фильм с id = %d не найден!", newFilm.getId()));
        }

    }

    public Integer getNewId() {
        int returnId = baseOfFilms.keySet().stream().max(Integer::compareTo).orElse(0);
        return ++returnId;
    }
}
