package ru.yandex.practicum.filmorate.exception;

public class ThingIsAlreadyContain extends RuntimeException {
    public ThingIsAlreadyContain(String message) {
        super(message);
    }
}
