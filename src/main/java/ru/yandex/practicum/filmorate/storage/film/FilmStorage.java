package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film getFilmById(int id);

    Film addUserLike(int filmId, int userId);

    List<Film> getPopularFilms(int count);

    Film removeUserLike(int filmId, int userId);

    List<Film> getDirectorFilmsSortedByYear(int directorId);

    List<Film> getDirectorFilmsSortedByLikes(int directorId);

    void removeFilmById(int filmId);

    List<Film> findByTitleContaining(String query);

    List<Film> findByDirectorContaining(String query);

    List<Film> findByTitleContainingOrDirectorContaining(String titleQuery, String directorQuery);
}