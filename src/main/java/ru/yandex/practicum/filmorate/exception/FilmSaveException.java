package ru.yandex.practicum.filmorate.exception;

public class FilmSaveException extends RuntimeException {
    public FilmSaveException(final String message) {
        super(message);
    }
}