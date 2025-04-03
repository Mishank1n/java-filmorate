package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ThingIsAlreadyContain;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
public class FilmService {

    private final String errorMessageOfReleaseDate = "Неверная дата! Должна быть не раньше 28 декабря 1895 года!";
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final UserService userService;
    private final String errorMessageOfFindFilmForLog = "Фильм с id = {} не найден!";
    private final String errorMessageOfFindFilmForException = "Фильм с id = %d не найден!";

    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.userService = userService;
    }

    public Film createFilm(Film film) {
        log.info("Началось создание фильма");
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error(errorMessageOfReleaseDate);
            throw new ValidationException(errorMessageOfReleaseDate);
        }
        film.setId(getNewId());
        filmStorage.addFilm(film);
        log.info("Фильм с id = {} создан и добавлен в базу", film.getId());
        return film;
    }

    public Film getFilm(Integer filmId) {
        log.info("Началось получение фильма");
        if (filmStorage.getFilm(filmId) != null) {
            log.info("Получен фильм с id = {}", filmId);
            return filmStorage.getFilm(filmId);
        } else {
            log.error(errorMessageOfFindFilmForLog, filmId);
            throw new NotFoundException(String.format(errorMessageOfFindFilmForException, filmId));
        }
    }

    public List<Film> getAllFilms() {
        log.info("Возвращены все фильмы");
        return filmStorage.getBaseOfFilms();
    }

    public Film updateFilm(Film newFilm) {
        log.info("Началось обновление фильма");
        if (newFilm.getId() == null) {
            log.error("Получен фильм с пустым id");
            throw new ValidationException("Id не должен быть пустым!");
        } else if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error(errorMessageOfReleaseDate);
            throw new ValidationException(errorMessageOfReleaseDate);
        } else if (filmStorage.getFilm(newFilm.getId()) != null) {
            Film oldFilm = filmStorage.getFilm(newFilm.getId());
            if (!newFilm.getName().equals(oldFilm.getName())) {
                oldFilm.setName(newFilm.getName());
            }
            if (!newFilm.getDescription().isEmpty() && !newFilm.getDescription().equals(oldFilm.getDescription())) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getDuration() != null && !newFilm.getDuration().equals(oldFilm.getDuration())) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            if (newFilm.getReleaseDate() != null && !newFilm.getReleaseDate().equals(oldFilm.getReleaseDate())) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getGenres() != null && !newFilm.getGenres().equals(oldFilm.getGenres())) {
                oldFilm.setGenres(newFilm.getGenres());
            }
            if (newFilm.getMpa() != null && !newFilm.getMpa().equals(oldFilm.getMpa())) {
                oldFilm.setMpa(newFilm.getMpa());
            }
            filmStorage.updateFilm(oldFilm);
            log.info("Фильм c id = {} обновлен", oldFilm.getId());
            return oldFilm;
        } else {
            log.error(errorMessageOfFindFilmForLog, newFilm.getId());
            throw new NotFoundException(String.format(errorMessageOfFindFilmForException, newFilm.getId()));
        }
    }

    public String deleteFilm(Integer filmId) {
        log.info("Началось удаление фильма");
        if (filmStorage.getFilm(filmId) != null) {
            filmStorage.deleteFilm(filmId);
            log.info("Фильм с id = {} найден и удален", filmId);
            return String.format("Фильм с id = %d найден и удален", filmId);
        } else {
            log.error(errorMessageOfFindFilmForLog, filmId);
            throw new NotFoundException(String.format(errorMessageOfFindFilmForException, filmId));
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Начался вывод списка самых популярных фильмов по лайкам");
        List<Film> films = filmStorage.getPopularFilms(count);
        films.forEach(System.out::println);
        return films;
    }

    public Set<Integer> addLikeToFilm(Integer filmId, Integer userId) {
        log.info("Началось добавление лайка фильму c id = {} от пользователя с id = {}", filmId, userId);
        if (filmStorage.getFilm(filmId) != null && userStorage.getUser(userId) != null) {
            if (filmStorage.getUserLikesOnFilm(filmId).contains(userStorage.getUser(userId))) {
                log.error("Пользователь с id = {} уже поставил лайк фильму с id = {}", userId, filmId);
                throw new ThingIsAlreadyContain(String.format("Пользователь с id = %d уже поставил лайк фильму с id = %d", userId, filmId));
            } else {
                filmStorage.addLikeToFilm(filmId, userId);
                log.info("Пользователь поставил с id = {} поставил лайк фильму с id = {}", userId, filmId);
                return filmStorage.getUserLikesOnFilm(filmId).stream().map(User::getId).collect(Collectors.toSet());
            }
        } else if (filmStorage.getFilm(filmId) == null) {
            log.error(errorMessageOfFindFilmForLog, filmId);
            throw new NotFoundException(String.format(errorMessageOfFindFilmForException, filmId));
        } else {
            log.error(userService.getErrorMessageFindUserForLog(), userId);
            throw new NotFoundException(String.format(userService.getErrorMessageFindUserForException(), userId));
        }
    }

    public String deleteLikeFromFilm(Integer filmId, Integer userId) {
        log.info("Началось удаление лайка у фильма с id = {} от пользователя с id = {}", filmId, userId);
        if (filmStorage.getFilm(filmId) == null) {
            log.error(errorMessageOfFindFilmForLog, filmId);
            throw new NotFoundException(String.format(errorMessageOfFindFilmForException, filmId));
        }
        if (userStorage.getUser(userId) == null) {
            log.error(userService.getErrorMessageFindUserForLog(), userId);
            throw new NotFoundException(String.format(userService.getErrorMessageFindUserForException(), userId));
        }
        if (!filmStorage.getUserLikesOnFilm(filmId).stream().map(User::getId).toList().contains(userId)) {
            log.error("Лайк пользователя c id = {} не найден", userId);
            throw new NotFoundException(String.format("Лайк пользователя c id = %d не найден", userId));
        }
        filmStorage.deleteLikeFromFilm(filmId, userId);
        log.info("Пользователь с id = {} удалил лайк у фильма с id = {}", userId, filmId);
        return String.format("Пользователь с id = %d удалил лайк у фильма с id = %d", userId, filmId);
    }


    public Integer getNewId() {
        List<Film> films = filmStorage.getBaseOfFilms();
        int returnId = films.stream().map(Film::getId).max(Integer::compareTo).orElse(0);
        return ++returnId;
    }
}
