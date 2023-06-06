package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Slf4j
@Component
public class ReviewDaoImpl implements ReviewDao {
    @Override
    public Review createReview(Review review) {
        return null;
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
    public List<Review> findReviews(Integer filmId, Integer limit) {
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
