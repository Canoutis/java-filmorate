package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MpaRatingDaoImpl implements MpaRatingDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaRatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> findAll() {
        return jdbcTemplate.query("select * from mpa_rating", MpaRatingDaoImpl::makeMpaRating);
    }

    @Override
    public MpaRating getMpaRatingById(int id) {
        List<MpaRating> ratings = jdbcTemplate.query("select * from mpa_rating where rating_id=?", MpaRatingDaoImpl::makeMpaRating, id);
        if (ratings.size() == 1) {
            log.info("Найден рейтинг: {} {}", ratings.get(0), ratings.get(0));
            return ratings.get(0);
        } else {
            if (ratings.isEmpty()) {
                log.info("Рейтинг с идентификатором {} не найден.", id);
            } else {
                log.warn("Получено неожиданное количество записей ({}) для рейтинга с идентификатором {}.", ratings.size(), id);
            }
            throw new ObjectNotFoundException(
                    String.format("Ошибка получения рейтинга. Рейтинг не найден! Id=%d", id));
        }
    }

    static MpaRating makeMpaRating(ResultSet rs, int rowNum) throws SQLException {
        return new MpaRating(
                rs.getInt("rating_id"),
                rs.getString("rating_name"));
    }
}
