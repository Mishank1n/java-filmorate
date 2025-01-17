package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Integer id;
    private String login;
    private String name;
    @Email(message = "Электронная почта должна быть нужного формата! Пример: email@gmail.com")
    private String email;
    @PastOrPresent(message = "Дата рождения не может быть в будущем!")
    private LocalDate birthday;
}
