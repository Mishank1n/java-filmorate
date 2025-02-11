package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public void addFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public Film getFilm(Integer id) {
        return films.getOrDefault(id, null);
    }

    @Override
    public void deleteFilm(Integer idOfFilm) {
        films.remove(idOfFilm);
    }

    @Override
    public Map<Integer, Film> getBaseOfFilms() {
        return films;
    }


}
