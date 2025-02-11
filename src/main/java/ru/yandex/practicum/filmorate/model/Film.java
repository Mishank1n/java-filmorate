package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Integer id;
    @NotBlank(message = "Название фильма не может быть пустым!")
    String name;
    @Size(max = 200, message = "Длина описания фильма не должна превышать 200 символов!")
    String description;
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом!")
    Integer duration;
    Set<Integer> likesOfUsers = new HashSet<>();
}