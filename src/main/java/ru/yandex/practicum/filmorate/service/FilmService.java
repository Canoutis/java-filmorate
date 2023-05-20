package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Film addUsersLike(int filmId, int userId) {
        Film film = inMemoryFilmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeUsersLike(int filmId, int userId) {
        Film film = inMemoryFilmStorage.getFilmById(filmId);
        User user = inMemoryUserStorage.getUserById(userId);
        film.getLikes().remove(user.getId());
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return inMemoryFilmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size())).limit(count)
                .collect(Collectors.toList());
    }
}
