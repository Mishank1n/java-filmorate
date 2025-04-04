package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class GenreDbStorage {
    private static final String GET_ALL_GENRES = "SELECT * FROM genres";
    private static final String GET_GENRE_BY_ID = "SELECT * FROM genres WHERE id = ?";
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> mapper;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Genre> mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    public Optional<Genre> getById(Integer genreId) {
        log.info("Запрос в базу данных для получения жанра с id = {}", genreId);
        try {
            Genre result = jdbcTemplate.queryForObject(GET_GENRE_BY_ID, mapper, genreId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public List<Genre> getAll() {
        log.info("Запрос в базу данных для получение всех жанров");
        return jdbcTemplate.query(GET_ALL_GENRES, mapper);
    }
}
