package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectSaveException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

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
            throw new ObjectUpdateException(String.format("Ошибка обновления фильма! Ошибка входных данных! Id=%x", film.getId()));
        } else {
            return filmStorage.update(film);
        }
    }

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addUsersLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeUsersLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().remove(user.getId());
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size())).limit(count)
                .collect(Collectors.toList());
    }

    private boolean isInvalidFilm(Film film) {
        return film.getName().isEmpty()
                || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
                || film.getDuration() <= 0;
    }
}
