package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreDaoImpl;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDaoImpl;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final MpaRatingDaoImpl mpaRatingDaoImpl;
    private final UserDbStorage userDbStorage;
    private final GenreDaoImpl genreDaoImpl;

    @Test
    @BeforeEach
    public void cleanFilms() {
        List<Film> films = filmDbStorage.findAll();
        films.forEach(film -> filmDbStorage.removeFilmById(film.getId()));
    }

    @Test
    public void testCreateOneFilm() {
        Film film = Film.builder()
                .name("1+1")
                .description("Нету ручек, нет конфеток!")
                .duration(100)
                .releaseDate(LocalDate.of(2012, 5, 11))
                .mpa(mpaRatingDaoImpl.getMpaRatingById(4))
                .build();
        Film createdFilm = filmDbStorage.create(film);
        Assertions.assertEquals("1+1", createdFilm.getName());
        Assertions.assertEquals("Нету ручек, нет конфеток!", createdFilm.getDescription());
        Assertions.assertEquals(100, createdFilm.getDuration());
        Assertions.assertEquals(LocalDate.of(2012, 5, 11), createdFilm.getReleaseDate());
        Assertions.assertEquals("R", createdFilm.getMpa().getName());
    }

    @Test
    public void testGetOneFilmById() {
        Film film = Film.builder()
                .name("1+1")
                .description("Нету ручек, нет конфеток!")
                .duration(100)
                .releaseDate(LocalDate.of(2012, 5, 11))
                .mpa(mpaRatingDaoImpl.getMpaRatingById(4))
                .build();
        Film createdFilm = filmDbStorage.create(film);
        Film foundFilm = filmDbStorage.getFilmById(createdFilm.getId());
        Assertions.assertEquals(createdFilm.getId(), foundFilm.getId());
        Assertions.assertEquals("1+1", foundFilm.getName());
        Assertions.assertEquals("Нету ручек, нет конфеток!", foundFilm.getDescription());
        Assertions.assertEquals(100, foundFilm.getDuration());
        Assertions.assertEquals(LocalDate.of(2012, 5, 11), foundFilm.getReleaseDate());
        Assertions.assertEquals("R", foundFilm.getMpa().getName());
    }

    @Test
    public void testUpdateOneFilm() {
        Film film = Film.builder()
                .name("1+1")
                .description("Нету ручек, нет конфеток!")
                .duration(100)
                .releaseDate(LocalDate.of(2012, 5, 11))
                .mpa(mpaRatingDaoImpl.getMpaRatingById(4))
                .build();
        Film createdFilm = filmDbStorage.create(film);
        createdFilm.setName("1+2");
        createdFilm.setDescription("Some description");
        createdFilm.setDuration(50);
        createdFilm.setReleaseDate(LocalDate.of(2021, 1, 1));
        createdFilm.setMpa(mpaRatingDaoImpl.getMpaRatingById(1));
        filmDbStorage.update(createdFilm);
        Film updatedFilm = filmDbStorage.getFilmById(createdFilm.getId());
        Assertions.assertEquals("1+2", updatedFilm.getName());
        Assertions.assertEquals("Some description", updatedFilm.getDescription());
        Assertions.assertEquals(50, updatedFilm.getDuration());
        Assertions.assertEquals(LocalDate.of(2021, 1, 1), updatedFilm.getReleaseDate());
        Assertions.assertEquals("G", updatedFilm.getMpa().getName());
    }

    @Test
    public void testUserAddLikeToFilmAndGetPopularFilms() {
        Film film = Film.builder()
                .name("1+1")
                .description("Нету ручек, нет конфеток!")
                .duration(100)
                .releaseDate(LocalDate.of(2012, 5, 11))
                .mpa(mpaRatingDaoImpl.getMpaRatingById(4))
                .build();
        Film createdFilm = filmDbStorage.create(film);
        Film film2 = Film.builder()
                .name("1+2")
                .description("Some description")
                .duration(5)
                .releaseDate(LocalDate.of(2021, 1, 1))
                .mpa(mpaRatingDaoImpl.getMpaRatingById(1))
                .build();
        Film createdFilm2 = filmDbStorage.create(film2);
        User user = User.builder()
                .name("Tester")
                .email("mail@etcdev.ru")
                .login("tester")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser = userDbStorage.create(user);
        filmDbStorage.addUserLike(createdFilm.getId(), createdUser.getId());
        Film lastMostPopularFilm = filmDbStorage.getPopularFilms(1).get(0);
        Assertions.assertEquals(createdFilm.getId(), lastMostPopularFilm.getId());
        filmDbStorage.addUserLike(createdFilm2.getId(), createdUser.getId());
        User user2 = User.builder()
                .name("Tester2")
                .email("mai2@etcdev.ru")
                .login("tester2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser2 = userDbStorage.create(user2);
        filmDbStorage.addUserLike(createdFilm2.getId(), createdUser2.getId());
        Film newMostPopularFilm = filmDbStorage.getPopularFilms(2).get(0);
        Assertions.assertEquals(createdFilm2.getId(), newMostPopularFilm.getId());
        filmDbStorage.removeUserLike(createdFilm2.getId(), createdUser2.getId());
        filmDbStorage.removeUserLike(createdFilm2.getId(), createdUser.getId());
        filmDbStorage.removeUserLike(createdFilm.getId(), createdUser.getId());
    }

    @Test
    public void testUserRemoveLikeToFilmAndGetPopularFilms() {
        Film film = Film.builder()
                .name("1+1")
                .description("Нету ручек, нет конфеток!")
                .duration(100)
                .releaseDate(LocalDate.of(2012, 5, 11))
                .mpa(mpaRatingDaoImpl.getMpaRatingById(4))
                .build();
        Film createdFilm = filmDbStorage.create(film);

        Film film2 = Film.builder()
                .name("1+2")
                .description("Some description")
                .duration(5)
                .releaseDate(LocalDate.of(2021, 1, 1))
                .mpa(mpaRatingDaoImpl.getMpaRatingById(1))
                .build();
        Film createdFilm2 = filmDbStorage.create(film2);

        User user = User.builder()
                .name("Tester")
                .email("mail@etcdev.ru")
                .login("tester")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser = userDbStorage.create(user);

        User user2 = User.builder()
                .name("Tester2")
                .email("mai2@etcdev.ru")
                .login("tester2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser2 = userDbStorage.create(user2);

        filmDbStorage.addUserLike(createdFilm.getId(), createdUser.getId());
        filmDbStorage.addUserLike(createdFilm.getId(), createdUser2.getId());
        filmDbStorage.addUserLike(createdFilm2.getId(), createdUser2.getId());
        Film lastMostPopularFilm = filmDbStorage.getPopularFilms(1).get(0);
        Assertions.assertEquals(createdFilm.getId(), lastMostPopularFilm.getId());
        filmDbStorage.removeUserLike(createdFilm.getId(), createdUser.getId());
        filmDbStorage.removeUserLike(createdFilm.getId(), createdUser2.getId());
        Film newMostPopularFilm = filmDbStorage.getPopularFilms(1).get(0);
        Assertions.assertEquals(createdFilm2.getId(), newMostPopularFilm.getId());
    }

    @Test
    public void testGetFilmGenres() {
        Film film = Film.builder()
                .name("1+1")
                .description("Нету ручек, нет конфеток!")
                .duration(100)
                .releaseDate(LocalDate.of(2012, 5, 11))
                .mpa(mpaRatingDaoImpl.getMpaRatingById(5))
                .build();
        film.getGenres().add(genreDaoImpl.getGenreById(1));
        Film createdFilm = filmDbStorage.create(film);
        Genre gotGenre = filmDbStorage.getFilmGenresByFilmId(createdFilm.getId()).get(0);
        Assertions.assertEquals(1, gotGenre.getId());
        Assertions.assertEquals("Комедия", gotGenre.getName());
    }

    @Test
    public void testRemoveOneFilmGetById() {
        Film film = Film.builder()
                .name("1+2")
                .description("Нету ручек, нет конфеток!")
                .duration(100)
                .releaseDate(LocalDate.of(2012, 5, 11))
                .mpa(mpaRatingDaoImpl.getMpaRatingById(1))
                .build();
        Film createdFilm = filmDbStorage.create(film);
        filmDbStorage.removeFilmById(createdFilm.getId());
        Assertions.assertTrue(filmDbStorage.findAll().isEmpty());
    }

}
