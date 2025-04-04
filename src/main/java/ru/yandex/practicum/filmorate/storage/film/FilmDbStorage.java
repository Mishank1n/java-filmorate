package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Slf4j
@Qualifier
@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String ADD_FILM = "MERGE INTO films(id, name, description, releaseDate, duration, MPA_id)  KEY(id)" + "VALUES (?, ?,?,?,?,?)";
    private static final String ADD_FILM_GENRES = "MERGE INTO films_genres(film_id, genre_id) KEY(film_id, genre_id) VALUES (?,?)";
    private static final String GET_FILM_BY_ID = "SELECT * FROM films WHERE id = ?";
    private static final String GET_FILM_MPA = "SELECT * FROM MPA WHERE id in (SELECT MPA_id FROM films WHERE id = ?)";
    private static final String GET_FILM_GENRES = "SELECT * FROM genres WHERE id IN (SELECT genre_id FROM films_genres WHERE film_id = ?)";
    private static final String GET_ALL_FILMS = "SELECT * FROM films";
    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, MPA_id = ? WHERE id = ?";
    private static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";
    private static final String DELETE_FILM_GENRES = "DELETE FROM films_genres WHERE film_id = ?";
    private static final String ADD_LIKE_TO_FILM = "MERGE INTO likes(film_id, user_id) KEY (film_id, user_id)" + "VALUES (?, ?)";
    private static final String GET_POPULAR_FILMS = "SELECT * FROM FILMS WHERE id IN " + "(SELECT film_id FROM likes GROUP BY film_id ORDER BY COUNT(*) DESC) LIMIT ?";
    private static final String DELETE_LIKE_FROM_FILM = "DELETE FROM likes WHERE film_id = ? AND user_id = ? ";
    private static final String GET_USERS_THAT_LIKED_FILM = "SELECT * FROM users WHERE id IN(SELECT user_id FROM LIKES WHERE film_id = ?)";
    private static final String DELETE_LIKES_WHEN_DELETE_FILM = "DELETE FROM likes WHERE film_id = ?";
    private static final MpaRowMapper mpaMapper = new MpaRowMapper();
    private static final GenreRowMapper genreMapper = new GenreRowMapper();
    private static final UserRowMapper userMapper = new UserRowMapper();
    private final List<Genre> genres = jdbcTemplate.query("SELECT * FROM genres", genreMapper);
    private final List<Mpa> mpaList = jdbcTemplate.query("SELECT * FROM MPA", mpaMapper);


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Film> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public void addFilm(Film film) {
        log.info("Получен запрос в базу данных на добавление фильма с id = {} ", film.getId());
        if (film.getMpa() != null && mpaList.stream().map(Mpa::getId).noneMatch(integer -> integer == film.getMpa().getId())) {
            log.error("MPA с id = {} не найден", film.getMpa().getId());
            throw new NotFoundException(String.format("MPA с id = %d не найден", film.getMpa().getId()));
        }
        insert(ADD_FILM, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        log.info("В базу данных был добавлен фильм с id = {}", film.getId());
        for (Genre genre : film.getGenres()) {
            if (genres.stream().map(Genre::getId).anyMatch(integer -> integer == genre.getId())) {
                log.info("К фильму с id = {} был добавлен жанр с id = {}", film.getId(), genre.getId());
                insert(ADD_FILM_GENRES, film.getId(), genre.getId());
            } else {
                log.error("Жанр с id = {} не найден", genre.getId());
                throw new NotFoundException(String.format("Жанр с id = %d не найден", genre.getId()));
            }
        }
    }

    @Override
    public Film getFilm(Integer filmId) {
        log.info("Получен запрос в базу данных о получении фильма с id = {}", filmId);
        try {
            Optional<Film> film = findOne(GET_FILM_BY_ID, filmId);
            Optional<Mpa> mpa = Optional.ofNullable(jdbcTemplate.queryForObject(GET_FILM_MPA, mpaMapper, filmId));
            List<Genre> genres = jdbcTemplate.query(GET_FILM_GENRES, genreMapper, filmId);
            if (film.isPresent()) {
                Film resultFilm = film.get();
                if (mpa.isPresent()) {
                    resultFilm.setMpa(mpa.get());
                }
                if (!genres.isEmpty()) resultFilm.setGenres(new HashSet<>(genres));
                log.info("Фильм с id = {} был найден в базе данных", filmId);
                return resultFilm;
            } else {
                log.error("Фильм с id = {} не был найден в базе данны", filmId);
                return null;
            }
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public void updateFilm(Film newFilm) {
        log.info("Получен запрос на обновление фильма с с id = {} в базе данных", newFilm.getId());
        delete(DELETE_FILM_GENRES, newFilm.getId());
        update(UPDATE_FILM, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId());
        log.info("Обновлен фильм с с id = {} в базе данных", newFilm.getId());
        for (Genre genre : newFilm.getGenres()) {
            insert(ADD_FILM_GENRES, newFilm.getId(), genre.getId());
        }
        log.info("Обновлены все жанры фильма с id = {}", newFilm.getId());
    }

    @Override
    public void deleteFilm(Integer filmId) {
        log.info("Получен запрос на удаление фильма с id = {} из базы данных", filmId);
        delete(DELETE_FILM_GENRES, filmId);
        delete(DELETE_FILM, filmId);
        delete(DELETE_LIKES_WHEN_DELETE_FILM, filmId);
        log.info("Удален фильм с id = {} и его жанры из базы данных", filmId);
    }

    @Override
    public List<Film> getBaseOfFilms() {
        log.info("Получен запрос в базу данных на получение всех фильмов");
        return findMany(GET_ALL_FILMS);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        log.info("Получен запрос в базу данных на получение {} популярных фильмов", count);
        return findMany(GET_POPULAR_FILMS, count);
    }

    @Override
    public void addLikeToFilm(Integer filmId, Integer userId) {
        log.info("Получен запрос в базу данных на добавление лайка к фильму с id = {} от пользователя с id = {}", filmId, userId);
        insert(ADD_LIKE_TO_FILM, filmId, userId);
        log.info("Лайк был добавлен");
    }

    @Override
    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        log.info("Получен запрос в базу данных на удаление лайка у фильма с id = {} от пользователя с id = {} ", filmId, userId);
        delete(DELETE_LIKE_FROM_FILM, filmId, userId);
        log.info("Лайк был удален");
    }

    @Override
    public List<User> getUserLikesOnFilm(Integer filmId) {
        log.info("Получен запрос в базу данных на получение списка пользователей, которые поставили лайк фильму с id = {}", filmId);
        return jdbcTemplate.query(GET_USERS_THAT_LIKED_FILM, userMapper, filmId);
    }
}
