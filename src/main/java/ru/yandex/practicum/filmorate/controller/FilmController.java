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

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) int count,
                                      @RequestParam(defaultValue = "0", required = false) int genreId,
                                      @RequestParam(defaultValue = "0", required = false) int year) {
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping(value = "/films/director/{directorId}")
    public List<Film> getDirectorSortedPopularFilms(@PathVariable int directorId,
                                                    @RequestParam(required = false, defaultValue = "likes") String sortBy) {
        return filmService.getDirectorSortedPopularFilms(directorId, sortBy);
    }

    @GetMapping("/films/search")
    public List<Film> searchFilms(@RequestParam("query") String query, @RequestParam("by") String searchBy) {
        return filmService.filmSearch(query, searchBy);
    }

    @GetMapping(value = "/users/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable int id) {
        return filmService.getRecommendations(id);
    }

}
