package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{mpa-id}")
    public Mpa getMpaById(@PathVariable("mpa-id") Integer mpaId) {
        log.info("Получен запрос на получение рейтинга MPA с id = {}", mpaId);
        return mpaService.getById(mpaId);
    }

    @GetMapping
    public List<Mpa> getAll() {
        log.info("Получен запрос на получение всех рейтингов MPA");
        return mpaService.getAll();
    }
}