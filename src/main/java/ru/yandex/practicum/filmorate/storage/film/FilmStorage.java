package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {
    void addFilm(Film film);

    Film getFilm(Integer id);

    void deleteFilm(Integer idOfFilm);

    List<Film> getBaseOfFilms();

    void updateFilm(Film newFilm);

    List<Film> getPopularFilms(Integer count);

    void addLikeToFilm(Integer filmId, Integer userId);

    void deleteLikeFromFilm(Integer filmId, Integer userId);

    List<User> getUserLikesOnFilm(Integer filmId);
}
