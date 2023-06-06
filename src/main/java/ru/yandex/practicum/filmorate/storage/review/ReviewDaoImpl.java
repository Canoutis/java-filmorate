package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDaoImpl implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

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
        userDbStorage.getUserById(review.getUserId());
        filmDbStorage.getFilmById(review.getFilmId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public void removeReview(Integer reviewId) {

    }

    @Override
    public Review getReviewById(Integer reviewId) {
        return null;
    }

    @Override
    public List<Review> findReviews(Integer filmId, Integer count) {
        return null;
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
}
