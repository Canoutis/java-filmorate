package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public Film addUsersLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addUsersLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public Film removeUsersLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.removeUsersLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}")
    public void removeFilmById(@PathVariable int id) {
        filmService.removeFilmById(id);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping(value = "/films/director/{directorId}")
    public List<Film> getDirectorSortedPopularFilms(@PathVariable int directorId,
                                                    @RequestParam(required = false, defaultValue = "likes") String sortBy) {
        return filmService.getDirectorSortedPopularFilms(directorId, sortBy);
    }
}
