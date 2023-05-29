package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.ArrayList;
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
        SqlRowSet mpaRatingRows = jdbcTemplate.queryForRowSet("select * from mpa_rating");
        List<MpaRating> mpaRatingList = new ArrayList<>();
        while (mpaRatingRows.next()) {
            MpaRating mpaRating = new MpaRating(
                    mpaRatingRows.getInt("rating_id"),
                    mpaRatingRows.getString("rating_name"));
            mpaRatingList.add(mpaRating);
        }
        return mpaRatingList;
    }

    @Override
    public MpaRating getMpaRatingById(int id) {
        SqlRowSet mpaRatingRows = jdbcTemplate.queryForRowSet("select * from mpa_rating where rating_id=?", id);
        if (mpaRatingRows.next()) {
            MpaRating mpaRating = new MpaRating(
                    mpaRatingRows.getInt("rating_id"),
                    mpaRatingRows.getString("rating_name"));

            log.info("Найден рейтинг: {} {}", mpaRating.getId(), mpaRating.getName());
            return mpaRating;
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException(
                    String.format("Ошибка получения рейтинга. Рейтинг не найден! Id=%s", id));
        }
    }
}
