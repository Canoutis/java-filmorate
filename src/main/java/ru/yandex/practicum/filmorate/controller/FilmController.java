package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.FilmSaveException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    public static int generationId = 0;

    private HashMap<Integer, Film> hmFilms = new HashMap<>();

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(hmFilms.values());
    }

    @PostMapping(value = "/film")
    public Film create(@Valid @RequestBody Film film) {
        if (film.getName().isEmpty() || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
                || film.getDuration() <= 0) {
            log.warn("Ошибка занесения фильма. Ошибка входных данных! " + film);
            throw new FilmSaveException("Ошибка занесения фильма. Ошибка входных данных!");
        } else {
            film.setId(++generationId);
            hmFilms.put(film.getId(), film);
            return film;
        }
    }

    @PatchMapping(value = "/film")
    public Film update(@Valid @RequestBody Film film) {
        if (film.getName().isEmpty() || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
                || film.getDuration() <= 0) {
            log.warn("Ошибка обновления фильма с id={}. Ошибка входных данных! " + film, film.getId());
            throw new FilmSaveException("Ошибка входных данных!");
        } else {
            hmFilms.put(film.getId(), film);
            return film;
        }
    }
}
