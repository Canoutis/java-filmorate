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
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue());
        review.setUseful(0);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        return null;
    }

    @Override
    public void removeReview(Integer reviewId) {

    }

    @Override
    public Review findReview(Integer reviewId) {
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
