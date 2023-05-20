package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
public class FilmController {

    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return inMemoryFilmStorage.findAll();
    }


    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.update(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public Film addUsersLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addUsersLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public Film removeUsersLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.removeUsersLike(id, userId);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
