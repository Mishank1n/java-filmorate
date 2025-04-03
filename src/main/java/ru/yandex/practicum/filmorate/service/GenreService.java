package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GenreService {
    public final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public List<Genre> getAll() {
        log.info("Началось получение списка всех жанров через запрос в базу данных");
        return genreDbStorage.getAll();
    }


    public Genre getById(Integer genreId) {
        log.info("Началось возвращение жанра с id = {}", genreId);
        Optional<Genre> result = genreDbStorage.getById(genreId);
        if (result.isPresent()) {
            log.info("Получен жанр с id = {}", genreId);
            return result.get();
        } else {
            log.info("Жанр с id = {} не найден", genreId);
            throw new NotFoundException(String.format("Жанр с id = %d не найден", genreId));
        }
    }
}
