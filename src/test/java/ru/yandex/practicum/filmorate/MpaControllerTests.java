package ru.yandex.practicum.filmorate;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.controller.MpaController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

@JdbcTest
@AllArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@Import({MpaController.class, MpaService.class, MpaRowMapper.class, MpaDbStorage.class})
public class MpaControllerTests {
    private final MpaController mpaController;

    @Test
    public void getMpa() {
        Assertions.assertEquals(mpaController.getMpaById(1), new Mpa(1, "G", "у фильма нет возрастных ограничений"));
    }

    @Test
    public void getNullMpa() {
        Assertions.assertThrows(NotFoundException.class, () -> mpaController.getMpaById(100));
    }

    @Test
    public void getAllMpa() {
        Assertions.assertEquals(5, mpaController.getAll().size());
    }
}
