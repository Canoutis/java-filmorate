package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.utils.Constant;
import ru.yandex.practicum.filmorate.utils.EventType;
import ru.yandex.practicum.filmorate.utils.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorDao directorDao;
    private final UserDbStorage userDbStorage;

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query("select * from film f inner join mpa_rating m where f.rating_id = m.rating_id", this::makeFilm);
        Map<Integer, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        for (Film film : films) {
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                Objects.requireNonNull(rs.getDate("release_date")).toLocalDate(),
                rs.getInt("duration"),
                new MpaRating(rs.getInt("rating_id"), rs.getString("rating_name"))
        );
    }

    private Map<Integer, List<Genre>> loadFilmsGenres(List<Film> films) {
        return loadFilmsGenresByFilmsIds(films.stream().map(Film::getId).collect(Collectors.toList()));
    }

    private List<Genre> loadFilmGenresByFilmId(int filmId) {
        return loadFilmsGenresByFilmsIds(List.of(filmId)).getOrDefault(filmId, Collections.emptyList());
    }

    private Map<Integer, List<Genre>> loadFilmsGenresByFilmsIds(List<Integer> filmsIds) {
        Map<Integer, List<Genre>> filmGenresMap = new HashMap<>();
        if (!filmsIds.isEmpty()) {
            String questionMarks = String.join(", ", Collections.nCopies(filmsIds.size(), "?"));
            String query = "select fg.film_id, g.genre_id, g.name " +
                    "from genre g inner " +
                    "join film_genre fg on fg.genre_id = g.genre_id " +
                    "where fg.film_id in (" + questionMarks + ")";
            jdbcTemplate.query(query, rs -> {
                int filmId = rs.getInt("film_id");
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
                filmGenresMap.computeIfAbsent(filmId, key -> new ArrayList<>()).add(genre);
            }, filmsIds.toArray());
        }
        return filmGenresMap;
    }

    private Map<Integer, List<Director>> loadFilmsDirectors(List<Film> films) {
        return loadFilmsDirectorsByFilmsIds(films.stream().map(Film::getId).collect(Collectors.toList()));
    }

    private List<Director> loadFilmDirectorsByFilmId(int filmId) {
        return loadFilmsDirectorsByFilmsIds(List.of(filmId)).getOrDefault(filmId, Collections.emptyList());
    }

    private Map<Integer, List<Director>> loadFilmsDirectorsByFilmsIds(List<Integer> filmsIds) {
        Map<Integer, List<Director>> filmDirectorsMap = new HashMap<>();
        if (!filmsIds.isEmpty()) {
            String questionMarks = String.join(", ", Collections.nCopies(filmsIds.size(), "?"));
            String query = "select fd.film_id, d.director_id, d.name " +
                    "from director d inner " +
                    "join film_director fd on fd.director_id = d.director_id " +
                    "where fd.film_id in (" + questionMarks + ")";
            jdbcTemplate.query(query, rs -> {
                int filmId = rs.getInt("film_id");
                Director director = new Director(rs.getInt("director_id"), rs.getString("name"));
                filmDirectorsMap.computeIfAbsent(filmId, key -> new ArrayList<>()).add(director);
            }, filmsIds.toArray());
        }
        return filmDirectorsMap;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.getGenres().addAll(addFilmGenresByFilmId(filmId, film.getGenres()));
        film.getDirectors().addAll(addFilmDirectorsByFilmId(filmId, film.getDirectors()));
        film = getFilmById(filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update film set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "where film_id = ?";
        int response = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().format(Constant.dateFormatter),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (response == 1) {
            removeFilmGenresByFilmId(film.getId(), film.getGenres());
            addFilmGenresByFilmId(film.getId(), film.getGenres());

            removeFilmDirectorsByFilmId(film.getId(), film.getDirectors());
            addFilmDirectorsByFilmId(film.getId(), film.getDirectors());

            return getFilmById(film.getId());
        } else {
            log.info("Фильм с идентификатором {} не изменен.", film.getId());
            throw new ObjectUpdateException(
                    String.format("Ошибка обновления фильма! Id=%d", film.getId()));
        }
    }

    @Override
    public Film getFilmById(int id) {
        List<Film> films = jdbcTemplate.query("select * from film f inner join mpa_rating m where f.rating_id = m.rating_id and f.film_id=?", this::makeFilm, id);
        Map<Integer, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        if (!films.isEmpty()) {
            Film film = films.get(0);
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException(
                    String.format("Ошибка получения фильма. Фильм не найден! Id=%d", id));
        }
    }


    private Collection<Genre> addFilmGenresByFilmId(int filmId, Collection<Genre> filmGenres) {
        filmGenres.forEach(genre -> addFilmGenreByFilmId(filmId, genre));
        return loadFilmGenresByFilmId(filmId);
    }

    private Collection<Director> addFilmDirectorsByFilmId(int filmId, Collection<Director> filmDirectors) {
        filmDirectors.forEach(director -> addFilmDirectorByFilmId(filmId, director));
        return loadFilmDirectorsByFilmId(filmId);
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

    private void removeFilmDirectorsByFilmId(int filmId, Collection<Director> filmDirectors) {
        String sqlQuery = "delete from film_director " +
                "where film_id = :filmId and director_id not in (:directorIds)";
        List<Integer> directorsIds = filmDirectors.stream()
                .map(Director::getId)
                .collect(Collectors.toList());

        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        params.put("directorIds", directorsIds);

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

    private void addFilmDirectorByFilmId(int filmId, Director director) {
        String sqlQuery = "insert into film_director(film_id, director_id) " +
                "select ?, ?" +
                "where not exists (select 1 from film_director where film_id = ? and director_id = ?)";
        jdbcTemplate.update(sqlQuery,
                filmId, director.getId(),
                filmId, director.getId());
    }

    @Override
    public Film addUserLike(int filmId, int userId) {
        String sqlQuery = "insert into likes(film_id, user_id) " +
                "select ?, ? " +
                "where not exists (select 1 from likes where film_id = ? and user_id = ?)";
        jdbcTemplate.update(sqlQuery,
                filmId, userId,
                filmId, userId);
        userDbStorage.addEvent(new Event(userId, EventType.LIKE, Operation.ADD, filmId));
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "select f.*, mr.rating_name " +
                "from film as f " +
                "inner join mpa_rating as mr using(rating_id) " +
                "left outer join likes as l using(film_id) " +
                "group by f.film_id " +
                "order by count(l.user_id) desc, f.film_id " +
                "limit ?";
        log.info("Получаем топ {} самых популярных фильмов.", count);
        return getPopularBySql(sql, count);
    }

    @Override
    public List<Film> getPopularByYear(int releaseYear, int count) {
        String sql = "select f.*, mpa_rating.rating_name " +
                "from film AS f " +
                "left join likes AS l USING(film_id) " +
                "inner join mpa_rating USING(rating_id)" +
                "where extract(year from release_date) = ? " +
                "group by f.film_id, f.name " +
                "order by count(l.user_id) desc, f.film_id " +
                "limit ?";
        log.info("Получаем топ {} самых популярных фильмов c датой релиза: {} ", count, releaseYear);
        return getPopularBySql(sql, releaseYear, count);
    }

    @Override
    public List<Film> getPopularByGenre(int genreId, int count) {
        String sql = "select f.*, mpa_rating.rating_name " +
                "from film as f " +
                "left join likes as l using(film_id) " +
                "join film_genre as fg using(film_id) " +
                "inner join mpa_rating using(rating_id)" +
                "where fg.genre_id = ? " +
                "group by f.film_id, f.name " +
                "order by count(l.user_id) desc, f.film_id " +
                "limit ?";
        log.info("Получаем топ {} самых популярных фильмов. ID жанра :{}.", count, genreId);
        return getPopularBySql(sql, genreId, count);
    }

    @Override
    public List<Film> getPopularByGenreAndYear(int genreId, int releaseYear, int count) {
        String sql = "select f.*, mpa_rating.rating_name " +
                "from film as f " +
                "left join likes as l using(film_id) " +
                "join film_genre as fg using(film_id) " +
                "inner join mpa_rating using(rating_id)" +
                "where fg.genre_id = ? and " +
                "extract(year from release_date) = ? " +
                "group by f.film_id, f.name " +
                "order by count(l.user_id) desc, f.film_id " +
                "limit ?";
        log.info("Получаем топ {} самых популярных фильмов c ID жанра :{} и датой релиза {}. ", count, genreId, releaseYear);
        return getPopularBySql(sql, genreId, releaseYear, count);
    }

    private List<Film> getPopularBySql(String sql, Object... args) {
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, args);
        Map<Integer, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        for (Film film : films) {
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
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
                    String.format("Ошибка обновления фильма! Не найден лайк FilmId=%d, UserId=%d", filmId, userId)
            );
        }
        userDbStorage.addEvent(new Event(userId, EventType.LIKE, Operation.REMOVE, filmId));
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getFilmsByUserId(int userId) {
        String sql = "SELECT f.*, m.rating_id, m.rating_name " +
                "FROM film f " +
                "JOIN likes l ON l.film_id = f.film_id " +
                "JOIN mpa_rating m ON f.rating_id = m.rating_id " +
                "WHERE l.user_id = ?";
        return jdbcTemplate.query(sql, this::makeFilm, userId);
    }

    @Override
    public List<Film> getFilmsByFriendId(int friendId) {
        String sql = "SELECT f.*, m.rating_id, m.rating_name " +
                "FROM film f " +
                "JOIN likes l ON l.film_id = f.film_id " +
                "JOIN mpa_rating m ON f.rating_id = m.rating_id " +
                "WHERE l.user_id = ?";
        return jdbcTemplate.query(sql, this::makeFilm, friendId);
    }

    @Override
    public List<Film> getDirectorFilmsSortedByYear(int directorId) {
        directorDao.getDirectorById(directorId);
        List<Film> films = jdbcTemplate.query("select * " +
                "from film_director fd " +
                "inner join film f on fd.film_id = f.film_id " +
                "inner join mpa_rating m on m.rating_id = f.rating_id " +
                "where fd.director_id=? " +
                "order by f.release_date", this::makeFilm, directorId);
        Map<Integer, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        for (Film film : films) {
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }

    @Override
    public List<Film> getDirectorFilmsSortedByLikes(int directorId) {
        directorDao.getDirectorById(directorId);
        List<Film> films = jdbcTemplate.query("select *, (select count(*) from likes where film_id = f.film_id) as like_count " +
                "from film_director fd " +
                "inner join film f on fd.film_id = f.film_id " +
                "inner join mpa_rating m on m.rating_id = f.rating_id " +
                "where fd.director_id=? " +
                "order by like_count desc", this::makeFilm, directorId);
        Map<Integer, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        for (Film film : films) {
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }


    public void removeFilmById(int filmId) {
        getFilmById(filmId);
        String deleteFilmQuery = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(deleteFilmQuery, filmId);
        log.debug("Фильм с ID = {} удален.", filmId);
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        var sqlQuery = "SELECT *\n" +
                "FROM FILM f \n" +
                "JOIN MPA_RATING AS r ON r.RATING_ID = f.RATING_ID\n" +
                "WHERE FILM_ID IN (SELECT l2.FILM_ID \n" +
                "FROM LIKES AS l2 \n" +
                "WHERE l2.USER_ID = (SELECT USER_ID\n" +
                "FROM LIKES l \n" +
                "WHERE USER_ID != " + userId + " AND FILM_ID IN (SELECT FILM_ID FROM LIKES l2 WHERE USER_ID = " + userId + ")\n" +
                "GROUP BY USER_ID \n" +
                "ORDER BY COUNT(FILM_ID) DESC \n" +
                "LIMIT 1) AND l2.FILM_ID NOT IN \n" +
                "(SELECT l.FILM_ID \n" +
                "FROM LIKES AS l \n" +
                "WHERE l.USER_ID = " + userId + "))";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        Map<Integer, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        for (Film film : films) {
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }

    @Override
    public List<Film> findByTitleContaining(String query) {
        String sql = "SELECT F.*, m.rating_id, m.rating_name FROM FILM F " +
                "JOIN mpa_rating m ON F.rating_id = m.rating_id " +
                "WHERE LOWER(NAME) LIKE ?";
        String param = "%" + query.toLowerCase() + "%";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, param);
        Map<Integer, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        for (Film film : films) {
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }

    @Override
    public List<Film> findByDirectorContaining(String query) {
        String sql = "SELECT F.*, m.rating_id, m.rating_name FROM FILM F" +
                " JOIN FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID " +
                "JOIN DIRECTOR D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "JOIN mpa_rating m ON F.rating_id = m.rating_id" +
                " WHERE D.NAME LIKE ?";
        String param = "%" + query.toLowerCase() + "%";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, param);
        Map<Integer, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        for (Film film : films) {
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }

    @Override
    public List<Film> findByTitleContainingOrDirectorContaining(String titleQuery, String directorQuery) {
        String sql = "SELECT F.*, m.rating_id, m.rating_name, COUNT(L.USER_ID) AS LIKE_COUNT " +
                "FROM FILM F " +
                "LEFT JOIN FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTOR D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "JOIN mpa_rating m ON F.rating_id = m.rating_id " +
                "LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID " +
                "WHERE LOWER(F.NAME) LIKE ? OR LOWER(D.NAME) LIKE ? " +
                "GROUP BY F.FILM_ID, M.RATING_ID, M.RATING_NAME " +
                "ORDER BY LIKE_COUNT DESC";
        String titleParam = "%" + titleQuery.toLowerCase() + "%";
        String directorParam = "%" + directorQuery.toLowerCase() + "%";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, titleParam, directorParam);
        Map<Integer, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        for (Film film : films) {
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }
}

