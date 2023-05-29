package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoImplTest {

    private final GenreDaoImpl genreDaoImpl;

    @Test
    public void testOneGenreGetById() {
        Genre comedy = genreDaoImpl.getGenreById(1);
        Assertions.assertEquals("Комедия", comedy.getName());
    }

    @Test
    public void testOneUserCreate() {
        List<Genre> genres = genreDaoImpl.findAll();
        Assertions.assertEquals(6, genres.size());
    }
}
