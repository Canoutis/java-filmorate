package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    List<Genre> findAll();

    Genre getGenreById(int id);
}
