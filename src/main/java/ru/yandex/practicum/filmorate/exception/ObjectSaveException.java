package ru.yandex.practicum.filmorate.exception;

public class ObjectSaveException extends RuntimeException {
    public ObjectSaveException(final String message) {
        super(message);
    }
}
