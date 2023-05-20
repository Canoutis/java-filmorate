package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectSaveException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest
public class FilmControllerTest {

    private final FilmController controller;

    @Autowired
    public FilmControllerTest(FilmController controller) {
        this.controller = controller;
    }

    @Test
    void shouldThrowExceptionWithEmptyName() {
        Film film = Film.builder()
                .name("")
                .description("description")
                .duration(114)
                .releaseDate(LocalDate.of(2011, 3, 10))
                .build();
        Assertions.assertThrows(ObjectSaveException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowExceptionWithLongDescription() {
        Film film = Film.builder()
                .name("The Lincoln Lawyer")
                .description("Микки Холлер – блестящий и удачливый адвокат из Лос-Анджелеса, чей яркий имидж и образ " +
                        "жизни отлично дополняет его любимая машина «Линкольн». Очередное дело – нападение богатого " +
                        "клиента Луи Руле на проститутку сначала казалось легким, и он без труда добился " +
                        "оправдательного приговора для подзащитного. Но вскоре Микки понимает, что его клиент " +
                        "скрывает правду. Пока Холлер пытается вывести Руле на чистую воду, его подставляют " +
                        "по-крупному, и теперь уже угроза тюремного заключения нависает над самим Холлером.")
                .duration(114)
                .releaseDate(LocalDate.of(2011, 3, 10))
                .build();
        Assertions.assertThrows(ObjectSaveException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowExceptionWithIncorrectDuration() {
        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .duration(0)
                .releaseDate(LocalDate.of(2011, 3, 10))
                .build();
        Assertions.assertThrows(ObjectSaveException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowExceptionWithUnbelievableReleaseDate() {
        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .duration(114)
                .releaseDate(LocalDate.of(1600, 3, 10))
                .build();
        Assertions.assertThrows(ObjectSaveException.class, () -> controller.create(film));
    }

    @Test
    void shouldBeOkWithCorrectFilm() {
        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .duration(114)
                .releaseDate(LocalDate.of(2006, 3, 10))
                .build();
        Film savedFilm = controller.create(film);
        Assertions.assertEquals(1, controller.findAll().size());
        Assertions.assertEquals(film.getName(), savedFilm.getName());
        Assertions.assertEquals(film.getDescription(), savedFilm.getDescription());
        Assertions.assertEquals(film.getDuration(), savedFilm.getDuration());
        Assertions.assertEquals(film.getReleaseDate(), savedFilm.getReleaseDate());
    }
}
