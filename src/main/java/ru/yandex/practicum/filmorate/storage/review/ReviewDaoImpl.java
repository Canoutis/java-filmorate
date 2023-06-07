package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDaoImpl implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private String sqlQuery;

    @Override
    public Review createReview(Review review) {
        userDbStorage.getUserById(review.getUserId());
        filmDbStorage.getFilmById(review.getFilmId());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review")
                .usingGeneratedKeyColumns("review_id");
        return getReviewById(simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue());
    }

    @Override
    public Review updateReview(Review review) {
        getReviewById(review.getReviewId());
        sqlQuery = "update review set content = ?, is_positive = ? where review_id = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public void removeReview(Integer reviewId) {
        getReviewById(reviewId);
        sqlQuery = "delete from review where review_id = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        sqlQuery = "select * from review where review_id = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, reviewId);
        if (row.next()) {
            return Review.builder()
                    .reviewId(row.getInt("review_id"))
                    .content(row.getString("content"))
                    .isPositive(row.getBoolean("is_positive"))
                    .userId(row.getInt("user_id"))
                    .filmId(row.getInt("film_id"))
                    .useful(row.getInt("useful"))
                    .build();
        } else {
            log.info("Отзыв с идентификатором {} не найден.", reviewId);
            throw new ObjectNotFoundException(
                    String.format("Ошибка получения отзыва. Отзыв не найден! Id=%d", reviewId));
        }
    }

    @Override
    public List<Review> findReviews(Integer filmId, Integer count) {
        if (filmId != null) {
            sqlQuery = "select * from review film_id = ? order by useful limit ?";
            return jdbcTemplate.query(sqlQuery, this::makeReview, filmId, count);
        } else {
            sqlQuery = "select * from review order by useful limit ?";
            return jdbcTemplate.query(sqlQuery, this::makeReview, count);
        }
    }

    @Override
    public void addLikeToReview(Integer reviewId, Integer userId) {

    }

    @Override
    public void addLDislikeToReview(Integer reviewId, Integer userId) {

    }

    @Override
    public void removeLikeFromReview(Integer reviewId, Integer userId) {

    }

    @Override
    public void removeDislikeFromReview(Integer reviewId, Integer userId) {

    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
