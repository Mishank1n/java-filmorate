package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    void addFilm(Film film);

    Film getFilm(Integer id);

    void deleteFilm(Integer idOfFilm);

    Map<Integer, Film> getBaseOfFilms();
}
