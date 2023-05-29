package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectSaveException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private static final LocalDate MIN_POSSIBLE_DATE = LocalDate.of(1895, 12, 28);

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        if (isInvalidFilm(film)) {
            throw new ObjectSaveException(String.format("Ошибка занесения фильма. Ошибка входных данных! %s", film));
        } else {
            return filmStorage.create(film);
        }
    }

    public Film update(Film film) {
        if (isInvalidFilm(film)) {
            throw new ObjectUpdateException(String.format("Ошибка обновления фильма! Ошибка входных данных! Id=%s", film.getId()));
        } else {
            return filmStorage.update(film);
        }
    }

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addUsersLike(int filmId, int userId) {
        return filmStorage.addUserLike(filmId, userId);
    }

    public Film removeUsersLike(int filmId, int userId) {
        return filmStorage.removeUserLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private boolean isInvalidFilm(Film film) {
        return film.getReleaseDate().isBefore(MIN_POSSIBLE_DATE);
    }
}
