package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.utils.EventType;
import ru.yandex.practicum.filmorate.utils.Operation;

import java.util.LinkedList;
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
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue());
        userDbStorage.addEvent(new Event(review.getUserId(), EventType.REVIEW, Operation.ADD, review.getReviewId()));
        return getReviewById(review.getReviewId());
    }

    @Override
    public Review updateReview(Review review) {
        var reviewEx = getReviewById(review.getReviewId());
        sqlQuery = "update review set content = ?, is_positive = ? where review_id = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());
        userDbStorage.addEvent(new Event(reviewEx.getUserId(), EventType.REVIEW, Operation.UPDATE, review.getReviewId()));
        return getReviewById(review.getReviewId());
    }

    @Override
    public void removeReview(Integer reviewId) {
        var review = getReviewById(reviewId);
        userDbStorage.addEvent(new Event(review.getUserId(), EventType.REVIEW, Operation.REMOVE, review.getReviewId()));
        sqlQuery = "delete from review where review_id = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        sqlQuery = "select * from review where review_id = ?";
        var rowMapper = BeanPropertyRowMapper.newInstance(Review.class);
        var reviews = jdbcTemplate.query(sqlQuery, rowMapper, reviewId);
        if (reviews.size() == 1)
            return reviews.get(0);
        else {
            log.info("Отзыв с идентификатором {} не найден.", reviewId);
            throw new ObjectNotFoundException(
                    String.format("Ошибка получения отзыва. Отзыв не найден! Id=%d", reviewId));
        }
    }

    @Override
    public List<Review> findReviews(Integer filmId, Integer count) {
        var rowMapper = BeanPropertyRowMapper.newInstance(Review.class);
        if (filmId != null) {
            sqlQuery = "select * from review where film_id = ? order by useful desc, review_id limit ?";
            return new LinkedList<>(jdbcTemplate.query(sqlQuery, rowMapper, filmId, count));
        } else {
            sqlQuery = "select * from review order by useful desc";
            return new LinkedList<>(jdbcTemplate.query(sqlQuery, rowMapper));
        }
    }

    @Override
    public void addLikeToReview(Integer reviewId, Integer userId) {
        addFeedback(reviewId, userId, true);
    }

    @Override
    public void addLDislikeToReview(Integer reviewId, Integer userId) {
        addFeedback(reviewId, userId, false);
    }

    @Override
    public void removeLikeFromReview(Integer reviewId, Integer userId) {
        removeFeedback(reviewId, userId, true);
    }

    @Override
    public void removeDislikeFromReview(Integer reviewId, Integer userId) {
        removeFeedback(reviewId, userId, false);
    }

    private void addFeedback(Integer reviewId, Integer userId, Boolean isUseful) {
        getReviewById(reviewId);
        userDbStorage.getUserById(userId);
        sqlQuery = "insert into feedback (review_id, user_id, is_useful) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId, isUseful);
        changeUseful(reviewId, isUseful ? 1 : -1);
    }

    private void removeFeedback(Integer reviewId, Integer userId, Boolean isUseful) {
        getReviewById(reviewId);
        userDbStorage.getUserById(userId);
        sqlQuery = "delete from feedback where user_id = ? and user_id = ? and is_useful = ?";
        jdbcTemplate.update(sqlQuery, reviewId, userId, isUseful);
        changeUseful(reviewId, isUseful ? -1 : 1);
    }

    private void changeUseful(Integer reviewId, Integer delta) {
        var review = getReviewById(reviewId);
        sqlQuery = "update review set useful = ? where review_id = ?";
        jdbcTemplate.update(sqlQuery, review.getUseful() + delta, reviewId);
    }
}
