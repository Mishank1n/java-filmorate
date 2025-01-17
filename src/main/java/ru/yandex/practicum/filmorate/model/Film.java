package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Integer id;
    @NotBlank(message = "Название не может быть пустым!")
    String name;
    @Size(max = 200, message = "Длина описания не должна превышать 200 символов!")
    String description;
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом!")
    Integer duration;
}