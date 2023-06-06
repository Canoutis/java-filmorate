package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewDaoImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDaoImpl reviewDao;

    public Review createReview(Review review) {
        return reviewDao.createReview(review);
    }

    public Review updateReview(Review review) {
        return reviewDao.updateReview(review);
    }

    public void removeReview(Integer reviewId) {
        reviewDao.removeReview(reviewId);
    }

    public Review findReview(Integer reviewId) {
        return reviewDao.findReview(reviewId);
    }

    public List<Review> findReviews(Integer filmId, Integer limit) {
        return reviewDao.findReviews(filmId, limit);
    }

    public void addLikeToReview(Integer reviewId, Integer userId) {
        reviewDao.addLikeToReview(reviewId, userId);
    }

    public void addLDislikeToReview(Integer reviewId, Integer userId) {
        reviewDao.addLDislikeToReview(reviewId, userId);
    }

    public void removeLikeFromReview(Integer reviewId, Integer userId) {
        reviewDao.removeLikeFromReview(reviewId, userId);
    }

    public void removeDislikeFromReview(Integer reviewId, Integer userId) {
        reviewDao.removeDislikeFromReview(reviewId, userId);
    }
}
