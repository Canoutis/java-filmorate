package ru.yandex.practicum.filmorate.exception;

public class UserSaveException extends RuntimeException {
    public UserSaveException(final String message) {
        super(message);
    }
}