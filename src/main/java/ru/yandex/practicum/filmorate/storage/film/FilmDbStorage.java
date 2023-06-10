package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDao;
import ru.yandex.practicum.filmorate.utils.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private final MpaRatingDao mpaRatingDao;

    private final GenreDao genreDao;
    private final DirectorDao directorDao;

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
        List<Integer> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Integer, List<Genre>> filmGenresMap = new HashMap<>();
        if (!filmIds.isEmpty()) {
            String questionMarks = String.join(", ", Collections.nCopies(filmIds.size(), "?"));
            String query = "select fg.film_id, g.genre_id, g.name " +
                    "from genre g inner " +
                    "join film_genre fg on fg.genre_id = g.genre_id " +
                    "where fg.film_id in (" + questionMarks + ")";
            jdbcTemplate.query(query, rs -> {
                int filmId = rs.getInt("film_id");
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
                filmGenresMap.computeIfAbsent(filmId, key -> new ArrayList<>()).add(genre);
            }, filmIds.toArray());
        }
        return filmGenresMap;
    }

    private Map<Integer, List<Director>> loadFilmsDirectors(List<Film> films) {
        List<Integer> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Integer, List<Director>> filmDirectorsMap = new HashMap<>();
        if (!filmIds.isEmpty()) {
            String questionMarks = String.join(", ", Collections.nCopies(filmIds.size(), "?"));
            String query = "select fd.film_id, d.director_id, d.name " +
                    "from director d inner " +
                    "join film_director fd on fd.director_id = d.director_id " +
                    "where fd.film_id in (" + questionMarks + ")";
            jdbcTemplate.query(query, rs -> {
                int filmId = rs.getInt("film_id");
                Director director = new Director(rs.getInt("director_id"), rs.getString("name"));
                filmDirectorsMap.computeIfAbsent(filmId, key -> new ArrayList<>()).add(director);
            }, filmIds.toArray());
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

    public List<Genre> getFilmGenresByFilmId(int filmId) {
        SqlRowSet filmGenreRows = jdbcTemplate.queryForRowSet("select * from film_genre where film_id=?", filmId);
        List<Genre> filmGenres = new ArrayList<>();
        while (filmGenreRows.next()) {
            filmGenres.add(genreDao.getGenreById(filmGenreRows.getInt("genre_id")));
        }
        return filmGenres;
    }

    public List<Director> getFilmDirectorsByFilmId(int filmId) {
        SqlRowSet filmDirectorRows = jdbcTemplate.queryForRowSet("select * from film_director where film_id=?", filmId);
        List<Director> filmDirectors = new ArrayList<>();
        while (filmDirectorRows.next()) {
            Optional<Director> director = directorDao.getDirectorById(filmDirectorRows.getInt("director_id"));
            director.ifPresent(filmDirectors::add);
        }
        return filmDirectors;
    }

    private Collection<Genre> addFilmGenresByFilmId(int filmId, Collection<Genre> filmGenres) {
        filmGenres.forEach(genre -> addFilmGenreByFilmId(filmId, genre));
        return getFilmGenresByFilmId(filmId);
    }

    private Collection<Director> addFilmDirectorsByFilmId(int filmId, Collection<Director> filmDirectors) {
        filmDirectors.forEach(director -> addFilmDirectorByFilmId(filmId, director));
        return getFilmDirectorsByFilmId(filmId);
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
                    mpaRatingDao.getMpaRatingById(filmRows.getInt("rating_id"))
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
                    String.format("Ошибка обновления фильма! Не найден лайк FilmId=%d, UserId=%d", filmId, userId)
            );
        }
        return getFilmById(filmId);
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
    public List<Film> findByTitleContaining(String query) {
        String sql = "SELECT * FROM FILM WHERE LOWER(NAME) LIKE ?";
        String param = "%" + query.toLowerCase() + "%";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> filmMapper(rs), param);
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
        String sql = "SELECT F.* FROM FILM F INNER JOIN FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID " +
                "INNER JOIN DIRECTOR D ON FD.DIRECTOR_ID = D.DIRECTOR_ID WHERE LOWER(D.NAME) LIKE ?";
        String param = "%" + query.toLowerCase() + "%";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> filmMapper(rs), param);
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
        String sql = "SELECT F.* FROM FILM F LEFT JOIN FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTOR D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE LOWER(F.NAME) LIKE ? OR LOWER(D.NAME) LIKE ?";
        String titleParam = "%" + titleQuery.toLowerCase() + "%";
        String directorParam = "%" + directorQuery.toLowerCase() + "%";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> filmMapper(rs), titleParam, directorParam);
        Map<Integer, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Integer, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        for (Film film : films) {
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
        }
        films.sort(Comparator.comparingInt(film -> film.getLikes().size()));
        Collections.reverse(films);
        return films;
    }

    private Film filmMapper(ResultSet rs) throws SQLException {
        return new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                Objects.requireNonNull(rs.getDate("release_date")).toLocalDate(),
                rs.getInt("duration"),
                mpaRatingDao.getMpaRatingById(rs.getInt("rating_id"))
        );
    }
}
