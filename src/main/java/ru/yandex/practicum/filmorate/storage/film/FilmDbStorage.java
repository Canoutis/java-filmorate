package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDaoImpl;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDaoImpl;
import ru.yandex.practicum.filmorate.utils.Constant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingDaoImpl mpaRatingDaoImpl;
    private final GenreDaoImpl genreDaoImpl;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaRatingDaoImpl mpaRatingDaoImpl, GenreDaoImpl genreDaoImpl) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRatingDaoImpl = mpaRatingDaoImpl;
        this.genreDaoImpl = genreDaoImpl;
    }

    @Override
    public List<Film> findAll() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film");
        List<Film> filmList = new ArrayList<>();
        // обрабатываем результат выполнения запроса
        while (filmRows.next()) {
            Film film = new Film(
                    filmRows.getInt("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate(),
                    filmRows.getInt("duration"),
                    mpaRatingDaoImpl.getMpaRatingById(filmRows.getInt("rating_id"))
            );
            film.getGenres().addAll(getFilmGenresByFilmId(film.getId()));
            filmList.add(film);
        }
        return filmList;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.getGenres().addAll(addFilmGenresByFilmId(filmId, film.getGenres()));
        film = getFilmById(filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update film set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "where film_id = ?";
        int response = jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate().format(Constant.dateFormatter)
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId()
        );
        if (response == 1) {
            removeFilmGenresByFilmId(film.getId(), film.getGenres());
            addFilmGenresByFilmId(film.getId(), film.getGenres());
            return getFilmById(film.getId());
        } else {
            log.info("Фильм с идентификатором {} не изменен.", film.getId());
            throw new ObjectUpdateException(
                    String.format("Ошибка обновления фильма! Id=%s", film.getId()));
        }
    }

    @Override
    public Film getFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where film_id=?", id);
        if (filmRows.next()) {
            Film film = new Film(
                    filmRows.getInt("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate(),
                    filmRows.getInt("duration"),
                    mpaRatingDaoImpl.getMpaRatingById(filmRows.getInt("rating_id")));
            film.getGenres().addAll(getFilmGenresByFilmId(film.getId()));
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException(
                    String.format("Ошибка получения фильма. Фильм не найден! Id=%s", id));
        }
    }

    public List<Genre> getFilmGenresByFilmId(int filmId) {
        SqlRowSet filmGenreRows = jdbcTemplate.queryForRowSet("select * from film_genre where film_id=?", filmId);
        List<Genre> filmGenres = new ArrayList<>();
        while (filmGenreRows.next()) {
            filmGenres.add(genreDaoImpl.getGenreById(filmGenreRows.getInt("genre_id")));
        }
        return filmGenres;
    }

    private Collection<Genre> addFilmGenresByFilmId(int filmId, Collection<Genre> filmGenres) {
        filmGenres.forEach(genre -> addFilmGenreByFilmId(filmId, genre));
        return getFilmGenresByFilmId(filmId);
    }

    private void removeFilmGenresByFilmId(int filmId, Collection<Genre> filmGenres) {
        String sqlQuery = "delete from film_genre " +
                "where film_id = :filmId and genre_id not in (:genreIds)";
        List<Integer> genreIds = filmGenres.stream()
                .map(Genre::getId)
                .collect(Collectors.toList());

        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        params.put("genreIds", genreIds);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedParameterJdbcTemplate.update(sqlQuery, params);
    }

    private void addFilmGenreByFilmId(int filmId, Genre genre) {
        String sqlQuery = "insert into film_genre(film_id, genre_id) " +
                "select ?, ?" +
                "where not exists (select 1 from film_genre where film_id = ? and genre_id = ?)";
        jdbcTemplate.update(sqlQuery,
                filmId, genre.getId(),
                filmId, genre.getId());
    }

    @Override
    public Film addUserLike(int filmId, int userId) {
        String sqlQuery = "insert into likes(film_id, user_id) " +
                "select ?, ? " +
                "where not exists (select 1 from likes where film_id = ? and user_id = ?)";
        jdbcTemplate.update(sqlQuery,
                filmId, userId,
                filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "select f.*, (select count(*) from likes where film_id = f.film_id) as like_count " +
                        "from film f " +
                        "order by like_count desc " +
                        "limit ?", count);
        List<Film> filmList = new ArrayList<>();
        while (filmRows.next()) {
            Film film = new Film(
                    filmRows.getInt("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate(),
                    filmRows.getInt("duration"),
                    mpaRatingDaoImpl.getMpaRatingById(filmRows.getInt("rating_id"))
            );
            film.getGenres().addAll(getFilmGenresByFilmId(film.getId()));
            filmList.add(film);
        }
        return filmList;
    }

    @Override
    public Film removeUserLike(int filmId, int userId) {
        String sqlQuery = "delete from likes " +
                "where film_id = ? and user_id = ?";
        int removedRowsNum = jdbcTemplate.update(sqlQuery,
                filmId,
                userId);
        if (removedRowsNum == 0) {
            log.info("Ошибка обновления фильма! Не найден лайк FilmId={}, UserId={}", filmId, userId);
            throw new ObjectNotFoundException(
                    String.format("Ошибка обновления фильма! Не найден лайк FilmId=%s, UserId=%s", filmId, userId)
            );
        }
        return getFilmById(filmId);
    }
}
