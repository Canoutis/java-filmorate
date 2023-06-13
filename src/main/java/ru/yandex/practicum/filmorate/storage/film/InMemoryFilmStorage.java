package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int generationId = 0;

    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        film.setId(++generationId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ObjectUpdateException(String.format("Ошибка обновления фильма. Фильм не найден! %s", film));
        } else {
            films.put(film.getId(), film);
            return film;
        }
    }

    @Override
    public Film getFilmById(int filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            throw new ObjectNotFoundException(String.format("Фильм не найден! %x", filmId));
        }
    }

    @Override
    public Film addUserLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return findAll()
                .stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size())).limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getPopularByYear(int releaseYear, int count) {
        return findAll()
                .stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size())).limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getPopularByGenreAndYear(int genreId, int releaseYear, int count) {
        return findAll()
                .stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size())).limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getPopularByGenre(int genreId, int count) {
        return findAll()
                .stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size())).limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film removeUserLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        film.getLikes().remove(userId);
        return film;
    }

    @Override
    public List<Film> getDirectorFilmsSortedByYear(int directorId) {
        return null;
    }

    @Override
    public List<Film> getDirectorFilmsSortedByLikes(int directorId) {
        return null;
    }

    @Override
    public void removeFilmById(int filmId) {
        if (films.containsKey(filmId)) {
            films.remove(filmId);
        } else {
            throw new ObjectNotFoundException(String.format("Фильм не найден! %x", filmId));
        }
    }

    @Override
    public List<Film> findByTitleContaining(String query) {
        return null;
    }

    @Override
    public List<Film> findByDirectorContaining(String query) {
        return null;
    }

    @Override
    public List<Film> findByTitleContainingOrDirectorContaining(String titleQuery, String directorQuery) {
        return null;
    }
}
