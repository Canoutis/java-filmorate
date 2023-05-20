package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectSaveException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int generationId = 0;

    private final HashMap<Integer, Film> films = new HashMap<>();

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film create(Film film) {
        if (isInvalidFilm(film)) {
            log.warn("Ошибка занесения фильма. Ошибка входных данных! " + film);
            throw new ObjectSaveException("Ошибка занесения фильма. Ошибка входных данных!");
        } else {
            film.setId(++generationId);
            film.setLikes(new HashSet<>());
            films.put(film.getId(), film);
            return film;
        }
    }

    public Film update(Film film) {
        if (isInvalidFilm(film) || !films.containsKey(film.getId())) {
            log.warn("Ошибка обновления фильма с id={}. Ошибка входных данных! " + film, film.getId());
            throw new ObjectUpdateException("Ошибка входных данных!");
        } else {
            if (film.getLikes() == null) film.setLikes(new HashSet<>());
            films.put(film.getId(), film);
            return film;
        }
    }

    public Film getFilmById(int filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            throw new ObjectNotFoundException("Фильм не найден!");
        }
    }

    private boolean isInvalidFilm(Film film) {
        return film.getName().isEmpty()
                || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
                || film.getDuration() <= 0;
    }
}
