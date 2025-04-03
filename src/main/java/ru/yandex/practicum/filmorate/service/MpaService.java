package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa getById(Integer mpaId) {
        log.info("Началось получение рейтинга MPA c id = {}", mpaId);
        Optional<Mpa> mpa = mpaDbStorage.getById(mpaId);
        if (mpa.isPresent()) {
            log.info("Рейтинг MPA c id = {} найден и получен", mpaId);
            return mpa.get();
        } else {
            log.error("Mpa с id = {} не найден", mpaId);
            throw new NotFoundException(String.format("Mpa с id = %d не найден", mpaId));
        }
    }

    public List<Mpa> getAll() {
        log.info("Началось получение списка всех рейтингов MPA через запрос в базу данных");
        return mpaDbStorage.getAll();
    }
}
