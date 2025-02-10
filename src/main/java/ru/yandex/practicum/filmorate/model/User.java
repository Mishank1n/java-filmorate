package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Integer id;
    String login;
    String name;
    @Email(message = "Электронная почта пользователя должна быть нужного формата! Пример: email@gmail.com")
    String email;
    @PastOrPresent(message = "Дата рождения пользователя не может быть в будущем!")
    LocalDate birthday;
    Set<Integer> friends =  new HashSet<>();
}