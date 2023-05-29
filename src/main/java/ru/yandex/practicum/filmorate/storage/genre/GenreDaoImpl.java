package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genre");
        List<Genre> genreList = new ArrayList<>();
        while (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getInt("genre_id"),
                    genreRows.getString("name"));
            genreList.add(genre);
        }
        return genreList;
    }

    @Override
    public Genre getGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id=?", id);
        if (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getInt("genre_id"),
                    genreRows.getString("name"));

            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());
            return genre;
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException(
                    String.format("Ошибка получения жанра. Жанр не найден! Id=%s", id));
        }
    }
}
