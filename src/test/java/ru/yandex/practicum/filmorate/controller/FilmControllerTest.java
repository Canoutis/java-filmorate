package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class FilmControllerTest {

    private final FilmController controller;

    @Autowired
    public FilmControllerTest(FilmController controller) {
        this.controller = controller;
    }

    @Test
    void shouldBeOkCreateCorrectFilm() {
        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .duration(114)
                .releaseDate(LocalDate.of(2006, 3, 10))
                .build();
        Film savedFilm = controller.create(film);
        Assertions.assertEquals(film.getName(), savedFilm.getName());
        Assertions.assertEquals(film.getDescription(), savedFilm.getDescription());
        Assertions.assertEquals(film.getDuration(), savedFilm.getDuration());
        Assertions.assertEquals(film.getReleaseDate(), savedFilm.getReleaseDate());
    }

    @Test
    void shouldBeOkUpdateCorrectFilm() {
        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .duration(114)
                .releaseDate(LocalDate.of(2006, 3, 10))
                .build();
        Film savedFilm = controller.create(film);
        savedFilm.setName("New Film");
        Film updatedFilm = controller.update(savedFilm);
        Assertions.assertEquals(savedFilm.getName(), updatedFilm.getName());
        Assertions.assertEquals(savedFilm.getDescription(), updatedFilm.getDescription());
        Assertions.assertEquals(savedFilm.getDuration(), updatedFilm.getDuration());
        Assertions.assertEquals(savedFilm.getReleaseDate(), updatedFilm.getReleaseDate());
    }

    @Test
    void shouldGetCorrectFilmsList() {
        List<Film> films = controller.findAll();
        Film film = Film.builder()
                .name("Film 2")
                .description("Description 2")
                .duration(220)
                .releaseDate(LocalDate.of(2004, 3, 10))
                .build();
        controller.create(film);
        List<Film> films2 = controller.findAll();
        Assertions.assertEquals(films.size() + 1, films2.size());
    }
}
