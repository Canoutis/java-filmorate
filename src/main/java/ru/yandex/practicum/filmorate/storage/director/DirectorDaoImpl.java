package ru.yandex.practicum.filmorate.storage.director;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> findAll() {
        return jdbcTemplate.query("select * from director", DirectorDaoImpl::makeDirector);
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("select * from director where director_id = ?", id);

        if (directorRows.next()) {
            Director director = new Director(
                    directorRows.getInt("director_id"),
                    directorRows.getString("name"));

            log.info("Найден режиссер: {} {}", director.getId(), director.getName());

            return Optional.of(director);
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
                "name = ?";
        int response = jdbcTemplate.update(sqlQuery,
                director.getName());
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


    static Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(
                rs.getInt("director_id"),
                rs.getString("name")
        );
    }
}
