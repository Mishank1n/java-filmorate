package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Getter
@RequestMapping("/films")

public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmStorage) {
        this.filmService = filmStorage;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Получен запрос на получение списка всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{film-id}")
    public Film getFilm(@PathVariable("film-id") Integer filmId) {
        log.info("Получен запрос на получение фильма с id = {}", filmId);
        return filmService.getFilm(filmId);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма");
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма c id = {}", newFilm.getId());
        return filmService.updateFilm(newFilm);
    }

    @DeleteMapping("/{film-id}")
    public String deleteFilm(@PathVariable("film-id") Integer filmId) {
        log.info("Получен запрос на удаление фильма c id = {}", filmId);
        return filmService.deleteFilm(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Получен запрос на вывод самых популярных фильмов");
        return filmService.getPopularFilms(count);
    }

    @PutMapping("/{id}/like/{user-id}")
    public Set<Integer> addLikeToFilm(@PathVariable Integer id, @PathVariable("user-id") Integer userId) {
        log.info("Получен запрос на добавление лайка пользователем c id = {} фильму с id = {}", userId, id);
        return filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{user-id}")
    public String deleteLikeFromFilm(@PathVariable Integer id, @PathVariable("user-id") Integer userId) {
        log.info("Получен запрос на удаления лайка у фильма с id = {} пользователя с id = {}", id, userId);
        return filmService.deleteLikeFromFilm(id, userId);
    }
}
