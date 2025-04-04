package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbcTemplate.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbcTemplate.query(query, mapper, params);
    }

    protected boolean delete(String query, Object... params) {
        int rowsDeleted = jdbcTemplate.update(query, params);
        return rowsDeleted > 0;
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbcTemplate.update(query, params);
    }


    protected void insert(String query, Object... params) {
        jdbcTemplate.update(query, params);
    }
}