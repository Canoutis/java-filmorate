package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectSaveException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
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
            throw new ObjectUpdateException(String.format("Ошибка обновления фильма! Ошибка входных данных! Id=%d", film.getId()));
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

    public List<Film> getPopular(int count, int genreId, int year) {
        if (genreId == 0 && year == 0) {
            return filmStorage.getPopularFilms(count);
        } else if (genreId != 0 && year != 0) {
            return filmStorage.getPopularByGenreAndYear(genreId, year, count);
        } else if (genreId != 0) {
            return filmStorage.getPopularByGenre(genreId, count);
        } else {
            return filmStorage.getPopularByYear(year, count);
        }
    }

    public List<Film> getDirectorSortedPopularFilms(int directorId, String sortBy) {
        if ("year".equals(sortBy)) {
            return filmStorage.getDirectorFilmsSortedByYear(directorId);
        } else {
            return filmStorage.getDirectorFilmsSortedByLikes(directorId);
        }
    }

    public void removeFilmById(int filmId) {
        filmStorage.removeFilmById(filmId);
    }

    private boolean isInvalidFilm(Film film) {
        return film.getReleaseDate().isBefore(MIN_POSSIBLE_DATE);
    }

    public List<Film> filmSearch(String query, String searchBy) {
        List<Film> films;

        if (searchBy.contains("title") && searchBy.contains("director")) {
            films = filmStorage.findByTitleContainingOrDirectorContaining(query, query);
            log.info("Поиск по названию и режиссеру = {}", query);
        } else if (searchBy.equals("title")) {
            films = filmStorage.findByTitleContaining(query);
            log.info("Поиск по названию = {}", query);
        } else if (searchBy.equals("director")) {
            films = filmStorage.findByDirectorContaining(query);
            log.info("Поиск по режиссеру = {}", query);
        } else {
            films = Collections.emptyList();
            log.info("Поиск можно сделать только по режиссеру и названию");
        }

        return films;
    }

}
