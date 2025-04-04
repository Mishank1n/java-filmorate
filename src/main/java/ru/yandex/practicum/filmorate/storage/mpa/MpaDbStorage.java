package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MpaDbStorage extends BaseRepository<Mpa> {
    private static final String GET_All_MPA = "SELECT * FROM MPA";
    private static final String GET_MPA_BY_ID = "SELECT * FROM MPA WHERE id = ?";


    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Mpa> mapper) {
        super(jdbcTemplate, mapper);
    }


    public Optional<Mpa> getById(Integer mpaId) {
        log.info("Запрос в базу данных получен и начат поиск рейтинга MPA c id = {}", mpaId);
        try {
            Mpa result = jdbcTemplate.queryForObject(GET_MPA_BY_ID, mapper, mpaId);
            log.info("Получен и возвращен рейтинг MPA c id = {}", mpaId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public List<Mpa> getAll() {
        log.info("Запрос в базу данных получен и возращен список всех рейтингов MPA");
        return jdbcTemplate.query(GET_All_MPA, mapper);
    }
}
