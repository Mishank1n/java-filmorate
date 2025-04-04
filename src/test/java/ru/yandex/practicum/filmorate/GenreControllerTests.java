package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreController.class, GenreDbStorage.class, GenreRowMapper.class, GenreService.class})
public class GenreControllerTests {
    private final GenreController genreController;

    @Test
    public void getGenre() {
        Assertions.assertEquals(genreController.getById(1), new Genre(1, "Комедия"));
    }

    @Test
    public void getNotGenre() {
        Assertions.assertThrows(NotFoundException.class, () -> genreController.getById(null));
    }

    @Test
    public void getAllGenres() {
        Assertions.assertEquals(6, genreController.getAll().size());
    }

}
