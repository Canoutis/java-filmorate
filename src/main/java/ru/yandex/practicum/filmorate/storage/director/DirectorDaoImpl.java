package ru.yandex.practicum.filmorate.storage.director;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> findAll() {
        return jdbcTemplate.query("select * from director", this::makeDirector);
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        List<Director> directors = jdbcTemplate.query("select * from director where director_id = ?", this::makeDirector, id);
        if (!directors.isEmpty()) {
            log.info("Найден режиссер: {} {}", directors.get(0).getId(), directors.get(0).getName());
            return Optional.of(directors.get(0));
        } else {
            log.info("Режиссер с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException(String.format("Режиссер с идентификатором %d не найден.", id));
        }
    }

    @Override
    public Optional<Director> create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");
        int directorId = simpleJdbcInsert.executeAndReturnKey(Collections.singletonMap("name", director.getName())).intValue();
        return getDirectorById(directorId);
    }

    @Override
    public Optional<Director> update(Director director) {
        String sqlQuery = "update director set " +
                "name = ? " +
                "where director_id = ? ";
        int response = jdbcTemplate.update(sqlQuery,
                director.getName(), director.getId());
        if (response == 1) {
            return getDirectorById(director.getId());
        } else {
            log.info("Режиссер с идентификатором {} не изменен.", director.getId());
            throw new ObjectUpdateException(
                    String.format("Ошибка обновления режиссера! Id=%d", director.getId()));
        }
    }

    @Override
    public void removeDirectorById(int id) {
        String sqlQuery = "delete from director where director_id = ?";
        int response = jdbcTemplate.update(sqlQuery, id);
        if (response != 1) {
            log.info("Режиссер с идентификатором {} не удален!", id);
            throw new ObjectUpdateException(
                    String.format("Режиссер с идентификатором %d не удален!", id));
        }
    }


    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(
                rs.getInt("director_id"),
                rs.getString("name")
        );
    }
}
